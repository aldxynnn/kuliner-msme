import { Injectable, BadRequestException, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { ConfirmPaymentDto } from './dto/confirm-payment.dto';
import { PaymentStatus, OrderStatus } from '@prisma/client';

@Injectable()
export class PaymentsService {
  constructor(private readonly prisma: PrismaService) {}

  async confirmPayment(dto: ConfirmPaymentDto, cashierUserId: string) {
    const order = await this.prisma.order.findUnique({
      where: { id: dto.orderId },
      include: { payments: true },
    });

    if (!order) {
      throw new NotFoundException('Pesanan tidak ditemukan');
    }

    if (order.paymentStatus === PaymentStatus.PAID) {
      throw new BadRequestException('Pembayaran pesanan ini sudah terkonfirmasi sebelumnya (Cegah Konfirmasi Ganda).');
    }

    if (order.paymentStatus === PaymentStatus.CANCELLED) {
      throw new BadRequestException('Pesanan ini sudah dibatalkan');
    }

    // Atomic transaction for payment confirmation
    return this.prisma.$transaction(async (tx) => {
      const payment = await tx.payment.create({
        data: {
          orderId: order.id,
          amount: order.total,
          paymentMethod: order.paymentMethod,
          status: PaymentStatus.PAID,
          confirmedAt: new Date(),
          confirmedByUserId: cashierUserId,
        },
      });

      const updatedOrder = await tx.order.update({
        where: { id: order.id },
        data: {
          paymentStatus: PaymentStatus.PAID,
          orderStatus: order.orderStatus === OrderStatus.MENUNGGU_PEMBAYARAN
            ? OrderStatus.MENUNGGU_DIPROSES
            : order.orderStatus,
        },
        include: {
          table: true,
          orderDetails: true,
          payments: true,
        },
      });

      return {
        message: 'Konfirmasi pembayaran berhasil',
        payment,
        order: updatedOrder,
      };
    });
  }

  async getUnpaidOrders() {
    return this.prisma.order.findMany({
      where: {
        paymentStatus: PaymentStatus.PENDING,
        orderStatus: { not: OrderStatus.DIBATALKAN },
      },
      include: {
        table: true,
        orderDetails: true,
      },
      orderBy: { createdAt: 'desc' },
    });
  }

  async getPaymentHistory() {
    return this.prisma.payment.findMany({
      where: { status: PaymentStatus.PAID },
      include: {
        order: {
          include: {
            table: true,
            orderDetails: true,
          },
        },
        confirmedByUser: {
          select: { id: true, name: true, email: true },
        },
      },
      orderBy: { confirmedAt: 'desc' },
    });
  }
}
