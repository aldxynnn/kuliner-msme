import React from 'react';
import { Product } from '../types';
import { formatRupiah } from '../utils/format';
import { getImageUrl } from '../api';
import { Plus, Check } from 'lucide-react';

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

  return (
    <div
      onClick={() => product.isAvailable && onSelectProduct(product)}
      className={`bg-white rounded-3xl p-3.5 border border-cream-200/80 shadow-sm flex gap-4 transition-all group ${
        product.isAvailable
          ? 'cursor-pointer hover:border-brand-400 hover:shadow-lg hover:-translate-y-0.5'
          : 'opacity-60 cursor-not-allowed bg-cream-50/50'
      }`}
    >
      {/* Product Image */}
      <div className="w-28 h-28 rounded-2xl bg-cream-100 overflow-hidden shrink-0 relative shadow-inner">
        {imageUrl ? (
          <img
            src={imageUrl}
            alt={product.name}
            className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
            onError={(e) => {
              (e.target as HTMLElement).style.display = 'none';
            }}
          />
        ) : null}
        {!product.isAvailable && (
          <div className="absolute inset-0 bg-black/60 backdrop-blur-xs flex items-center justify-center p-2">
            <span className="text-[11px] font-extrabold text-white uppercase tracking-wider text-center bg-black/40 px-3 py-1 rounded-full border border-white/20">
              Habis
            </span>
          </div>
        )}
      </div>

      {/* Product Info */}
      <div className="flex-1 flex flex-col justify-between min-w-0 py-0.5">
        <div>
          <div className="flex items-center gap-2">
            <span className="text-[10px] font-bold text-brand-600 uppercase tracking-wider bg-brand-50 px-2.5 py-0.5 rounded-md border border-brand-200/60">
              {product.category?.name || 'Menu'}
            </span>
          </div>

          <h3 className="font-extrabold text-sm md:text-base text-charcoal group-hover:text-brand-600 transition-colors line-clamp-1 mt-1">
            {product.name}
          </h3>

          <p className="text-xs text-charcoal-muted mt-1 line-clamp-2 leading-relaxed">
            {product.description || 'Hidangan istimewa AROMA yang disajikan segar.'}
          </p>
        </div>

        <div className="flex items-center justify-between mt-2 pt-2 border-t border-cream-100">
          <span className="font-extrabold text-sm md:text-base text-brand-600 tracking-tight">
            {formatRupiah(product.price)}
          </span>

          {product.isAvailable && (
            <button
              onClick={(e) => {
                e.stopPropagation();
                onSelectProduct(product);
              }}
              className={`px-3.5 py-2 rounded-2xl flex items-center gap-1.5 font-bold text-xs transition-all shadow-sm active:scale-95 ${
                cartQuantity > 0
                  ? 'bg-emerald-600 text-white shadow-emerald-600/20'
                  : 'bg-brand-500 text-white hover:bg-brand-600 shadow-brand-500/20'
              }`}
            >
              {cartQuantity > 0 ? (
                <>
                  <Check className="w-3.5 h-3.5" />
                  <span>{cartQuantity} di Keranjang</span>
                </>
              ) : (
                <>
                  <Plus className="w-3.5 h-3.5" />
                  <span>Tambah</span>
                </>
              )}
            </button>
          )}
        </div>
      </div>
    </div>
  );
};
