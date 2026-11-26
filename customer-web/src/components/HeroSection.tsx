import React from 'react';
import { Flame, Award, MapPin } from 'lucide-react';
import { OrderType } from '../types';

interface HeroSectionProps {
  tableNumber?: string | null;
  orderType?: OrderType;
}

export const HeroSection: React.FC<HeroSectionProps> = ({ tableNumber, orderType }) => {
  return (
    <section className="rounded-3xl bg-charcoal text-white p-5 sm:p-7 border border-charcoal shadow-card">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-5">
        <div className="space-y-2.5 max-w-xl">
          <div className="flex flex-wrap items-center gap-2">
            <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg bg-brand-500 text-white text-[11px] font-bold tracking-wide uppercase">
              Resep Rempah Nusantara
            </span>
            {orderType === 'DINE_IN' && tableNumber && (
              <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg bg-white/10 text-cream-100 text-[11px] font-semibold">
                <MapPin className="w-3 h-3 text-brand-300" />
                <span>Pemesanan {tableNumber}</span>
              </span>
            )}
          </div>

          <h2 className="text-xl sm:text-2xl lg:text-[26px] font-extrabold tracking-tight text-white leading-snug">
            AROMA – Ayam Goreng Lengkuas
          </h2>

          <p className="text-sm font-semibold text-brand-200">
            &ldquo;Gurih Rempahnya, Hangat Rasanya.&rdquo;
          </p>

          <p className="text-xs sm:text-sm font-body text-cream-200/90 leading-relaxed">
            Ayam goreng bumbu rempah pilihan dengan taburan lengkuas gurih renyah, disajikan hangat
            langsung dari dapur kami.
          </p>
        </div>

        <div className="flex sm:flex-col items-start sm:items-end justify-between gap-2.5 pt-3 sm:pt-0 border-t sm:border-t-0 border-white/10 shrink-0">
          <div className="inline-flex items-center gap-2 px-3 py-2 rounded-xl bg-white/[0.07] border border-white/10 text-xs font-semibold text-cream-100">
            <Award className="w-4 h-4 text-brand-300 shrink-0" />
            <span>100% Rempah Asli</span>
          </div>
          <div className="inline-flex items-center gap-2 px-3 py-2 rounded-xl bg-white/[0.07] border border-white/10 text-xs font-semibold text-cream-100">
            <Flame className="w-4 h-4 text-brand-300 shrink-0" />
            <span>Disajikan Hangat</span>
          </div>
        </div>
      </div>
    </section>
  );
};
