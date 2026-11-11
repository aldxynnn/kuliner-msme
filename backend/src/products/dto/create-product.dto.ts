import { IsBoolean, IsInt, IsNotEmpty, IsOptional, IsString, Min } from 'class-validator';
import { Type } from 'class-transformer';

export class CreateProductDto {
  @IsString()
  @IsNotEmpty({ message: 'Kategori wajib dipilih' })
  categoryId: string;

  @IsString()
  @IsNotEmpty({ message: 'Nama produk wajib diisi' })
  name: string;

  @IsString()
  @IsOptional()
  description?: string;

  @Type(() => Number)
  @IsInt()
  @Min(0, { message: 'Harga tidak boleh negatif' })
  price: number;

  @IsString()
  @IsOptional()
  image?: string;

  @IsOptional()
  @Type(() => Boolean)
  @IsBoolean()
  isAvailable?: boolean;
}

export class UpdateProductDto {
  @IsString()
  @IsOptional()
  categoryId?: string;

  @IsString()
  @IsOptional()
  name?: string;

  @IsString()
  @IsOptional()
  description?: string;

  @Type(() => Number)
  @IsInt()
  @Min(0)
  @IsOptional()
  price?: number;

  @IsString()
  @IsOptional()
  image?: string;

  @IsOptional()
  @Type(() => Boolean)
  @IsBoolean()
  isAvailable?: boolean;
}

export class ToggleAvailabilityDto {
  @IsBoolean()
  isAvailable: boolean;
}
