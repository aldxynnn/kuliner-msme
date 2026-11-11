import {
  IsArray,
  IsEnum,
  IsInt,
  IsNotEmpty,
  IsOptional,
  IsString,
  Min,
  ValidateNested,
} from 'class-validator';
import { Type } from 'class-transformer';
import { OrderType, PaymentMethod } from '@prisma/client';

export class OrderItemDto {
  @IsString()
  @IsNotEmpty({ message: 'ID produk wajib diisi' })
  productId: string;

  @IsInt()
  @Min(1, { message: 'Jumlah minimal 1' })
  quantity: number;

  @IsString()
  @IsOptional()
  notes?: string;
}

export class CreateOrderDto {
  @IsString()
  @IsOptional()
  customerName?: string;

  @IsEnum(OrderType, { message: 'Tipe pesanan harus DINE_IN atau TAKE_AWAY' })
  orderType: OrderType;

  @IsString()
  @IsOptional()
  tableId?: string;

  @IsString()
  @IsOptional()
  tableNumber?: string;

  @IsEnum(PaymentMethod, { message: 'Metode pembayaran harus QRIS atau CASH' })
  paymentMethod: PaymentMethod;

  @IsString()
  @IsOptional()
  notes?: string;

  @IsArray()
  @ValidateNested({ each: true })
  @Type(() => OrderItemDto)
  items: OrderItemDto[];

  @IsOptional()
  @IsEnum(['PENDING', 'PAID'])
  paymentStatus?: 'PENDING' | 'PAID';
}

export class UpdateOrderStatusDto {
  @IsEnum([
    'MENUNGGU_PEMBAYARAN',
    'MENUNGGU_DIPROSES',
    'DIPROSES',
    'SIAP_DISAJIKAN',
    'SELESAI',
    'DIBATALKAN',
  ], { message: 'Status pesanan tidak valid' })
  orderStatus: 'MENUNGGU_PEMBAYARAN' | 'MENUNGGU_DIPROSES' | 'DIPROSES' | 'SIAP_DISAJIKAN' | 'SELESAI' | 'DIBATALKAN';
}
