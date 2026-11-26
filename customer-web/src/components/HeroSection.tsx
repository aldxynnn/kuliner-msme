import React, { useEffect, useState } from 'react';
import { ArrowRight, MapPin, UtensilsCrossed } from 'lucide-react';
import { OrderType, Product } from '../types';
import { getImageUrl } from '../api';
import { formatRupiah } from '../utils/format';

interface HeroSectionProps {
  tableNumber?: string | null;
  orderType?: OrderType;
  featuredProduct?: Product | null;
  onSelectFeatured?: (product: Product) => void;
}

export const HeroSection: React.FC<HeroSectionProps> = ({
  tableNumber,
  orderType,
  featuredProduct,
  onSelectFeatured,
}) => {
  const [imgError, setImgError] = useState(false);

  useEffect(() => {
    setImgError(false);
  }, [featuredProduct?.id, featuredProduct?.image]);

  const featuredImageUrl = featuredProduct ? getImageUrl(featuredProduct.image) : undefined;
  const showFeaturedImage = Boolean(featuredImageUrl) && !imgError;

  return (
    <section
      aria-label="Pengantar Menu AROMA"
      className="rounded-2xl bg-white border border-paper-200 overflow-hidden"
    >
      <div className="grid grid-cols-1 lg:grid-cols-[1.25fr_0.95fr]">
        {/* Left Editorial Atelier Panel */}
        <div className="p-5 sm:p-8 lg:p-9 flex flex-col justify-between gap-6">
          <div className="space-y-3">
            <div className="flex flex-wrap items-center gap-2.5">
              <span className="text-xs font-bold uppercase tracking-wider text-spice-600">
                Dapur Nusantara &bull; Serang, Banten
              </span>
              {orderType === 'DINE_IN' && tableNumber && (
                <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full bg-spice-50 border border-spice-200 text-spice-700 text-[11px] font-bold">
                  <MapPin className="w-3 h-3 shrink-0" />
                  <span>{tableNumber}</span>
                </span>
              )}
            </div>

            <h2 className="font-display font-extrabold text-[26px] sm:text-4xl lg:text-[40px] text-ink tracking-tight leading-[1.12]">
              Gurih Rempah Lengkuas,{' '}
              <span className="text-spice-500">Hangat Setiap Sajian.</span>
            </h2>

            <p className="text-xs sm:text-sm text-ink-secondary leading-relaxed max-w-xl">
              Racikan ayam goreng ungkep rempah asli dengan taburan lengkuas keemasan yang renyah, disajikan hangat langsung dari dapur AROMA.
            </p>
          </div>

          <div className="pt-4 border-t border-paper-200 flex flex-wrap items-center justify-between gap-3">
            <div className="flex items-center gap-2 text-xs font-semibold text-ink">
              <span className="w-2 h-2 rounded-full bg-spice-500 shrink-0" />
              <span>
                {orderType === 'DINE_IN'
                  ? `Dine In${tableNumber ? ` · ${tableNumber}` : ' · Pilih Meja Saat Checkout'}`
                  : 'Take Away · Siap Diambil di Kasir'}
              </span>
            </div>

            <p className="text-xs text-ink-muted font-medium">
              100% Rempah Asli &middot; Goreng Segar &middot; Pesan Cepat
            </p>
          </div>
        </div>

        {/* Right Live Featured Dish Showcase Panel */}
        <div
          onClick={() => featuredProduct && onSelectFeatured?.(featuredProduct)}
          role={featuredProduct && onSelectFeatured ? 'button' : undefined}
          tabIndex={featuredProduct && onSelectFeatured ? 0 : undefined}
          onKeyDown={(e) => {
            if (featuredProduct && onSelectFeatured && (e.key === 'Enter' || e.key === ' ')) {
              e.preventDefault();
              onSelectFeatured(featuredProduct);
            }
          }}
          className={`relative min-h-[210px] sm:min-h-[240px] lg:min-h-[272px] bg-spice-50 border-t lg:border-t-0 lg:border-l border-paper-200 overflow-hidden group ${
            featuredProduct && onSelectFeatured ? 'cursor-pointer' : ''
          }`}
        >
          {showFeaturedImage ? (
            <img
              src={featuredImageUrl}
              alt={featuredProduct?.name || 'Menu Unggulan AROMA'}
              className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-[1.04]"
              onError={() => setImgError(true)}
            />
          ) : (
            <div className="w-full h-full flex flex-col items-center justify-center text-ink-muted gap-2 p-6">
              <div className="w-12 h-12 rounded-2xl bg-white border border-spice-200 flex items-center justify-center">
                <UtensilsCrossed className="w-6 h-6 text-spice-500" />
              </div>
              <span className="text-xs font-semibold text-ink-muted">
                Sajian Khas AROMA
              </span>
            </div>
          )}

          {featuredProduct && (
            <div className="absolute inset-x-0 bottom-0 bg-gradient-to-t from-black/75 via-black/35 to-transparent p-4 sm:p-5 flex items-end justify-between gap-3">
              <div className="min-w-0 text-white">
                <span className="inline-block text-[10px] uppercase tracking-wider text-spice-200 font-bold">
                  Menu Unggulan
                </span>
                <h3 className="font-display font-bold text-base sm:text-lg leading-snug truncate mt-0.5">
                  {featuredProduct.name}
                </h3>
                <p className="text-xs sm:text-sm font-bold text-white/95 tabular-nums mt-0.5">
                  {formatRupiah(featuredProduct.price)}
                </p>
              </div>

              {onSelectFeatured && (
                <span className="inline-flex items-center gap-1.5 px-3.5 py-2 rounded-full bg-spice-500 text-white font-bold text-xs shrink-0 shadow-sm group-hover:bg-spice-600 transition-colors">
                  <span>Pesan</span>
                  <ArrowRight className="w-3.5 h-3.5" />
                </span>
              )}
            </div>
          )}
        </div>
      </div>
    </section>
  );
};
