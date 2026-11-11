import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { PaymentStatus, OrderStatus } from '@prisma/client';

@Injectable()
export class ReportsService {
  constructor(private readonly prisma: PrismaService) {}

  async getDashboardSummary() {
    const todayStart = new Date();
    todayStart.setHours(0, 0, 0, 0);

    const todayEnd = new Date();
    todayEnd.setHours(23, 59, 59, 999);

    // Today's paid orders & revenue
    const todayPaidOrders = await this.prisma.order.findMany({
      where: {
        paymentStatus: PaymentStatus.PAID,
        createdAt: { gte: todayStart, lte: todayEnd },
      },
    });

    const todayRevenue = todayPaidOrders.reduce((acc, curr) => acc + curr.total, 0);
    const todayTransactionCount = todayPaidOrders.length;

    // Pending payments
    const pendingPaymentsCount = await this.prisma.order.count({
      where: {
        paymentStatus: PaymentStatus.PENDING,
        orderStatus: { not: OrderStatus.DIBATALKAN },
      },
    });

    // Active orders in kitchen/service
    const activeOrdersCount = await this.prisma.order.count({
      where: {
        orderStatus: {
          in: [OrderStatus.MENUNGGU_DIPROSES, OrderStatus.DIPROSES, OrderStatus.SIAP_DISAJIKAN],
        },
      },
    });

    // Completed orders today
    const completedOrdersTodayCount = await this.prisma.order.count({
      where: {
        orderStatus: OrderStatus.SELESAI,
        createdAt: { gte: todayStart, lte: todayEnd },
      },
    });

    return {
      todayRevenue,
      todayTransactionCount,
      pendingPaymentsCount,
      activeOrdersCount,
      completedOrdersTodayCount,
    };
  }

  async getFinancialReport(period: 'daily' | 'monthly' = 'daily', dateStr?: string) {
    let startDate: Date;
    let endDate: Date;

    const baseDate = dateStr ? new Date(dateStr) : new Date();

    if (period === 'monthly') {
      startDate = new Date(baseDate.getFullYear(), baseDate.getMonth(), 1, 0, 0, 0, 0);
      endDate = new Date(baseDate.getFullYear(), baseDate.getMonth() + 1, 0, 23, 59, 59, 999);
    } else {
      startDate = new Date(baseDate);
      startDate.setHours(0, 0, 0, 0);
      endDate = new Date(baseDate);
      endDate.setHours(23, 59, 59, 999);
    }

    // Revenue from confirmed paid orders
    const paidOrders = await this.prisma.order.findMany({
      where: {
        paymentStatus: PaymentStatus.PAID,
        createdAt: { gte: startDate, lte: endDate },
      },
      select: { id: true, orderNumber: true, total: true, createdAt: true, paymentMethod: true },
    });

    const totalRevenue = paidOrders.reduce((acc, o) => acc + o.total, 0);

    // Expenses
    const expenses = await this.prisma.expense.findMany({
      where: {
        date: { gte: startDate, lte: endDate },
      },
    });

    const totalExpenses = expenses.reduce((acc, e) => acc + e.amount, 0);
    const operatingSurplus = totalRevenue - totalExpenses;

    return {
      period,
      startDate,
      endDate,
      transactionCount: paidOrders.length,
      totalRevenue,
      totalExpenses,
      operatingSurplus,
      note: 'Surplus operasional dihitung dari (Total Pendapatan - Total Pengeluaran)',
      paidOrders,
      expenses,
    };
  }
}
