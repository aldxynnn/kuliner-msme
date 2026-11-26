import React from 'react';
import { MapPin, Clock, Utensils, ShoppingBag } from 'lucide-react';
import { OrderStatus, OrderType } from '../types';

interface HeaderProps {
  tableNumber: string | null;
  orderType: OrderType;
  onOrderTypeChange: (type: OrderType) => void;
  activeOrderNumber: string | null;
  activeOrderStatus?: OrderStatus;
  onOpenOrderTracking: () => void;
}

const getCompactStatusLabel = (status?: OrderStatus): string => {
  switch (status) {
    case 'MENUNGGU_PEMBAYARAN':
      return 'Menunggu Bayar';
    case 'MENUNGGU_DIPROSES':
      return 'Masuk Antrean';
    case 'DIPROSES':
      return 'Sedang Dimasak';
    case 'SIAP_DISAJIKAN':
      return 'Siap Disajikan';
    case 'SELESAI':
      return 'Selesai';
    case 'DIBATALKAN':
      return 'Dibatalkan';
    default:
      return 'Status Pesanan';
  }
};

export const Header: React.FC<HeaderProps> = ({
  tableNumber,
  orderType,
  onOrderTypeChange,
  activeOrderNumber,
  activeOrderStatus,
  onOpenOrderTracking,
}) => {
  return (
    <header className="bg-white/95 backdrop-blur-md border-b border-cream-200">
      {/* Main Bar */}
      <div className="max-w-6xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between gap-3">
        {/* Brand Identity */}
        <div className="flex items-center gap-3 min-w-0">
          <img
            src="/logo-aroma.png"
            alt="Logo AROMA Ayam Goreng Lengkuas"
            className="w-10 h-10 rounded-xl object-cover border border-cream-200 shrink-0 bg-cream-50"
          />
          <div className="min-w-0">
            <h1 className="font-display font-bold text-lg sm:text-xl text-charcoal tracking-tight leading-none truncate">
              AROMA
            </h1>
            <p className="text-[11px] font-medium text-charcoal-muted leading-tight mt-0.5 truncate">
              Ayam Goreng Lengkuas
            </p>
          </div>
        </div>

        {/* Desktop Order Type Segmented Switcher */}
        <div className="hidden md:flex items-center bg-cream-100 p-1 rounded-xl border border-cream-200">
          <button
            type="button"
            onClick={() => onOrderTypeChange('DINE_IN')}
            aria-pressed={orderType === 'DINE_IN'}
            className={`inline-flex items-center gap-1.5 px-4 py-1.5 rounded-lg text-xs font-bold transition-all ${
              orderType === 'DINE_IN'
                ? 'bg-white text-brand-600 shadow-sm border border-cream-200/80'
                : 'text-charcoal-secondary hover:text-charcoal'
            }`}
          >
            <Utensils className={`w-3.5 h-3.5 ${orderType === 'DINE_IN' ? 'text-brand-500' : ''}`} />
            <span>Dine In</span>
          </button>
          <button
            type="button"
            onClick={() => onOrderTypeChange('TAKE_AWAY')}
            aria-pressed={orderType === 'TAKE_AWAY'}
            className={`inline-flex items-center gap-1.5 px-4 py-1.5 rounded-lg text-xs font-bold transition-all ${
              orderType === 'TAKE_AWAY'
                ? 'bg-white text-brand-600 shadow-sm border border-cream-200/80'
                : 'text-charcoal-secondary hover:text-charcoal'
            }`}
          >
            <ShoppingBag className={`w-3.5 h-3.5 ${orderType === 'TAKE_AWAY' ? 'text-brand-500' : ''}`} />
            <span>Take Away</span>
          </button>
        </div>

        {/* Right Context Actions: Active Order & Table Badge */}
        <div className="flex items-center gap-2 shrink-0">
          {activeOrderNumber && (
            <button
              type="button"
              onClick={onOpenOrderTracking}
              className="inline-flex items-center gap-2 min-h-[38px] px-3 py-1.5 rounded-xl bg-semantic-warning-bg text-semantic-warning border border-semantic-warning-border text-xs font-bold hover:brightness-95 active:scale-[0.98] transition-all"
            >
              <span className="relative flex h-2 w-2 shrink-0">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-semantic-warning opacity-60" />
                <span className="relative inline-flex rounded-full h-2 w-2 bg-semantic-warning" />
              </span>
              <Clock className="w-3.5 h-3.5 hidden sm:block shrink-0" />
              <span className="hidden sm:inline">{getCompactStatusLabel(activeOrderStatus)}</span>
              <span className="sm:hidden">Pesanan</span>
            </button>
          )}

          {tableNumber && (
            <div className="inline-flex items-center gap-1.5 min-h-[38px] px-3 py-1.5 rounded-xl bg-brand-50 text-brand-700 border border-brand-200 text-xs font-bold">
              <MapPin className="w-3.5 h-3.5 text-brand-500 shrink-0" />
              <span className="max-w-[96px] truncate">{tableNumber}</span>
            </div>
          )}
        </div>
      </div>

      {/* Mobile Order Type Bar */}
      <div className="md:hidden bg-cream-50/90 border-t border-cream-200 px-4 py-2">
        <div className="grid grid-cols-2 gap-1 bg-cream-100 p-1 rounded-xl border border-cream-200">
          <button
            type="button"
            onClick={() => onOrderTypeChange('DINE_IN')}
            aria-pressed={orderType === 'DINE_IN'}
            className={`min-h-[36px] inline-flex items-center justify-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
              orderType === 'DINE_IN'
                ? 'bg-white text-brand-600 shadow-sm border border-cream-200/80'
                : 'text-charcoal-secondary hover:text-charcoal'
            }`}
          >
            <Utensils className={`w-3.5 h-3.5 shrink-0 ${orderType === 'DINE_IN' ? 'text-brand-500' : ''}`} />
            <span>Dine In</span>
          </button>
          <button
            type="button"
            onClick={() => onOrderTypeChange('TAKE_AWAY')}
            aria-pressed={orderType === 'TAKE_AWAY'}
            className={`min-h-[36px] inline-flex items-center justify-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
              orderType === 'TAKE_AWAY'
                ? 'bg-white text-brand-600 shadow-sm border border-cream-200/80'
                : 'text-charcoal-secondary hover:text-charcoal'
            }`}
          >
            <ShoppingBag className={`w-3.5 h-3.5 shrink-0 ${orderType === 'TAKE_AWAY' ? 'text-brand-500' : ''}`} />
            <span>Take Away</span>
          </button>
        </div>
      </div>
    </header>
  );
};
