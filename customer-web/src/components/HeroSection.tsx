import React from 'react';
import { MapPin } from 'lucide-react';
import { OrderType } from '../types';

interface HeroSectionProps {
  tableNumber?: string | null;
  orderType?: OrderType;
}

export const HeroSection: React.FC<HeroSectionProps> = ({ tableNumber, orderType }) => {
  return (
    <section className="rounded-2xl bg-white border border-cream-200 px-5 py-5 sm:px-7 sm:py-6 shadow-card">
      <div className="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-4">
        <div className="space-y-2 max-w-2xl">
          <div className="flex flex-wrap items-center gap-2">
            <span className="text-[11px] font-bold tracking-[0.12em] uppercase text-brand-600">
              Ayam Goreng Lengkuas &bull; Resep Nusantara
            </span>
            {orderType === 'DINE_IN' && tableNumber && (
              <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-md bg-brand-50 border border-brand-200 text-brand-700 text-[11px] font-bold">
                <MapPin className="w-3 h-3 text-brand-500 shrink-0" />
                <span>Pemesanan {tableNumber}</span>
              </span>
            )}
          </div>

          <h2 className="font-display font-bold text-2xl sm:text-3xl text-charcoal tracking-tight leading-[1.15]">
            Gurih Rempahnya, Hangat Rasanya.
          </h2>

          <p className="text-xs sm:text-sm font-body text-charcoal-secondary leading-relaxed max-w-xl">
            Hidangan ayam goreng bumbu rempah pilihan dengan taburan lengkuas gurih renyah, disajikan hangat langsung dari dapur AROMA.
          </p>
        </div>

        <div className="flex flex-wrap items-center gap-2 text-[11px] font-semibold text-charcoal-muted pt-3 sm:pt-0 border-t sm:border-t-0 border-cream-200/80 shrink-0">
          <span className="px-2.5 py-1 rounded-md bg-cream-50 border border-cream-200">100% Rempah Asli</span>
          <span className="px-2.5 py-1 rounded-md bg-cream-50 border border-cream-200">Disajikan Hangat</span>
          <span className="px-2.5 py-1 rounded-md bg-cream-50 border border-cream-200">Serang, Banten</span>
        </div>
      </div>
    </section>
  );
};
