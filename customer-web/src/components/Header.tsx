import React from 'react';
import { Flame, MapPin, Clock } from 'lucide-react';
import { OrderType } from '../types';

interface HeaderProps {
  tableNumber: string | null;
  orderType: OrderType;
  onOrderTypeChange: (type: OrderType) => void;
  activeOrderNumber: string | null;
  onOpenOrderTracking: () => void;
}

export const Header: React.FC<HeaderProps> = ({
  tableNumber,
  orderType,
  onOrderTypeChange,
  activeOrderNumber,
  onOpenOrderTracking,
}) => {
  return (
    <header className="sticky top-0 z-30 bg-white/90 backdrop-blur-md border-b border-cream-200 shadow-xs transition-all">
      <div className="max-w-4xl mx-auto px-4 py-3.5 flex items-center justify-between">
        {/* Brand */}
        <div className="flex items-center gap-3">
          <div className="w-11 h-11 rounded-2xl bg-gradient-to-br from-brand-500 to-brand-700 text-white flex items-center justify-center shadow-md shadow-brand-500/20">
            <Flame className="w-6 h-6" />
          </div>
          <div>
            <h1 className="font-extrabold text-base md:text-lg text-charcoal tracking-tight leading-tight">
              AROMA
            </h1>
            <p className="text-[11px] font-medium text-charcoal-muted tracking-wide">
              Ayam Goreng Lengkuas
            </p>
          </div>
        </div>

        {/* Status & Table Badge */}
        <div className="flex items-center gap-2.5">
          {activeOrderNumber && (
            <button
              onClick={onOpenOrderTracking}
              className="flex items-center gap-1.5 px-3.5 py-2 rounded-xl bg-amber-50 text-amber-800 border border-amber-200/80 text-xs font-bold hover:bg-amber-100 transition-all shadow-xs"
            >
              <Clock className="w-4 h-4 animate-pulse text-amber-600" />
              <span className="hidden sm:inline">Status Pesanan</span>
              <span className="sm:hidden">Status</span>
            </button>
          )}

          {tableNumber && (
            <div className="flex items-center gap-1.5 px-3.5 py-2 rounded-xl bg-brand-50 text-brand-700 border border-brand-200/80 text-xs font-bold shadow-xs">
              <MapPin className="w-4 h-4 text-brand-500" />
              <span>{tableNumber}</span>
            </div>
          )}
        </div>
      </div>

      {/* Order Type Toggle Bar */}
      <div className="bg-cream-100/60 border-t border-cream-200/80 py-2 px-4">
        <div className="max-w-4xl mx-auto flex items-center justify-between">
          <span className="text-xs font-semibold text-charcoal-muted uppercase tracking-wider">
            Tipe Pesanan:
          </span>
          <div className="bg-white p-1 rounded-xl border border-cream-200/85 flex gap-1 shadow-xs">
            <button
              onClick={() => onOrderTypeChange('DINE_IN')}
              className={`px-3.5 py-1.5 rounded-lg text-xs font-bold transition-all ${
                orderType === 'DINE_IN'
                  ? 'bg-brand-500 text-white shadow-sm'
                  : 'text-charcoal-muted hover:text-charcoal hover:bg-cream-50'
              }`}
            >
              Dine In (Makan di Tempat)
            </button>
            <button
              onClick={() => onOrderTypeChange('TAKE_AWAY')}
              className={`px-3.5 py-1.5 rounded-lg text-xs font-bold transition-all ${
                orderType === 'TAKE_AWAY'
                  ? 'bg-brand-500 text-white shadow-sm'
                  : 'text-charcoal-muted hover:text-charcoal hover:bg-cream-50'
              }`}
            >
              Take Away (Bawa Pulang)
            </button>
          </div>
        </div>
      </div>
    </header>
  );
};
