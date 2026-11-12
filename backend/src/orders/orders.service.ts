import { Injectable, BadRequestException, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CreateOrderDto, UpdateOrderStatusDto } from './dto/create-order.dto';
import { OrderType, OrderStatus, PaymentStatus, PaymentMethod } from '@prisma/client';

@Injectable()
export class OrdersService {
  constructor(private readonly prisma: PrismaService) {}

  private generateOrderNumber(): string {
    const today = new Date();
    const dateStr = today.toISOString().slice(0, 10).replace(/-/g, '');
    const random = Math.floor(1000 + Math.random() * 9000);
    return `ORD-${dateStr}-${random}`;
  }

  async createOrder(dto: CreateOrderDto, confirmedByUserId?: string) {
    if (!dto.items || dto.items.length === 0) {
      throw new BadRequestException('Pesanan harus memiliki minimal 1 item produk');
    }

    let resolvedTableId = dto.tableId;

    if (dto.orderType === OrderType.DINE_IN) {
      if (!resolvedTableId && dto.tableNumber) {
        const table = await this.prisma.table.findUnique({
          where: { tableNumber: dto.tableNumber },
        });
        if (!table) {
          throw new BadRequestException(`Meja '${dto.tableNumber}' tidak ditemukan`);
        }
        resolvedTableId = table.id;
      }

      if (!resolvedTableId) {
        throw new BadRequestException('Pesanan Dine In wajib memilih atau memasukkan nomor meja');
      }
    }

    // Fetch product details & verify availability
    const productIds = dto.items.map((i) => i.productId);
    const products = await this.prisma.product.findMany({
      where: { id: { in: productIds } },
    });

    const productMap = new Map(products.map((p) => [p.id, p]));

    let calculatedTotal = 0;
    const orderDetailsData = [];

    for (const item of dto.items) {
      const product = productMap.get(item.productId);
      if (!product) {
        throw new NotFoundException(`Produk dengan ID ${item.productId} tidak ditemukan`);
      }
      if (!product.isAvailable) {
        throw new BadRequestException(`Produk '${product.name}' saat ini tidak tersedia`);
      }

      const subtotal = product.price * item.quantity;
      calculatedTotal += subtotal;

      orderDetailsData.push({
        productId: product.id,
        productName: product.name,
        price: product.price,
        quantity: item.quantity,
        notes: item.notes || null,
        subtotal,
      });
    }

    const orderNumber = this.generateOrderNumber();
    const isAlreadyPaid = dto.paymentStatus === 'PAID';

    const initialPaymentStatus = isAlreadyPaid ? PaymentStatus.PAID : PaymentStatus.PENDING;
    const initialOrderStatus = isAlreadyPaid ? OrderStatus.MENUNGGU_DIPROSES : OrderStatus.MENUNGGU_PEMBAYARAN;

    // Use Prisma Transaction
    return this.prisma.$transaction(async (tx) => {
      const order = await tx.order.create({
        data: {
          orderNumber,
          customerName: dto.customerName || 'Pelanggan',
          orderType: dto.orderType,
          tableId: dto.orderType === OrderType.DINE_IN ? resolvedTableId : null,
          paymentMethod: dto.paymentMethod,
          paymentStatus: initialPaymentStatus,
          orderStatus: initialOrderStatus,
          subtotal: calculatedTotal,
          total: calculatedTotal,
          notes: dto.notes || null,
          orderDetails: {
            create: orderDetailsData,
          },
        },
        include: {
          table: true,
          orderDetails: true,
          payments: true,
        },
      });

      if (isAlreadyPaid) {
        await tx.payment.create({
          data: {
            orderId: order.id,
            amount: calculatedTotal,
            paymentMethod: dto.paymentMethod,
            status: PaymentStatus.PAID,
            confirmedAt: new Date(),
            confirmedByUserId: confirmedByUserId || null,
          },
        });
      }

      return order;
    });
  }

  async findAll(status?: OrderStatus, paymentStatus?: PaymentStatus, date?: string) {
    const where: any = {};

    if (status) {
      where.orderStatus = status;
    }
    if (paymentStatus) {
      where.paymentStatus = paymentStatus;
    }
    if (date) {
      const startDate = new Date(date);
      startDate.setHours(0, 0, 0, 0);
      const endDate = new Date(date);
      endDate.setHours(23, 59, 59, 999);
      where.createdAt = {
        gte: startDate,
        lte: endDate,
      };
    }

    return this.prisma.order.findMany({
      where,
      include: {
        table: true,
        orderDetails: true,
        payments: true,
      },
      orderBy: { createdAt: 'desc' },
    });
  }

  async findOne(id: string) {
    const order = await this.prisma.order.findUnique({
      where: { id },
      include: {
        table: true,
        orderDetails: true,
        payments: {
          include: {
            confirmedByUser: {
              select: { id: true, name: true, email: true },
            },
          },
        },
      },
    });

    if (!order) {
      throw new NotFoundException('Pesanan tidak ditemukan');
    }

    return order;
  }

  async findByOrderNumber(orderNumber: string) {
    const order = await this.prisma.order.findUnique({
      where: { orderNumber },
      include: {
        table: true,
        orderDetails: true,
        payments: true,
      },
    });

    if (!order) {
      throw new NotFoundException(`Pesanan #${orderNumber} tidak ditemukan`);
    }

    return order;
  }

  async updateOrderStatus(id: string, dto: UpdateOrderStatusDto) {
    const order = await this.findOne(id);

    // Business rule check: Unpaid orders cannot enter processing statuses
    const processingStatuses: OrderStatus[] = [
      OrderStatus.MENUNGGU_DIPROSES,
      OrderStatus.DIPROSES,
      OrderStatus.SIAP_DISAJIKAN,
      OrderStatus.SELESAI,
    ];

    if (processingStatuses.includes(dto.orderStatus as OrderStatus) && order.paymentStatus !== PaymentStatus.PAID) {
      throw new BadRequestException('Pesanan yang belum dibayar tidak dapat diproses dapur/kasir.');
    }

    return this.prisma.order.update({
      where: { id },
      data: { orderStatus: dto.orderStatus as OrderStatus },
      include: {
        table: true,
        orderDetails: true,
        payments: true,
      },
    });
  }

  async cancelOrder(id: string) {
    const order = await this.findOne(id);

    if (order.paymentStatus === PaymentStatus.PAID) {
      throw new BadRequestException('Pesanan yang sudah lunas tidak dapat dibatalkan langsung.');
    }

    return this.prisma.order.update({
      where: { id },
      data: {
        paymentStatus: PaymentStatus.CANCELLED,
        orderStatus: OrderStatus.DIBATALKAN,
      },
    });
  }

  async getReceipt(id: string) {
    const order = await this.findOne(id);

    return {
      storeInfo: {
        name: 'Ayam Goreng Lengkuas Aroma',
        address: 'Ruko Puri Cempaka Blok R1 No. 10, Panancangan, Kec. Cipocok Jaya, Kota Serang, Banten 42124',
        phone: '0821-1215-2744',
      },
      orderNumber: order.orderNumber,
      dateTime: order.createdAt,
      customerName: order.customerName,
      orderType: order.orderType,
      tableName: order.table ? order.table.tableNumber : null,
      paymentMethod: order.paymentMethod,
      paymentStatus: order.paymentStatus,
      items: order.orderDetails.map((i) => ({
        productName: i.productName,
        price: i.price,
        quantity: i.quantity,
        subtotal: i.subtotal,
        notes: i.notes,
      })),
      total: order.total,
    };
  }
}
