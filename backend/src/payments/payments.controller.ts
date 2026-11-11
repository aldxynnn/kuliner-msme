import { Controller, Post, Get, Body, UseGuards } from '@nestjs/common';
import { PaymentsService } from './payments.service';
import { ConfirmPaymentDto } from './dto/confirm-payment.dto';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { GetUser } from '../auth/get-user.decorator';

@Controller('payments')
@UseGuards(JwtAuthGuard)
export class PaymentsController {
  constructor(private readonly paymentsService: PaymentsService) {}

  @Post('confirm')
  async confirmPayment(@Body() dto: ConfirmPaymentDto, @GetUser('id') cashierUserId: string) {
    return this.paymentsService.confirmPayment(dto, cashierUserId);
  }

  @Get('unpaid')
  async getUnpaidOrders() {
    return this.paymentsService.getUnpaidOrders();
  }

  @Get('history')
  async getPaymentHistory() {
    return this.paymentsService.getPaymentHistory();
  }
}
