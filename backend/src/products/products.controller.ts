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
} from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import { diskStorage } from 'multer';
import { extname } from 'path';
import { ProductsService } from './products.service';
import { CreateProductDto, UpdateProductDto, ToggleAvailabilityDto } from './dto/create-product.dto';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';

const storage = diskStorage({
  destination: './uploads',
  filename: (req, file, callback) => {
    const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1e9);
    const ext = extname(file.originalname);
    callback(null, `product-${uniqueSuffix}${ext}`);
  },
});

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

  @Post()
  @UseGuards(JwtAuthGuard)
  @UseInterceptors(FileInterceptor('imageFile', { storage }))
  async create(@Body() dto: CreateProductDto, @UploadedFile() file?: Express.Multer.File) {
    const imageUrl = file ? `/uploads/${file.filename}` : undefined;
    return this.productsService.create(dto, imageUrl);
  }

  @Put(':id')
  @UseGuards(JwtAuthGuard)
  @UseInterceptors(FileInterceptor('imageFile', { storage }))
  async update(
    @Param('id') id: string,
    @Body() dto: UpdateProductDto,
    @UploadedFile() file?: Express.Multer.File,
  ) {
    const imageUrl = file ? `/uploads/${file.filename}` : undefined;
    return this.productsService.update(id, dto, imageUrl);
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
