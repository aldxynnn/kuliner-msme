import { IsBoolean, IsNotEmpty, IsOptional, IsString } from 'class-validator';

export class CreateTableDto {
  @IsString()
  @IsNotEmpty({ message: 'Nomor/Nama meja wajib diisi' })
  tableNumber: string;

  @IsBoolean()
  @IsOptional()
  isAvailable?: boolean;
}

export class UpdateTableDto {
  @IsString()
  @IsOptional()
  tableNumber?: string;

  @IsBoolean()
  @IsOptional()
  isAvailable?: boolean;
}
