import React, { useState } from 'react';
import { Product } from '../types';
import { formatRupiah } from '../utils/format';
import { X, Minus, Plus, ShoppingBag, FileText } from 'lucide-react';

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
  if (!product) return null;

  const [quantity, setQuantity] = useState(initialQuantity);
  const [notes, setNotes] = useState(initialNotes);

  const handleAdd = () => {
    onAddToCart(product, quantity, notes);
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 bg-black/65 backdrop-blur-sm flex items-end sm:items-center justify-center p-0 sm:p-4">
      <div className="bg-white w-full max-w-lg rounded-t-[32px] sm:rounded-3xl overflow-hidden shadow-2xl animate-in slide-in-from-bottom duration-200">
        {/* Header Image */}
        <div className="relative h-56 sm:h-64 bg-cream-100">
          {product.image && (
            <img
              src={product.image.startsWith('/') ? `http://localhost:3000${product.image}` : product.image}
              alt={product.name}
              className="w-full h-full object-cover"
            />
          )}
          <div className="absolute inset-0 bg-gradient-to-t from-black/50 via-transparent to-transparent" />
          <button
            onClick={onClose}
            className="absolute top-4 right-4 w-10 h-10 rounded-full bg-black/50 text-white flex items-center justify-center backdrop-blur-md hover:bg-black/70 transition-colors shadow-md"
          >
            <X className="w-5 h-5" />
          </button>

          <div className="absolute bottom-4 left-4 right-4">
            <span className="text-[10px] font-bold text-white uppercase tracking-wider bg-brand-500 px-3 py-1 rounded-full shadow-sm">
              {product.category?.name || 'Menu AROMA'}
            </span>
          </div>
        </div>

        {/* Body */}
        <div className="p-6 space-y-5">
          <div>
            <div className="flex items-start justify-between gap-4">
              <h2 className="font-extrabold text-xl text-charcoal">{product.name}</h2>
              <span className="font-extrabold text-xl text-brand-600 shrink-0">
                {formatRupiah(product.price)}
              </span>
            </div>
            <p className="text-sm text-charcoal-muted mt-2 leading-relaxed">
              {product.description || 'Hidangan istimewa AROMA dengan bahan berkualitas tinggi dan rempah pilihan.'}
            </p>
          </div>

          {/* Notes Input */}
          <div className="space-y-1.5">
            <label className="block text-xs font-bold text-charcoal flex items-center gap-1.5">
              <FileText className="w-3.5 h-3.5 text-brand-500" />
              <span>Catatan Khusus (opsional)</span>
            </label>
            <input
              type="text"
              placeholder="Contoh: Pedas sedang, pisah sambal, es sedikit..."
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              className="w-full px-4 py-3 rounded-2xl border border-cream-200 focus:outline-none focus:border-brand-500 text-xs bg-cream-50/60 font-medium"
            />
          </div>

          {/* Quantity Controls */}
          <div className="flex items-center justify-between pt-2 border-t border-cream-200">
            <span className="text-xs font-bold text-charcoal uppercase tracking-wider">Jumlah Pesanan</span>
            <div className="flex items-center gap-3 bg-cream-100/80 p-1.5 rounded-2xl border border-cream-200">
              <button
                onClick={() => setQuantity((q) => Math.max(1, q - 1))}
                className="w-9 h-9 rounded-xl bg-white text-charcoal flex items-center justify-center font-bold hover:bg-cream-200 shadow-xs transition-transform active:scale-95"
              >
                <Minus className="w-4 h-4" />
              </button>
              <span className="w-8 text-center font-extrabold text-sm text-charcoal">
                {quantity}
              </span>
              <button
                onClick={() => setQuantity((q) => q + 1)}
                className="w-9 h-9 rounded-xl bg-white text-charcoal flex items-center justify-center font-bold hover:bg-cream-200 shadow-xs transition-transform active:scale-95"
              >
                <Plus className="w-4 h-4" />
              </button>
            </div>
          </div>

          {/* Action Button */}
          <button
            onClick={handleAdd}
            className="w-full py-4 px-5 rounded-2xl bg-brand-500 hover:bg-brand-600 text-white font-extrabold text-sm shadow-lg shadow-brand-500/25 flex items-center justify-center gap-2.5 active:scale-[0.99] transition-all"
          >
            <ShoppingBag className="w-5 h-5" />
            <span>Tambah ke Keranjang • {formatRupiah(product.price * quantity)}</span>
          </button>
        </div>
      </div>
    </div>
  );
};
