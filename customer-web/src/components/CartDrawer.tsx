import React, { useState } from 'react';
import { CartItem, OrderType, PaymentMethod } from '../types';
import { formatRupiah } from '../utils/format';
import { X, Trash2, Plus, Minus, QrCode, Banknote, ArrowRight, Loader2, MapPin, User, ShoppingBag } from 'lucide-react';

interface CartDrawerProps {
  isOpen: boolean;
  onClose: () => void;
  items: CartItem[];
  orderType: OrderType;
  tableNumber: string | null;
  onUpdateQuantity: (productId: string, quantity: number) => void;
  onRemoveItem: (productId: string) => void;
  onClearCart: () => void;
  onCheckout: (customerName: string, paymentMethod: PaymentMethod, manualTableNumber?: string) => Promise<void>;
}

export const CartDrawer: React.FC<CartDrawerProps> = ({
  isOpen,
  onClose,
  items,
  orderType,
  tableNumber,
  onUpdateQuantity,
  onRemoveItem,
  onCheckout,
}) => {
  if (!isOpen) return null;

  const [customerName, setCustomerName] = useState('');
  const [paymentMethod, setPaymentMethod] = useState<PaymentMethod>('QRIS');
  const [manualTable, setManualTable] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  const total = items.reduce((acc, i) => acc + i.product.price * i.quantity, 0);

  const handleProcessCheckout = async () => {
    setErrorMsg(null);
    if (items.length === 0) return;

    if (orderType === 'DINE_IN' && !tableNumber && !manualTable.trim()) {
      setErrorMsg('Harap isi nomor/nama meja Anda untuk pesanan Makan di Tempat.');
      return;
    }

    try {
      setIsSubmitting(true);
      await onCheckout(
        customerName.trim() || 'Pelanggan AROMA',
        paymentMethod,
        tableNumber || manualTable.trim()
      );
      onClose();
    } catch (err: any) {
      setErrorMsg(err.message || 'Gagal mengirimkan pesanan. Silakan coba lagi.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 bg-black/65 backdrop-blur-sm flex justify-end">
      <div className="bg-white w-full max-w-md h-full flex flex-col shadow-2xl animate-in slide-in-from-right duration-200">
        {/* Header */}
        <div className="p-5 border-b border-cream-200 flex items-center justify-between bg-cream-50 shrink-0">
          <div className="flex items-center gap-2.5">
            <div className="w-9 h-9 rounded-xl bg-brand-500 text-white flex items-center justify-center shadow-sm">
              <ShoppingBag className="w-4 h-4" />
            </div>
            <div>
              <h2 className="font-extrabold text-base text-charcoal">Keranjang Pesanan</h2>
              <p className="text-xs text-charcoal-muted font-medium">
                {orderType === 'DINE_IN' ? `Dine In (${tableNumber || 'Meja Dipilih'})` : 'Take Away'}
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="w-9 h-9 rounded-full bg-cream-200 text-charcoal flex items-center justify-center hover:bg-cream-300 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content */}
        <div className="flex-1 overflow-y-auto p-5 space-y-4">
          {errorMsg && (
            <div className="p-3.5 bg-red-50 border border-red-200 rounded-2xl text-red-700 text-xs font-bold leading-relaxed">
              {errorMsg}
            </div>
          )}

          {items.length === 0 ? (
            <div className="text-center py-16 space-y-2 text-charcoal-muted">
              <p className="font-bold text-sm text-charcoal">Keranjang Anda masih kosong</p>
              <p className="text-xs">Pilih menu lezat AROMA terlebih dahulu.</p>
            </div>
          ) : (
            <>
              {/* Item List */}
              <div className="space-y-3">
                {items.map((item) => (
                  <div
                    key={item.product.id}
                    className="p-3.5 rounded-2xl border border-cream-200/80 bg-cream-50/50 flex gap-3.5 items-center"
                  >
                    <div className="flex-1 min-w-0">
                      <h4 className="font-extrabold text-xs text-charcoal truncate">{item.product.name}</h4>
                      <p className="text-xs font-bold text-brand-600 mt-0.5">
                        {formatRupiah(item.product.price)}
                      </p>
                      {item.notes && (
                        <p className="text-[11px] text-charcoal-muted italic mt-1 line-clamp-1">
                          "{item.notes}"
                        </p>
                      )}
                    </div>

                    <div className="flex items-center gap-2">
                      <div className="flex items-center gap-1.5 bg-white border border-cream-200 rounded-xl p-1 shadow-xs">
                        <button
                          onClick={() => onUpdateQuantity(item.product.id, item.quantity - 1)}
                          className="w-6 h-6 flex items-center justify-center text-charcoal hover:bg-cream-100 rounded-lg font-bold"
                        >
                          <Minus className="w-3 h-3" />
                        </button>
                        <span className="w-5 text-center text-xs font-extrabold">{item.quantity}</span>
                        <button
                          onClick={() => onUpdateQuantity(item.product.id, item.quantity + 1)}
                          className="w-6 h-6 flex items-center justify-center text-charcoal hover:bg-cream-100 rounded-lg font-bold"
                        >
                          <Plus className="w-3 h-3" />
                        </button>
                      </div>

                      <button
                        onClick={() => onRemoveItem(item.product.id)}
                        className="text-red-500 hover:text-red-700 p-1.5 transition-colors"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                ))}
              </div>

              {/* Order Form Settings */}
              <div className="pt-4 border-t border-cream-200 space-y-4">
                {orderType === 'DINE_IN' && !tableNumber && (
                  <div className="space-y-1.5">
                    <label className="block text-xs font-bold text-charcoal flex items-center gap-1.5">
                      <MapPin className="w-3.5 h-3.5 text-brand-500" />
                      <span>Nomor / Nama Meja *</span>
                    </label>
                    <input
                      type="text"
                      placeholder="Contoh: Meja 01"
                      value={manualTable}
                      onChange={(e) => setManualTable(e.target.value)}
                      className="w-full px-4 py-3 rounded-2xl border border-cream-200 focus:outline-none focus:border-brand-500 text-xs font-medium bg-cream-50/50"
                    />
                  </div>
                )}

                <div className="space-y-1.5">
                  <label className="block text-xs font-bold text-charcoal flex items-center gap-1.5">
                    <User className="w-3.5 h-3.5 text-brand-500" />
                    <span>Nama Pemesan (opsional)</span>
                  </label>
                  <input
                    type="text"
                    placeholder="Masukkan nama Anda"
                    value={customerName}
                    onChange={(e) => setCustomerName(e.target.value)}
                    className="w-full px-4 py-3 rounded-2xl border border-cream-200 focus:outline-none focus:border-brand-500 text-xs font-medium bg-cream-50/50"
                  />
                </div>

                {/* Payment Method Selector */}
                <div className="space-y-2">
                  <label className="block text-xs font-bold text-charcoal">
                    Metode Pembayaran
                  </label>
                  <div className="grid grid-cols-2 gap-2.5">
                    <button
                      type="button"
                      onClick={() => setPaymentMethod('QRIS')}
                      className={`p-3 rounded-2xl border text-xs font-bold flex items-center justify-center gap-2 transition-all ${
                        paymentMethod === 'QRIS'
                          ? 'border-brand-500 bg-brand-50 text-brand-700 shadow-sm'
                          : 'border-cream-200 bg-white text-charcoal-muted hover:bg-cream-50'
                      }`}
                    >
                      <QrCode className="w-4 h-4" />
                      <span>QRIS Standar</span>
                    </button>

                    <button
                      type="button"
                      onClick={() => setPaymentMethod('CASH')}
                      className={`p-3 rounded-2xl border text-xs font-bold flex items-center justify-center gap-2 transition-all ${
                        paymentMethod === 'CASH'
                          ? 'border-brand-500 bg-brand-50 text-brand-700 shadow-sm'
                          : 'border-cream-200 bg-white text-charcoal-muted hover:bg-cream-50'
                      }`}
                    >
                      <Banknote className="w-4 h-4" />
                      <span>Bayar di Kasir</span>
                    </button>
                  </div>
                </div>
              </div>
            </>
          )}
        </div>

        {/* Footer */}
        {items.length > 0 && (
          <div className="p-5 border-t border-cream-200 bg-cream-50 space-y-4 shrink-0">
            <div className="flex items-center justify-between text-sm">
              <span className="font-semibold text-charcoal-muted">Total Pembayaran</span>
              <span className="font-extrabold text-xl text-brand-600">{formatRupiah(total)}</span>
            </div>

            <button
              onClick={handleProcessCheckout}
              disabled={isSubmitting}
              className="w-full py-4 px-5 rounded-2xl bg-brand-500 hover:bg-brand-600 text-white font-extrabold text-sm shadow-lg shadow-brand-500/25 flex items-center justify-center gap-2.5 active:scale-[0.99] transition-all disabled:opacity-50"
            >
              {isSubmitting ? (
                <>
                  <Loader2 className="w-5 h-5 animate-spin" />
                  <span>Memproses Pesanan...</span>
                </>
              ) : (
                <>
                  <span>Buat Pesanan Sekarang</span>
                  <ArrowRight className="w-4 h-4" />
                </>
              )}
            </button>
          </div>
        )}
      </div>
    </div>
  );
};
