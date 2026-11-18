import { Controller, Get, Post, Put, Delete, Body, Param, UseGuards, Req } from '@nestjs/common';
import { TablesService } from './tables.service';
import { CreateTableDto, UpdateTableDto } from './dto/create-table.dto';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';

const DEFAULT_FRONTEND_URL = process.env.FRONTEND_URL || 'https://aroma-ayam-goreng-lengkuas.vercel.app';

@Controller('tables')
export class TablesController {
  constructor(private readonly tablesService: TablesService) {}

  @Get()
  async findAll() {
    return this.tablesService.findAll();
  }

  @Get('by-number/:tableNumber')
  async findByNumber(@Param('tableNumber') tableNumber: string) {
    return this.tablesService.findByNumber(tableNumber);
  }

  @Get(':id')
  async findOne(@Param('id') id: string) {
    return this.tablesService.findOne(id);
  }

  @Post()
  @UseGuards(JwtAuthGuard)
  async create(@Body() dto: CreateTableDto, @Req() req: any) {
    const host = req.headers.origin || DEFAULT_FRONTEND_URL;
    return this.tablesService.create(dto, host);
  }

  @Put(':id')
  @UseGuards(JwtAuthGuard)
  async update(@Param('id') id: string, @Body() dto: UpdateTableDto, @Req() req: any) {
    const host = req.headers.origin || DEFAULT_FRONTEND_URL;
    return this.tablesService.update(id, dto, host);
  }

  @Delete(':id')
  @UseGuards(JwtAuthGuard)
  async remove(@Param('id') id: string) {
    return this.tablesService.remove(id);
  }
}
