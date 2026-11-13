import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CreateProductDto, UpdateProductDto, ReorderProductsDto } from './dto/create-product.dto';

@Injectable()
export class ProductsService {
  constructor(private readonly prisma: PrismaService) {}

  async findAll(categoryId?: string, search?: string) {
    const where: any = {};
    if (categoryId) {
      where.categoryId = categoryId;
    }
    if (search) {
      where.OR = [
        { name: { contains: search, mode: 'insensitive' } },
        { description: { contains: search, mode: 'insensitive' } },
      ];
    }

    return this.prisma.product.findMany({
      where,
      include: {
        category: {
          select: { id: true, name: true },
        },
      },
      orderBy: [
        { sortOrder: 'asc' },
        { isAvailable: 'desc' },
        { name: 'asc' },
      ],
    });
  }

  async findOne(id: string) {
    const product = await this.prisma.product.findUnique({
      where: { id },
      include: {
        category: true,
      },
    });
    if (!product) {
      throw new NotFoundException('Produk tidak ditemukan');
    }
    return product;
  }

  async create(dto: CreateProductDto, imageUrl?: string) {
    const maxProduct = await this.prisma.product.findFirst({
      where: { categoryId: dto.categoryId },
      orderBy: { sortOrder: 'desc' },
    });
    const nextSortOrder = (maxProduct?.sortOrder ?? 0) + 1;

    return this.prisma.product.create({
      data: {
        ...dto,
        image: imageUrl || dto.image || null,
        isAvailable: dto.isAvailable !== undefined ? dto.isAvailable : true,
        sortOrder: dto.sortOrder !== undefined ? dto.sortOrder : nextSortOrder,
      },
      include: {
        category: true,
      },
    });
  }

  async update(id: string, dto: UpdateProductDto, imageUrl?: string) {
    await this.findOne(id);
    const dataToUpdate: any = { ...dto };
    if (imageUrl) {
      dataToUpdate.image = imageUrl;
    }

    return this.prisma.product.update({
      where: { id },
      data: dataToUpdate,
      include: {
        category: true,
      },
    });
  }

  async reorder(productIds: string[]) {
    if (!productIds || productIds.length === 0) {
      return [];
    }
    return this.prisma.$transaction(
      productIds.map((id, index) =>
        this.prisma.product.update({
          where: { id },
          data: { sortOrder: index + 1 },
        }),
      ),
    );
  }

  async toggleAvailability(id: string, isAvailable: boolean) {
    await this.findOne(id);
    return this.prisma.product.update({
      where: { id },
      data: { isAvailable },
    });
  }

  async remove(id: string) {
    await this.findOne(id);
    return this.prisma.product.delete({
      where: { id },
    });
  }
}
