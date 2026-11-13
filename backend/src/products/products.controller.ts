import {
  Controller,
  Get,
  Post,
  Put,
  Patch,
  Delete,
  Body,
  Param,
  Query,
  UseGuards,
  UseInterceptors,
  UploadedFile,
  BadRequestException,
} from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import { diskStorage } from 'multer';
import { extname } from 'path';
import * as fs from 'fs';
import { v2 as cloudinary } from 'cloudinary';
import { ProductsService } from './products.service';
import { CreateProductDto, UpdateProductDto, ToggleAvailabilityDto, ReorderProductsDto } from './dto/create-product.dto';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';

@Controller('products')
export class ProductsController {
  constructor(private readonly productsService: ProductsService) {}

  @Get()
  async findAll(@Query('categoryId') categoryId?: string, @Query('search') search?: string) {
    return this.productsService.findAll(categoryId, search);
  }

  @Get(':id')
  async findOne(@Param('id') id: string) {
    return this.productsService.findOne(id);
  }

  @Post('upload')
  @UseGuards(JwtAuthGuard)
  @UseInterceptors(
    FileInterceptor('file', {
      storage: diskStorage({
        destination: './uploads',
        filename: (req, file, callback) => {
          const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1e9);
          const ext = extname(file.originalname);
          callback(null, `product-${uniqueSuffix}${ext}`);
        },
      }),
      fileFilter: (req, file, callback) => {
        if (!file.mimetype.match(/\/(jpg|jpeg|png|gif|webp)$/)) {
          return callback(new BadRequestException('Hanya file gambar yang diperbolehkan!'), false);
        }
        callback(null, true);
      },
      limits: {
        fileSize: 5 * 1024 * 1024,
      },
    }),
  )
  async uploadFile(@UploadedFile() file: Express.Multer.File) {
    if (!file) {
      throw new BadRequestException('File gambar wajib diunggah');
    }

    const cloudName = process.env.CLOUDINARY_CLOUD_NAME;
    const apiKey = process.env.CLOUDINARY_API_KEY;
    const apiSecret = process.env.CLOUDINARY_API_SECRET;

    // Production Cloudinary Storage Path
    if (cloudName && apiKey && apiSecret) {
      try {
        cloudinary.config({
          cloud_name: cloudName,
          api_key: apiKey,
          api_secret: apiSecret,
        });

        const uploadRes = await cloudinary.uploader.upload(file.path, {
          folder: 'aroma_products',
        });

        if (fs.existsSync(file.path)) {
          fs.unlinkSync(file.path);
        }

        return {
          imageUrl: uploadRes.secure_url,
        };
      } catch (err: any) {
        console.error('Cloudinary upload error:', err);
        if (fs.existsSync(file.path)) {
          fs.unlinkSync(file.path);
        }
        throw new BadRequestException(`Gagal mengunggah foto ke Cloud Storage (Cloudinary): ${err.message || err}`);
      }
    }

    // Local Development Fallback Storage Path
    return {
      imageUrl: `/uploads/${file.filename}`,
    };
  }

  @Patch('reorder')
  @UseGuards(JwtAuthGuard)
  async reorder(@Body() dto: ReorderProductsDto) {
    return this.productsService.reorder(dto.productIds);
  }

  @Post()
  @UseGuards(JwtAuthGuard)
  async create(@Body() dto: CreateProductDto) {
    return this.productsService.create(dto);
  }

  @Put(':id')
  @UseGuards(JwtAuthGuard)
  async update(
    @Param('id') id: string,
    @Body() dto: UpdateProductDto,
  ) {
    return this.productsService.update(id, dto);
  }

  @Patch(':id/availability')
  @UseGuards(JwtAuthGuard)
  async toggleAvailability(@Param('id') id: string, @Body() dto: ToggleAvailabilityDto) {
    return this.productsService.toggleAvailability(id, dto.isAvailable);
  }

  @Delete(':id')
  @UseGuards(JwtAuthGuard)
  async remove(@Param('id') id: string) {
    return this.productsService.remove(id);
  }
}
