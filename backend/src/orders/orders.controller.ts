import {
  Controller,
  Get,
  Post,
  Patch,
  Body,
  Param,
  Query,
  UseGuards,
} from '@nestjs/common';
import { OrdersService } from './orders.service';
import { CreateOrderDto, UpdateOrderStatusDto } from './dto/create-order.dto';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { GetUser } from '../auth/get-user.decorator';
import { OrderStatus, PaymentStatus } from '@prisma/client';

@Controller('orders')
export class OrdersController {
  constructor(private readonly ordersService: OrdersService) {}

  @Post()
  async createOrder(@Body() dto: CreateOrderDto) {
    return this.ordersService.createOrder(dto);
  }

  @Post('cashier')
  @UseGuards(JwtAuthGuard)
  async createCashierOrder(@Body() dto: CreateOrderDto, @GetUser('id') userId: string) {
    return this.ordersService.createOrder(dto, userId);
  }

  @Get()
  @UseGuards(JwtAuthGuard)
  async findAll(
    @Query('status') status?: OrderStatus,
    @Query('paymentStatus') paymentStatus?: PaymentStatus,
    @Query('date') date?: string,
  ) {
    return this.ordersService.findAll(status, paymentStatus, date);
  }

  @Get('track/:orderNumber')
  async findByOrderNumber(@Param('orderNumber') orderNumber: string) {
    return this.ordersService.findByOrderNumber(orderNumber);
  }

  @Get(':id')
  async findOne(@Param('id') id: string) {
    return this.ordersService.findOne(id);
  }

  @Get(':id/receipt')
  async getReceipt(@Param('id') id: string) {
    return this.ordersService.getReceipt(id);
  }

  @Patch(':id/status')
  @UseGuards(JwtAuthGuard)
  async updateOrderStatus(@Param('id') id: string, @Body() dto: UpdateOrderStatusDto) {
    return this.ordersService.updateOrderStatus(id, dto);
  }

  @Post(':id/cancel')
  @UseGuards(JwtAuthGuard)
  async cancelOrder(@Param('id') id: string) {
    return this.ordersService.cancelOrder(id);
  }
}
