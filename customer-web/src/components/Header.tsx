import React from 'react';
import { UtensilsCrossed, MapPin, ShoppingBag, Clock } from 'lucide-react';
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
    <header className="sticky top-0 z-20 bg-white border-b border-cream-200 shadow-sm">
      <div className="max-w-xl mx-auto px-4 py-3 flex items-center justify-between">
        {/* Brand */}
        <div className="flex items-center gap-2.5">
          <div className="w-10 h-10 rounded-xl bg-brand-500 text-white flex items-center justify-center shadow-sm">
            <UtensilsCrossed className="w-5 h-5" />
          </div>
          <div>
            <h1 className="font-bold text-base text-charcoal leading-tight">AROMA</h1>
            <p className="text-[11px] text-charcoal-muted">Gurih Rempahnya, Hangat Rasanya.</p>
          </div>
        </div>

        {/* Status / Active Order / Table */}
        <div className="flex items-center gap-2">
          {activeOrderNumber && (
            <button
              onClick={onOpenOrderTracking}
              className="flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-amber-50 text-amber-800 border border-amber-200 text-xs font-semibold hover:bg-amber-100 transition-colors"
            >
              <Clock className="w-3.5 h-3.5 animate-pulse text-amber-600" />
              <span>Status Pesanan</span>
            </button>
          )}

          {tableNumber && (
            <div className="flex items-center gap-1 px-3 py-1.5 rounded-full bg-brand-50 text-brand-700 border border-brand-200 text-xs font-semibold">
              <MapPin className="w-3.5 h-3.5" />
              <span>{tableNumber}</span>
            </div>
          )}
        </div>
      </div>

      {/* Order Type Toggle Banner */}
      <div className="bg-cream-50 border-t border-cream-200 py-2 px-4">
        <div className="max-w-xl mx-auto flex items-center justify-between">
          <span className="text-xs font-medium text-charcoal-muted">Tipe Pesanan:</span>
          <div className="bg-white p-1 rounded-lg border border-cream-200 flex gap-1 text-xs">
            <button
              onClick={() => onOrderTypeChange('DINE_IN')}
              className={`px-3 py-1.5 rounded-md font-semibold transition-all ${
                orderType === 'DINE_IN'
                  ? 'bg-brand-500 text-white shadow-sm'
                  : 'text-charcoal-muted hover:text-charcoal'
              }`}
            >
              Dine In (Makan di Tempat)
            </button>
            <button
              onClick={() => onOrderTypeChange('TAKE_AWAY')}
              className={`px-3 py-1.5 rounded-md font-semibold transition-all ${
                orderType === 'TAKE_AWAY'
                  ? 'bg-brand-500 text-white shadow-sm'
                  : 'text-charcoal-muted hover:text-charcoal'
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
