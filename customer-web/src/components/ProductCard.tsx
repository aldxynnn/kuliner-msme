import React, { useState } from 'react';
import { Product } from '../types';
import { formatRupiah } from '../utils/format';
import { getImageUrl } from '../api';
import { Plus, Check, UtensilsCrossed } from 'lucide-react';

interface ProductCardProps {
  product: Product;
  cartQuantity: number;
  onSelectProduct: (product: Product) => void;
}

export const ProductCard: React.FC<ProductCardProps> = ({
  product,
  cartQuantity,
  onSelectProduct,
}) => {
  const imageUrl = getImageUrl(product.image);
  const [imgError, setImgError] = useState(false);

  const showImage = Boolean(imageUrl) && !imgError;

  return (
    <article
      onClick={() => product.isAvailable && onSelectProduct(product)}
      className={`bg-white rounded-2xl border border-paper-200 overflow-hidden flex flex-row sm:flex-col justify-between transition-all duration-200 group ${
        product.isAvailable
          ? 'cursor-pointer hover:border-ink/30 hover:shadow-card-hover'
          : 'opacity-65 cursor-not-allowed bg-paper-50'
      }`}
    >
      {/* Desktop Top Editorial Image (sm+) */}
      <div className="hidden sm:flex relative w-full aspect-[4/3] bg-paper-100 overflow-hidden shrink-0 items-center justify-center">
        {showImage ? (
          <img
            src={imageUrl}
            alt={product.name}
            loading="lazy"
            className={`w-full h-full object-cover transition-transform duration-500 ${
              product.isAvailable ? 'group-hover:scale-[1.04]' : 'grayscale'
            }`}
            onError={() => setImgError(true)}
          />
        ) : (
          <div className="flex flex-col items-center justify-center text-ink-light p-4">
            <UtensilsCrossed className="w-8 h-8 opacity-50" />
          </div>
        )}

        {product.category?.name && (
          <span className="absolute top-3 left-3 px-2.5 py-1 rounded-md bg-ink/75 backdrop-blur-sm text-white font-mono text-[10px] font-semibold uppercase tracking-wider">
            {product.category.name}
          </span>
        )}

        {product.isAvailable && cartQuantity > 0 && (
          <span className="inline-flex items-center gap-1 absolute top-3 right-3 px-2.5 py-1 rounded-md bg-pandan-600 text-white text-[11px] font-mono font-bold shadow-sm tabular-nums">
            <Check className="w-3 h-3" />
            <span>{cartQuantity} di tiket</span>
          </span>
        )}

        {!product.isAvailable && (
          <div className="absolute inset-0 bg-paper-50/75 backdrop-blur-[1px] flex items-center justify-center p-2">
            <span className="font-mono text-[11px] font-bold text-ink uppercase tracking-widest px-3 py-1 rounded-md bg-white border border-paper-300 shadow-sm">
              Habis
            </span>
          </div>
        )}
      </div>

      {/* Product Typography Body */}
      <div className="flex-1 flex flex-col justify-between min-w-0 p-3.5 sm:p-5">
        <div>
          {product.category?.name && (
            <p className="sm:hidden font-mono text-[10px] font-semibold text-spice-600 uppercase tracking-[0.1em]">
              {product.category.name}
            </p>
          )}

          <h3 className="font-bold text-[15px] sm:text-base text-ink group-hover:text-spice-600 transition-colors leading-snug line-clamp-2 mt-0.5 sm:mt-0">
            {product.name}
          </h3>

          <p className="text-xs sm:text-[13px] text-ink-secondary mt-1.5 line-clamp-2 leading-relaxed">
            {product.description || 'Hidangan istimewa AROMA yang disajikan hangat dan segar.'}
          </p>
        </div>

        <div className="flex items-center justify-between gap-2 mt-3 sm:mt-4 sm:pt-3.5 sm:border-t sm:border-paper-100">
          <span className="font-mono font-bold text-[15px] sm:text-base text-ink tabular-nums tracking-tight">
            {formatRupiah(product.price)}
          </span>

          {/* Desktop Action CTA */}
          {product.isAvailable ? (
            <button
              type="button"
              onClick={(e) => {
                e.stopPropagation();
                onSelectProduct(product);
              }}
              aria-label={`Tambah ${product.name}`}
              className={`hidden sm:inline-flex min-h-[38px] px-3.5 py-1.5 rounded-full items-center gap-1.5 font-bold text-xs transition-all active:scale-95 shrink-0 ${
                cartQuantity > 0
                  ? 'bg-pandan-600 text-white hover:bg-pandan-700'
                  : 'bg-ink text-white hover:bg-spice-500'
              }`}
            >
              {cartQuantity > 0 ? (
                <>
                  <Check className="w-3.5 h-3.5" />
                  <span className="font-mono tabular-nums">{cartQuantity}×</span>
                </>
              ) : (
                <>
                  <Plus className="w-3.5 h-3.5" />
                  <span>Tambah</span>
                </>
              )}
            </button>
          ) : (
            <span className="font-mono text-[11px] font-semibold text-ink-muted uppercase tracking-wider">
              Habis
            </span>
          )}
        </div>
      </div>

      {/* Mobile Right Thumbnail with Overlaid Bottom Action Pill (< sm) */}
      <div className="sm:hidden relative w-[104px] h-[104px] m-3.5 ml-0 rounded-xl bg-paper-100 border border-paper-200/80 overflow-hidden shrink-0 flex items-center justify-center">
        {showImage ? (
          <img
            src={imageUrl}
            alt={product.name}
            loading="lazy"
            className={`w-full h-full object-cover ${product.isAvailable ? '' : 'grayscale'}`}
            onError={() => setImgError(true)}
          />
        ) : (
          <UtensilsCrossed className="w-6 h-6 text-ink-light opacity-60" />
        )}

        {product.isAvailable ? (
          <button
            type="button"
            onClick={(e) => {
              e.stopPropagation();
              onSelectProduct(product);
            }}
            aria-label={`Tambah ${product.name}`}
            className={`absolute bottom-1.5 inset-x-1.5 min-h-[32px] px-2 py-1 rounded-lg inline-flex items-center justify-center gap-1 font-bold text-[11px] shadow-md transition-all active:scale-95 ${
              cartQuantity > 0
                ? 'bg-pandan-600 text-white'
                : 'bg-ink/95 text-white hover:bg-spice-500'
            }`}
          >
            {cartQuantity > 0 ? (
              <>
                <Check className="w-3 h-3 shrink-0" />
                <span className="font-mono tabular-nums">{cartQuantity}×</span>
              </>
            ) : (
              <>
                <Plus className="w-3 h-3 shrink-0" />
                <span>Tambah</span>
              </>
            )}
          </button>
        ) : (
          <div className="absolute inset-0 bg-paper-50/75 flex items-center justify-center">
            <span className="font-mono text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded bg-white border border-paper-300 text-ink">
              Habis
            </span>
          </div>
        )}
      </div>
    </article>
  );
};
