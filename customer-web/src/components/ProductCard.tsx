import React from 'react';
import { Product } from '../types';
import { formatRupiah } from '../utils/format';
import { Plus } from 'lucide-react';

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
  return (
    <div
      onClick={() => product.isAvailable && onSelectProduct(product)}
      className={`bg-white rounded-2xl p-3 border border-cream-200 shadow-sm flex gap-3.5 transition-all ${
        product.isAvailable
          ? 'cursor-pointer hover:border-brand-300 hover:shadow-md'
          : 'opacity-60 cursor-not-allowed'
      }`}
    >
      {/* Product Image / Placeholder */}
      <div className="w-24 h-24 rounded-xl bg-cream-100 overflow-hidden shrink-0 relative">
        {product.image ? (
          <img
            src={product.image.startsWith('/') ? `http://localhost:3000${product.image}` : product.image}
            alt={product.name}
            className="w-full h-full object-cover"
            onError={(e) => {
              (e.target as HTMLElement).style.display = 'none';
            }}
          />
        ) : null}
        {!product.isAvailable && (
          <div className="absolute inset-0 bg-black/50 backdrop-blur-[1px] flex items-center justify-center p-1">
            <span className="text-[10px] font-bold text-white uppercase tracking-wider text-center">
              Habis
            </span>
          </div>
        )}
      </div>

      {/* Product Details */}
      <div className="flex-1 flex flex-col justify-between min-w-0">
        <div>
          <h3 className="font-bold text-sm text-charcoal line-clamp-1">{product.name}</h3>
          <p className="text-xs text-charcoal-muted mt-0.5 line-clamp-2 leading-relaxed">
            {product.description || 'Hidangan lezat siap disajikan.'}
          </p>
        </div>

        <div className="flex items-center justify-between mt-2 pt-1 border-t border-cream-100">
          <span className="font-extrabold text-sm text-brand-600">
            {formatRupiah(product.price)}
          </span>

          {product.isAvailable && (
            <button
              onClick={(e) => {
                e.stopPropagation();
                onSelectProduct(product);
              }}
              className={`w-7 h-7 rounded-full flex items-center justify-center transition-transform active:scale-95 ${
                cartQuantity > 0
                  ? 'bg-brand-500 text-white font-bold text-xs shadow-sm'
                  : 'bg-cream-100 text-charcoal hover:bg-brand-500 hover:text-white'
              }`}
            >
              {cartQuantity > 0 ? cartQuantity : <Plus className="w-4 h-4" />}
            </button>
          )}
        </div>
      </div>
    </div>
  );
};
