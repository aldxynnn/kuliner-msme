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
      className={`bg-white rounded-2xl border border-cream-200 overflow-hidden shadow-card flex flex-row sm:flex-col transition-all duration-200 group ${
        product.isAvailable
          ? 'cursor-pointer hover:border-brand-300 hover:shadow-card-hover'
          : 'opacity-70 cursor-not-allowed bg-cream-50/70'
      }`}
    >
      {/* Product Photo */}
      <div className="relative w-28 h-28 m-3 sm:m-0 sm:w-full sm:h-auto sm:aspect-[4/3] rounded-xl sm:rounded-none bg-cream-50 overflow-hidden shrink-0 flex items-center justify-center">
        {showImage ? (
          <img
            src={imageUrl}
            alt={product.name}
            loading="lazy"
            className={`w-full h-full object-cover transition-transform duration-300 ${
              product.isAvailable ? 'group-hover:scale-[1.04]' : 'grayscale'
            }`}
            onError={() => setImgError(true)}
          />
        ) : (
          <div className="flex flex-col items-center justify-center text-charcoal-light p-3">
            <UtensilsCrossed className="w-7 h-7 opacity-60" />
          </div>
        )}

        {/* Quantity Badge on Desktop Image */}
        {product.isAvailable && cartQuantity > 0 && (
          <span className="hidden sm:inline-flex items-center gap-1 absolute top-3 right-3 px-2.5 py-1 rounded-lg bg-semantic-success text-white text-xs font-bold shadow-sm tabular-nums">
            <Check className="w-3.5 h-3.5" />
            <span>{cartQuantity} di keranjang</span>
          </span>
        )}

        {/* Sold Out Overlay */}
        {!product.isAvailable && (
          <div className="absolute inset-0 bg-charcoal/55 flex items-center justify-center p-2">
            <span className="text-[11px] font-extrabold text-white uppercase tracking-wider px-3 py-1 rounded-lg bg-charcoal/80 border border-white/20">
              Habis
            </span>
          </div>
        )}
      </div>

      {/* Product Info */}
      <div className="flex-1 flex flex-col justify-between min-w-0 py-3 pr-3 sm:p-4">
        <div>
          {product.category?.name && (
            <p className="text-[11px] font-bold text-brand-500 uppercase tracking-wider">
              {product.category.name}
            </p>
          )}

          <h3 className="font-bold text-[15px] sm:text-base text-charcoal group-hover:text-brand-600 transition-colors leading-snug line-clamp-2 mt-0.5">
            {product.name}
          </h3>

          <p className="text-xs sm:text-[13px] font-body text-charcoal-muted mt-1 line-clamp-2 leading-relaxed">
            {product.description || 'Hidangan istimewa AROMA yang disajikan hangat dan segar.'}
          </p>
        </div>

        <div className="flex items-center justify-between gap-2 mt-3 pt-2.5 border-t border-cream-100">
          <span className="font-extrabold text-[15px] sm:text-base text-charcoal tabular-nums tracking-tight">
            {formatRupiah(product.price)}
          </span>

          {product.isAvailable ? (
            <button
              type="button"
              onClick={(e) => {
                e.stopPropagation();
                onSelectProduct(product);
              }}
              aria-label={`Tambah ${product.name}`}
              className={`min-h-[38px] px-3.5 py-1.5 rounded-xl inline-flex items-center gap-1.5 font-bold text-xs transition-all active:scale-95 shrink-0 ${
                cartQuantity > 0
                  ? 'bg-semantic-success text-white hover:brightness-95'
                  : 'bg-brand-500 text-white hover:bg-brand-600'
              }`}
            >
              {cartQuantity > 0 ? (
                <>
                  <Check className="w-3.5 h-3.5" />
                  <span className="tabular-nums">{cartQuantity} item</span>
                </>
              ) : (
                <>
                  <Plus className="w-3.5 h-3.5" />
                  <span>Tambah</span>
                </>
              )}
            </button>
          ) : (
            <span className="text-xs font-bold text-charcoal-light">Tidak tersedia</span>
          )}
        </div>
      </div>
    </article>
  );
};
