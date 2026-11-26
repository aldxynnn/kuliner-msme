import React from 'react';
import { Clock, Utensils, ShoppingBag } from 'lucide-react';
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
      return 'Antrean';
    case 'DIPROSES':
      return 'Dimasak';
    case 'SIAP_DISAJIKAN':
      return 'Siap Saji';
    case 'SELESAI':
      return 'Selesai';
    case 'DIBATALKAN':
      return 'Dibatalkan';
    default:
      return 'Status';
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
    <header className="bg-paper-50/95 backdrop-blur-md border-b border-paper-200">
      {/* Main Architectural Bar */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between gap-3">
        {/* Brand Atelier Identity */}
        <div className="flex items-center gap-3 min-w-0">
          <img
            src="/logo-aroma.png"
            alt="Logo AROMA Ayam Goreng Lengkuas"
            className="w-9 h-9 rounded-lg object-cover border border-paper-200 shrink-0 bg-white"
          />
          <div className="flex items-center gap-2.5 min-w-0">
            <h1 className="font-display font-bold text-xl sm:text-[22px] text-ink tracking-tight leading-none shrink-0">
              AROMA
            </h1>
            <span className="hidden sm:block h-4 w-px bg-paper-200 shrink-0" aria-hidden="true" />
            <p className="hidden sm:block text-[10px] font-mono font-semibold uppercase tracking-[0.14em] text-ink-muted truncate">
              Ayam Goreng Lengkuas
            </p>
          </div>
        </div>

        {/* Desktop Order Mode Switcher */}
        <div
          role="group"
          aria-label="Tipe Pemesanan"
          className="hidden md:flex items-center bg-paper-100 p-1 rounded-full border border-paper-200"
        >
          <button
            type="button"
            onClick={() => onOrderTypeChange('DINE_IN')}
            aria-pressed={orderType === 'DINE_IN'}
            className={`inline-flex items-center gap-1.5 px-4 py-1.5 rounded-full text-xs font-bold transition-all ${
              orderType === 'DINE_IN'
                ? 'bg-ink text-white shadow-sm'
                : 'text-ink-secondary hover:text-ink'
            }`}
          >
            <Utensils className="w-3.5 h-3.5 shrink-0" />
            <span>Dine In</span>
          </button>
          <button
            type="button"
            onClick={() => onOrderTypeChange('TAKE_AWAY')}
            aria-pressed={orderType === 'TAKE_AWAY'}
            className={`inline-flex items-center gap-1.5 px-4 py-1.5 rounded-full text-xs font-bold transition-all ${
              orderType === 'TAKE_AWAY'
                ? 'bg-ink text-white shadow-sm'
                : 'text-ink-secondary hover:text-ink'
            }`}
          >
            <ShoppingBag className="w-3.5 h-3.5 shrink-0" />
            <span>Take Away</span>
          </button>
        </div>

        {/* Right Context Actions: Live Order Tracker & Table Tag */}
        <div className="flex items-center gap-2 shrink-0">
          {activeOrderNumber && (
            <button
              type="button"
              onClick={onOpenOrderTracking}
              className="inline-flex items-center gap-2 min-h-[36px] px-3 py-1.5 rounded-full bg-semantic-warning-bg text-semantic-warning border border-semantic-warning-border text-xs font-bold hover:brightness-95 active:scale-[0.98] transition-all"
            >
              <span className="relative flex h-2 w-2 shrink-0">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-semantic-warning opacity-65" />
                <span className="relative inline-flex rounded-full h-2 w-2 bg-semantic-warning" />
              </span>
              <Clock className="w-3.5 h-3.5 hidden sm:block shrink-0" />
              <span className="hidden sm:inline font-mono text-[11px] uppercase tracking-wider">
                {getCompactStatusLabel(activeOrderStatus)}
              </span>
              <span className="sm:hidden">Pesanan</span>
            </button>
          )}

          {tableNumber && (
            <div className="inline-flex items-center gap-1.5 min-h-[36px] px-3 py-1.5 rounded-full bg-pandan-50 text-pandan-700 border border-pandan-200 text-[11px] font-mono font-bold uppercase tracking-wider">
              <span className="w-2 h-2 rounded-full bg-pandan-500 shrink-0" />
              <span className="max-w-[96px] truncate">{tableNumber}</span>
            </div>
          )}
        </div>
      </div>

      {/* Mobile Compact Order Mode Strip */}
      <div className="md:hidden bg-paper-50 border-t border-paper-200 px-4 py-2">
        <div className="grid grid-cols-2 gap-1 bg-paper-100 p-1 rounded-full border border-paper-200">
          <button
            type="button"
            onClick={() => onOrderTypeChange('DINE_IN')}
            aria-pressed={orderType === 'DINE_IN'}
            className={`min-h-[36px] inline-flex items-center justify-center gap-1.5 px-3 py-1.5 rounded-full text-xs font-bold transition-all ${
              orderType === 'DINE_IN'
                ? 'bg-ink text-white shadow-sm'
                : 'text-ink-secondary hover:text-ink'
            }`}
          >
            <Utensils className="w-3.5 h-3.5 shrink-0" />
            <span>Dine In</span>
          </button>
          <button
            type="button"
            onClick={() => onOrderTypeChange('TAKE_AWAY')}
            aria-pressed={orderType === 'TAKE_AWAY'}
            className={`min-h-[36px] inline-flex items-center justify-center gap-1.5 px-3 py-1.5 rounded-full text-xs font-bold transition-all ${
              orderType === 'TAKE_AWAY'
                ? 'bg-ink text-white shadow-sm'
                : 'text-ink-secondary hover:text-ink'
            }`}
          >
            <ShoppingBag className="w-3.5 h-3.5 shrink-0" />
            <span>Take Away</span>
          </button>
        </div>
      </div>
    </header>
  );
};
