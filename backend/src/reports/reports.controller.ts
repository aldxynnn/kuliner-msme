import { Controller, Get, Query, UseGuards } from '@nestjs/common';
import { ReportsService } from './reports.service';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';

@Controller('reports')
@UseGuards(JwtAuthGuard)
export class ReportsController {
  constructor(private readonly reportsService: ReportsService) {}

  @Get('dashboard')
  async getDashboardSummary() {
    return this.reportsService.getDashboardSummary();
  }

  @Get('financial')
  async getFinancialReport(
    @Query('period') period?: 'daily' | 'monthly',
    @Query('date') date?: string,
  ) {
    return this.reportsService.getFinancialReport(period || 'daily', date);
  }
}
