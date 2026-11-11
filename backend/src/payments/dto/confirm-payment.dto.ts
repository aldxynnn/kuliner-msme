import { IsNotEmpty, IsOptional, IsString } from 'class-validator';

export class ConfirmPaymentDto {
  @IsString()
  @IsNotEmpty({ message: 'ID pesanan wajib diisi' })
  orderId: string;

  @IsString()
  @IsOptional()
  notes?: string;
}
