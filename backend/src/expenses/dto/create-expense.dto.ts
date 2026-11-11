import { IsInt, IsNotEmpty, IsOptional, IsString, Min } from 'class-validator';
import { Type } from 'class-transformer';

export class CreateExpenseDto {
  @IsString()
  @IsNotEmpty({ message: 'Deskripsi pengeluaran wajib diisi' })
  description: string;

  @Type(() => Number)
  @IsInt()
  @Min(1, { message: 'Jumlah nominal harus lebih besar dari 0' })
  amount: number;

  @IsString()
  @IsNotEmpty({ message: 'Kategori pengeluaran wajib diisi' })
  category: string;

  @IsOptional()
  @IsString()
  notes?: string;

  @IsOptional()
  date?: Date;
}

export class UpdateExpenseDto {
  @IsString()
  @IsOptional()
  description?: string;

  @Type(() => Number)
  @IsInt()
  @Min(1)
  @IsOptional()
  amount?: number;

  @IsString()
  @IsOptional()
  category?: string;

  @IsOptional()
  @IsString()
  notes?: string;

  @IsOptional()
  date?: Date;
}
