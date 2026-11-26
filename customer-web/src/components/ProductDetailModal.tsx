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
      className="fixed inset-0 z-50 bg-ink/65 backdrop-blur-sm flex items-end sm:items-center justify-center p-0 sm:p-4 animate-aroma-fade"
    >
      <div
        onClick={(e) => e.stopPropagation()}
        className="bg-white w-full sm:max-w-lg rounded-t-3xl sm:rounded-3xl border border-paper-200 overflow-hidden shadow-2xl max-h-[92vh] flex flex-col animate-aroma-sheet sm:animate-aroma-modal"
      >
        {/* Top Photo Showcase */}
        <div className="relative h-60 sm:h-72 bg-paper-100 shrink-0 flex items-center justify-center overflow-hidden">
          {showImage ? (
            <img
              src={imageUrl}
              alt={product.name}
              onError={() => setImgError(true)}
              className="w-full h-full object-cover"
            />
          ) : (
            <div className="flex flex-col items-center justify-center text-ink-muted gap-2">
              <UtensilsCrossed className="w-10 h-10 opacity-45" />
              <span className="font-mono text-xs uppercase tracking-widest">Menu AROMA</span>
            </div>
          )}

          {/* Mobile Drag Handle */}
          <div className="sm:hidden absolute top-2.5 inset-x-0 flex justify-center pointer-events-none">
            <div className="w-10 h-1 rounded-full bg-white/85 shadow-sm" />
          </div>

          <button
            type="button"
            onClick={onClose}
            aria-label="Tutup detail produk"
            className="absolute top-3.5 right-3.5 w-10 h-10 rounded-full bg-white/95 text-ink border border-paper-200 flex items-center justify-center hover:bg-paper-50 active:scale-95 transition-all shadow-sm"
          >
            <X className="w-5 h-5" />
          </button>

          {product.category?.name && (
            <div className="absolute bottom-3.5 left-4">
              <span className="inline-flex items-center px-3 py-1 rounded-md bg-ink/85 backdrop-blur-sm text-white font-mono text-[11px] font-semibold uppercase tracking-wider">
                {product.category.name}
              </span>
            </div>
          )}
        </div>

        {/* Editorial Body */}
        <div className="p-5 sm:p-6 overflow-y-auto space-y-5 flex-1 bg-white">
          <div>
            <div className="flex items-start justify-between gap-4">
              <h2
                id="product-detail-title"
                className="font-display font-bold text-xl sm:text-2xl text-ink leading-snug"
              >
                {product.name}
              </h2>
              <span className="font-mono font-bold text-lg sm:text-xl text-spice-500 shrink-0 tabular-nums">
                {formatRupiah(product.price)}
              </span>
            </div>
            <p className="text-sm text-ink-secondary mt-2 leading-relaxed">
              {product.description ||
                'Hidangan istimewa AROMA dengan bahan berkualitas tinggi dan rempah pilihan.'}
            </p>
          </div>

          {/* Kitchen Notes Input */}
          <div className="space-y-2">
            <label
              htmlFor="product-notes"
              className="text-xs font-bold text-ink flex items-center gap-1.5"
            >
              <FileText className="w-3.5 h-3.5 text-spice-500" />
              <span>Catatan untuk Dapur</span>
              <span className="font-normal text-ink-muted">(opsional)</span>
            </label>
            <input
              id="product-notes"
              type="text"
              placeholder="Contoh: Pedas sedang, pisah sambal, es sedikit..."
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              className="w-full px-4 py-3 rounded-xl border border-paper-200 bg-paper-50 text-sm text-ink placeholder:text-ink-light focus:outline-none focus:bg-white focus:border-ink transition-colors"
            />
          </div>

          {/* Quantity Selector Row */}
          <div className="flex items-center justify-between pt-4 receipt-edge">
            <div>
              <p className="text-xs font-bold text-ink uppercase tracking-wider">
                Jumlah Pesanan
              </p>
              <p className="font-mono text-xs text-ink-muted mt-0.5 tabular-nums">
                {formatRupiah(product.price)} / porsi
              </p>
            </div>
            <div className="flex items-center gap-2 bg-paper-100 p-1 rounded-full border border-paper-200">
              <button
                type="button"
                onClick={() => setQuantity((q) => Math.max(1, q - 1))}
                aria-label="Kurangi jumlah"
                disabled={quantity <= 1}
                className="w-10 h-10 rounded-full bg-white text-ink flex items-center justify-center hover:bg-paper-50 active:scale-95 transition-all disabled:opacity-40 border border-paper-200"
              >
                <Minus className="w-4 h-4" />
              </button>
              <span className="w-9 text-center font-mono font-bold text-base text-ink tabular-nums">
                {quantity}
              </span>
              <button
                type="button"
                onClick={() => setQuantity((q) => q + 1)}
                aria-label="Tambah jumlah"
                className="w-10 h-10 rounded-full bg-ink text-white flex items-center justify-center hover:bg-spice-500 active:scale-95 transition-colors"
              >
                <Plus className="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>

        {/* Sticky Checkout Add Footer */}
        <div className="p-4 sm:p-5 border-t border-paper-200 bg-paper-50 shrink-0">
          <button
            type="button"
            onClick={handleAdd}
            className="w-full min-h-[52px] py-3.5 px-5 rounded-xl bg-ink hover:bg-spice-500 text-white font-bold text-sm flex items-center justify-between gap-3 active:scale-[0.99] transition-colors"
          >
            <span className="inline-flex items-center gap-2">
              <ShoppingBag className="w-4 h-4" />
              <span>Tambah ke Pesanan</span>
            </span>
            <span className="font-mono font-bold tabular-nums">
              {formatRupiah(product.price * quantity)}
            </span>
          </button>
        </div>
      </div>
    </div>
  );
};
