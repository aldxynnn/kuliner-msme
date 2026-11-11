import React, { useState } from 'react';
import { Product } from '../types';
import { formatRupiah } from '../utils/format';
import { X, Minus, Plus, ShoppingBag } from 'lucide-react';

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
    <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex items-end sm:items-center justify-center p-0 sm:p-4">
      <div className="bg-white w-full max-w-lg rounded-t-3xl sm:rounded-3xl overflow-hidden shadow-2xl animate-in slide-in-from-bottom duration-200">
        {/* Header Image */}
        <div className="relative h-48 bg-cream-100">
          {product.image && (
            <img
              src={product.image.startsWith('/') ? `http://localhost:3000${product.image}` : product.image}
              alt={product.name}
              className="w-full h-full object-cover"
            />
          )}
          <button
            onClick={onClose}
            className="absolute top-4 right-4 w-9 h-9 rounded-full bg-black/40 text-white flex items-center justify-center backdrop-blur-md hover:bg-black/60 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Body */}
        <div className="p-5 space-y-4">
          <div>
            <h2 className="font-bold text-xl text-charcoal">{product.name}</h2>
            <p className="text-sm text-charcoal-muted mt-1 leading-relaxed">
              {product.description || 'Hidangan lezat dan segar untuk dinikmati.'}
            </p>
            <p className="text-lg font-extrabold text-brand-600 mt-2">
              {formatRupiah(product.price)}
            </p>
          </div>

          {/* Notes Input */}
          <div>
            <label className="block text-xs font-semibold text-charcoal mb-1.5">
              Catatan Pesanan (opsional)
            </label>
            <input
              type="text"
              placeholder="Contoh: Pedas sedang, tanpa daun bawang, es sedikit..."
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              className="w-full px-3.5 py-2.5 rounded-xl border border-cream-200 focus:outline-none focus:border-brand-500 text-xs bg-cream-50"
            />
          </div>

          {/* Quantity Controls */}
          <div className="flex items-center justify-between pt-2 border-t border-cream-200">
            <span className="text-xs font-semibold text-charcoal">Jumlah Item</span>
            <div className="flex items-center gap-3 bg-cream-100 p-1.5 rounded-xl">
              <button
                onClick={() => setQuantity((q) => Math.max(1, q - 1))}
                className="w-8 h-8 rounded-lg bg-white text-charcoal flex items-center justify-center font-bold hover:bg-cream-200 shadow-sm"
              >
                <Minus className="w-4 h-4" />
              </button>
              <span className="w-8 text-center font-bold text-sm text-charcoal">
                {quantity}
              </span>
              <button
                onClick={() => setQuantity((q) => q + 1)}
                className="w-8 h-8 rounded-lg bg-white text-charcoal flex items-center justify-center font-bold hover:bg-cream-200 shadow-sm"
              >
                <Plus className="w-4 h-4" />
              </button>
            </div>
          </div>

          {/* Action Button */}
          <button
            onClick={handleAdd}
            className="w-full py-3.5 px-4 rounded-xl bg-brand-500 hover:bg-brand-600 text-white font-bold text-sm shadow-md flex items-center justify-center gap-2 active:scale-[0.99] transition-all"
          >
            <ShoppingBag className="w-4 h-4" />
            <span>Tambah ke Keranjang - {formatRupiah(product.price * quantity)}</span>
          </button>
        </div>
      </div>
    </div>
  );
};
