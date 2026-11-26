import React, { useEffect, useState } from 'react';
import { Product } from '../types';
import { formatRupiah } from '../utils/format';
import { getImageUrl } from '../api';
import { X, Minus, Plus, ShoppingBag, FileText, UtensilsCrossed } from 'lucide-react';

interface ProductDetailModalProps {
  product: Product | null;
  onClose: () => void;
  onAddToCart: (product: Product, quantity: number, notes: string) => void;
  initialQuantity?: number;
  initialNotes?: string;
}

export const ProductDetailModal: React.FC<ProductDetailModalProps> = ({
  product,
  onClose,
  onAddToCart,
  initialQuantity = 1,
  initialNotes = '',
}) => {
  const [quantity, setQuantity] = useState(initialQuantity);
  const [notes, setNotes] = useState(initialNotes);
  const [imgError, setImgError] = useState(false);

  useEffect(() => {
    if (product) {
      setQuantity(initialQuantity);
      setNotes(initialNotes);
      setImgError(false);
    }
  }, [product?.id, initialQuantity, initialNotes]);

  useEffect(() => {
    if (!product) return;
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [product, onClose]);

  if (!product) return null;

  const imageUrl = getImageUrl(product.image);
  const showImage = Boolean(imageUrl) && !imgError;

  const handleAdd = () => {
    onAddToCart(product, quantity, notes);
    onClose();
  };

  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-labelledby="product-detail-title"
      onClick={onClose}
      className="fixed inset-0 z-50 bg-charcoal/60 backdrop-blur-sm flex items-end sm:items-center justify-center p-0 sm:p-4 animate-aroma-fade"
    >
      <div
        onClick={(e) => e.stopPropagation()}
        className="bg-white w-full sm:max-w-lg rounded-t-3xl sm:rounded-3xl overflow-hidden shadow-sheet sm:shadow-2xl max-h-[92vh] flex flex-col animate-aroma-sheet sm:animate-aroma-modal"
      >
        {/* Top Image Banner */}
        <div className="relative h-56 sm:h-64 bg-cream-50 shrink-0 flex items-center justify-center overflow-hidden">
          {showImage ? (
            <img
              src={imageUrl}
              alt={product.name}
              onError={() => setImgError(true)}
              className="w-full h-full object-cover"
            />
          ) : (
            <div className="flex flex-col items-center justify-center text-charcoal-light gap-2">
              <UtensilsCrossed className="w-10 h-10 opacity-50" />
              <span className="text-xs font-semibold">Menu AROMA</span>
            </div>
          )}

          {/* Mobile Drag Handle Indicator */}
          <div className="sm:hidden absolute top-2.5 inset-x-0 flex justify-center pointer-events-none">
            <div className="w-10 h-1 rounded-full bg-white/80 shadow-sm" />
          </div>

          <button
            type="button"
            onClick={onClose}
            aria-label="Tutup detail produk"
            className="absolute top-3.5 right-3.5 w-10 h-10 rounded-full bg-white/95 text-charcoal border border-cream-200 flex items-center justify-center hover:bg-cream-50 active:scale-95 transition-all shadow-sm"
          >
            <X className="w-5 h-5" />
          </button>

          {product.category?.name && (
            <div className="absolute bottom-3.5 left-4">
              <span className="inline-flex items-center px-3 py-1 rounded-lg bg-charcoal/85 text-white text-[11px] font-bold uppercase tracking-wider">
                {product.category.name}
              </span>
            </div>
          )}
        </div>

        {/* Scrollable Details */}
        <div className="p-5 sm:p-6 overflow-y-auto space-y-5 flex-1">
          <div>
            <div className="flex items-start justify-between gap-4">
              <h2
                id="product-detail-title"
                className="font-extrabold text-lg sm:text-xl text-charcoal leading-snug"
              >
                {product.name}
              </h2>
              <span className="font-extrabold text-lg sm:text-xl text-brand-500 shrink-0 tabular-nums">
                {formatRupiah(product.price)}
              </span>
            </div>
            <p className="text-sm font-body text-charcoal-muted mt-2 leading-relaxed">
              {product.description ||
                'Hidangan istimewa AROMA dengan bahan berkualitas tinggi dan rempah pilihan.'}
            </p>
          </div>

          {/* Notes Field */}
          <div className="space-y-2">
            <label
              htmlFor="product-notes"
              className="text-xs font-bold text-charcoal flex items-center gap-1.5"
            >
              <FileText className="w-3.5 h-3.5 text-brand-500" />
              <span>Catatan Khusus</span>
              <span className="font-medium text-charcoal-muted">(opsional)</span>
            </label>
            <input
              id="product-notes"
              type="text"
              placeholder="Contoh: Pedas sedang, pisah sambal, es sedikit..."
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              className="w-full px-4 py-3 rounded-xl border border-cream-200 bg-cream-50 text-sm text-charcoal placeholder:text-charcoal-light focus:outline-none focus:bg-white focus:border-brand-500 focus:ring-2 focus:ring-brand-500/15 transition-all"
            />
          </div>

          {/* Quantity Stepper */}
          <div className="flex items-center justify-between pt-4 border-t border-cream-200">
            <div>
              <p className="text-xs font-bold text-charcoal uppercase tracking-wider">
                Jumlah Pesanan
              </p>
              <p className="text-xs text-charcoal-muted mt-0.5 tabular-nums">
                {formatRupiah(product.price)} / porsi
              </p>
            </div>
            <div className="flex items-center gap-2 bg-cream-100 p-1 rounded-xl border border-cream-200">
              <button
                type="button"
                onClick={() => setQuantity((q) => Math.max(1, q - 1))}
                aria-label="Kurangi jumlah"
                disabled={quantity <= 1}
                className="w-10 h-10 rounded-lg bg-white text-charcoal flex items-center justify-center hover:bg-cream-50 active:scale-95 transition-all disabled:opacity-40 border border-cream-200/70"
              >
                <Minus className="w-4 h-4" />
              </button>
              <span className="w-10 text-center font-extrabold text-base text-charcoal tabular-nums">
                {quantity}
              </span>
              <button
                type="button"
                onClick={() => setQuantity((q) => q + 1)}
                aria-label="Tambah jumlah"
                className="w-10 h-10 rounded-lg bg-brand-500 text-white flex items-center justify-center hover:bg-brand-600 active:scale-95 transition-all"
              >
                <Plus className="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>

        {/* Sticky Primary Action Footer */}
        <div className="p-4 sm:p-5 border-t border-cream-200 bg-cream-50 shrink-0">
          <button
            type="button"
            onClick={handleAdd}
            className="w-full min-h-[52px] py-3.5 px-5 rounded-xl bg-brand-500 hover:bg-brand-600 text-white font-bold text-sm flex items-center justify-between gap-3 active:scale-[0.99] transition-all shadow-sm"
          >
            <span className="inline-flex items-center gap-2">
              <ShoppingBag className="w-4 h-4" />
              <span>Tambah ke Keranjang</span>
            </span>
            <span className="font-extrabold tabular-nums">
              {formatRupiah(product.price * quantity)}
            </span>
          </button>
        </div>
      </div>
    </div>
  );
};
