import { Injectable, NotFoundException, BadRequestException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CreateTableDto, UpdateTableDto } from './dto/create-table.dto';

@Injectable()
export class TablesService {
  constructor(private readonly prisma: PrismaService) {}

  async findAll() {
    return this.prisma.table.findMany({
      orderBy: { tableNumber: 'asc' },
    });
  }

  async findOne(id: string) {
    const table = await this.prisma.table.findUnique({
      where: { id },
    });
    if (!table) {
      throw new NotFoundException('Meja tidak ditemukan');
    }
    return table;
  }

  async findByNumber(tableNumber: string) {
    const table = await this.prisma.table.findUnique({
      where: { tableNumber },
    });
    if (!table) {
      throw new NotFoundException(`Meja dengan nama ${tableNumber} tidak ditemukan`);
    }
    return table;
  }

  async create(dto: CreateTableDto, hostUrl: string = 'http://localhost:5173') {
    const existing = await this.prisma.table.findUnique({
      where: { tableNumber: dto.tableNumber },
    });

    if (existing) {
      throw new BadRequestException('Nomor meja sudah ada');
    }

    const qrCode = `${hostUrl}/?table=${encodeURIComponent(dto.tableNumber)}`;

    return this.prisma.table.create({
      data: {
        tableNumber: dto.tableNumber,
        qrCode,
        isAvailable: dto.isAvailable !== undefined ? dto.isAvailable : true,
      },
    });
  }

  async update(id: string, dto: UpdateTableDto, hostUrl: string = 'http://localhost:5173') {
    const current = await this.findOne(id);

    let qrCode = current.qrCode;
    if (dto.tableNumber && dto.tableNumber !== current.tableNumber) {
      const existing = await this.prisma.table.findUnique({
        where: { tableNumber: dto.tableNumber },
      });
      if (existing) {
        throw new BadRequestException('Nomor meja sudah digunakan');
      }
      qrCode = `${hostUrl}/?table=${encodeURIComponent(dto.tableNumber)}`;
    }

    return this.prisma.table.update({
      where: { id },
      data: {
        ...dto,
        qrCode,
      },
    });
  }

  async remove(id: string) {
    await this.findOne(id);
    return this.prisma.table.delete({
      where: { id },
    });
  }
}
