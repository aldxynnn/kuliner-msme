import React, { useEffect, useState } from 'react';
import { CartItem, OrderType, PaymentMethod } from '../types';
import { formatRupiah } from '../utils/format';
import { getImageUrl } from '../api';
import {
  X,
  Trash2,
  Plus,
  Minus,
  QrCode,
  Banknote,
  ArrowRight,
  Loader2,
  MapPin,
  User,
  ShoppingBag,
  AlertCircle,
  UtensilsCrossed,
  CheckCircle2,
} from 'lucide-react';

interface CartDrawerProps {
  isOpen: boolean;
  onClose: () => void;
  items: CartItem[];
  orderType: OrderType;
  tableNumber: string | null;
  onUpdateQuantity: (productId: string, quantity: number) => void;
  onRemoveItem: (productId: string) => void;
  onClearCart: () => void;
  onCheckout: (
    customerName: string,
    paymentMethod: PaymentMethod,
    manualTableNumber?: string
  ) => Promise<void>;
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
  const [customerName, setCustomerName] = useState('');
  const [paymentMethod, setPaymentMethod] = useState<PaymentMethod>('QRIS');
  const [manualTable, setManualTable] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  useEffect(() => {
    if (isOpen) {
      setErrorMsg(null);
    }
  }, [isOpen]);

  useEffect(() => {
    if (!isOpen) return;
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && !isSubmitting) onClose();
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, isSubmitting, onClose]);

  if (!isOpen) return null;

  const totalItems = items.reduce((acc, i) => acc + i.quantity, 0);
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
    <div
      role="dialog"
      aria-modal="true"
      aria-labelledby="cart-drawer-title"
      onClick={() => !isSubmitting && onClose()}
      className="fixed inset-0 z-50 bg-charcoal/60 backdrop-blur-sm flex justify-end animate-aroma-fade"
    >
      <div
        onClick={(e) => e.stopPropagation()}
        className="bg-white w-full sm:max-w-md h-full flex flex-col shadow-2xl animate-aroma-drawer"
      >
        {/* Drawer Header */}
        <div className="px-5 py-4 border-b border-cream-200 bg-white flex items-center justify-between gap-3 shrink-0">
          <div className="flex items-center gap-3 min-w-0">
            <div className="w-10 h-10 rounded-xl bg-brand-50 text-brand-500 border border-brand-200 flex items-center justify-center shrink-0">
              <ShoppingBag className="w-5 h-5" />
            </div>
            <div className="min-w-0">
              <div className="flex items-center gap-2">
                <h2 id="cart-drawer-title" className="font-extrabold text-base text-charcoal truncate">
                  Keranjang Pesanan
                </h2>
                {totalItems > 0 && (
                  <span className="text-[11px] font-bold px-2 py-0.5 rounded-md bg-brand-50 text-brand-600 tabular-nums">
                    {totalItems} item
                  </span>
                )}
              </div>
              <p className="text-xs text-charcoal-muted font-medium truncate mt-0.5">
                {orderType === 'DINE_IN'
                  ? `Dine In${tableNumber ? ` • ${tableNumber}` : ' • Makan di Tempat'}`
                  : 'Take Away • Bawa Pulang'}
              </p>
            </div>
          </div>

          <button
            type="button"
            onClick={onClose}
            disabled={isSubmitting}
            aria-label="Tutup keranjang"
            className="w-10 h-10 rounded-xl bg-cream-100 text-charcoal flex items-center justify-center hover:bg-cream-200 transition-colors shrink-0 disabled:opacity-50"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Scrollable Content */}
        <div className="flex-1 overflow-y-auto p-5 space-y-5 bg-cream-50/40">
          {errorMsg && (
            <div
              role="alert"
              className="p-3.5 bg-semantic-error-bg border border-semantic-error-border rounded-xl text-semantic-error text-xs font-semibold flex items-start gap-2.5 leading-relaxed"
            >
              <AlertCircle className="w-4 h-4 shrink-0 mt-0.5" />
              <span>{errorMsg}</span>
            </div>
          )}

          {items.length === 0 ? (
            <div className="text-center py-16 px-4 space-y-3">
              <div className="w-14 h-14 mx-auto rounded-2xl bg-cream-100 border border-cream-200 flex items-center justify-center text-charcoal-muted">
                <ShoppingBag className="w-6 h-6" />
              </div>
              <div className="space-y-1">
                <p className="font-bold text-sm text-charcoal">Keranjang masih kosong</p>
                <p className="text-xs text-charcoal-muted">
                  Pilih menu AROMA favorit Anda untuk memulai pesanan.
                </p>
              </div>
            </div>
          ) : (
            <>
              {/* Cart Items List */}
              <div className="space-y-3">
                <p className="text-[11px] font-bold text-charcoal-muted uppercase tracking-wider">
                  Daftar Menu Dipilih
                </p>

                {items.map((item) => {
                  const thumbUrl = getImageUrl(item.product.image);
                  return (
                    <div
                      key={item.product.id}
                      className="p-3.5 rounded-2xl border border-cream-200 bg-white space-y-3"
                    >
                      <div className="flex gap-3 items-start">
                        <div className="w-14 h-14 rounded-xl bg-cream-50 border border-cream-200/70 overflow-hidden shrink-0 flex items-center justify-center">
                          {thumbUrl ? (
                            <img
                              src={thumbUrl}
                              alt={item.product.name}
                              className="w-full h-full object-cover"
                            />
                          ) : (
                            <UtensilsCrossed className="w-5 h-5 text-charcoal-light" />
                          )}
                        </div>

                        <div className="flex-1 min-w-0">
                          <h4 className="font-bold text-sm text-charcoal leading-snug line-clamp-2">
                            {item.product.name}
                          </h4>
                          <p className="text-xs text-charcoal-muted mt-0.5 tabular-nums">
                            {formatRupiah(item.product.price)} / porsi
                          </p>
                          {item.notes && (
                            <p className="text-[11px] text-charcoal-secondary bg-cream-50 border border-cream-200/70 rounded-lg px-2 py-1 mt-1.5 line-clamp-2">
                              Catatan: {item.notes}
                            </p>
                          )}
                        </div>

                        <button
                          type="button"
                          onClick={() => onRemoveItem(item.product.id)}
                          aria-label={`Hapus ${item.product.name}`}
                          className="w-8 h-8 rounded-lg text-charcoal-muted hover:text-semantic-error hover:bg-semantic-error-bg flex items-center justify-center transition-colors shrink-0"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>

                      <div className="flex items-center justify-between pt-2.5 border-t border-cream-100">
                        <span className="font-extrabold text-sm text-brand-500 tabular-nums">
                          {formatRupiah(item.product.price * item.quantity)}
                        </span>

                        <div className="flex items-center gap-1.5 bg-cream-100 border border-cream-200 rounded-xl p-1">
                          <button
                            type="button"
                            onClick={() => onUpdateQuantity(item.product.id, item.quantity - 1)}
                            aria-label="Kurangi jumlah"
                            className="w-8 h-8 flex items-center justify-center bg-white text-charcoal hover:bg-cream-50 rounded-lg transition-colors"
                          >
                            <Minus className="w-3.5 h-3.5" />
                          </button>
                          <span className="w-7 text-center text-xs font-extrabold text-charcoal tabular-nums">
                            {item.quantity}
                          </span>
                          <button
                            type="button"
                            onClick={() => onUpdateQuantity(item.product.id, item.quantity + 1)}
                            aria-label="Tambah jumlah"
                            className="w-8 h-8 flex items-center justify-center bg-brand-500 text-white hover:bg-brand-600 rounded-lg transition-colors"
                          >
                            <Plus className="w-3.5 h-3.5" />
                          </button>
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>

              {/* Checkout Details Section */}
              <div className="pt-4 border-t border-cream-200 space-y-4">
                <p className="text-[11px] font-bold text-charcoal-muted uppercase tracking-wider">
                  Informasi Pesanan &amp; Pembayaran
                </p>

                {/* Detected Table Confirmation */}
                {orderType === 'DINE_IN' && tableNumber && (
                  <div className="p-3 rounded-xl bg-semantic-success-bg border border-semantic-success-border flex items-center gap-2.5 text-xs font-semibold text-semantic-success">
                    <CheckCircle2 className="w-4 h-4 shrink-0" />
                    <span>
                      Meja terdeteksi otomatis: <strong className="font-extrabold">{tableNumber}</strong>
                    </span>
                  </div>
                )}

                {/* Manual Table Input if Dine In without QR param */}
                {orderType === 'DINE_IN' && !tableNumber && (
                  <div className="space-y-1.5">
                    <label
                      htmlFor="manual-table-input"
                      className="text-xs font-bold text-charcoal flex items-center gap-1.5"
                    >
                      <MapPin className="w-3.5 h-3.5 text-brand-500" />
                      <span>Nomor / Nama Meja</span>
                      <span className="text-semantic-error">*</span>
                    </label>
                    <input
                      id="manual-table-input"
                      type="text"
                      placeholder="Contoh: Meja 01"
                      value={manualTable}
                      onChange={(e) => {
                        setManualTable(e.target.value);
                        if (errorMsg) setErrorMsg(null);
                      }}
                      className="w-full px-4 py-3 rounded-xl border border-cream-200 bg-white text-sm text-charcoal placeholder:text-charcoal-light focus:outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-500/15 transition-all"
                    />
                  </div>
                )}

                {/* Customer Name Input */}
                <div className="space-y-1.5">
                  <label
                    htmlFor="customer-name-input"
                    className="text-xs font-bold text-charcoal flex items-center gap-1.5"
                  >
                    <User className="w-3.5 h-3.5 text-brand-500" />
                    <span>Nama Pemesan</span>
                    <span className="font-medium text-charcoal-muted">(opsional)</span>
                  </label>
                  <input
                    id="customer-name-input"
                    type="text"
                    placeholder="Masukkan nama Anda"
                    value={customerName}
                    onChange={(e) => setCustomerName(e.target.value)}
                    className="w-full px-4 py-3 rounded-xl border border-cream-200 bg-white text-sm text-charcoal placeholder:text-charcoal-light focus:outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-500/15 transition-all"
                  />
                </div>

                {/* Payment Method Selector */}
                <div className="space-y-2">
                  <span className="block text-xs font-bold text-charcoal">
                    Metode Pembayaran
                  </span>
                  <div className="grid grid-cols-2 gap-2.5">
                    <button
                      type="button"
                      onClick={() => setPaymentMethod('QRIS')}
                      aria-pressed={paymentMethod === 'QRIS'}
                      className={`p-3.5 rounded-xl border text-left transition-all ${
                        paymentMethod === 'QRIS'
                          ? 'border-brand-500 bg-brand-50 ring-1 ring-brand-500'
                          : 'border-cream-200 bg-white hover:border-cream-300'
                      }`}
                    >
                      <div className="flex items-center gap-2">
                        <QrCode
                          className={`w-4 h-4 shrink-0 ${
                            paymentMethod === 'QRIS' ? 'text-brand-500' : 'text-charcoal-muted'
                          }`}
                        />
                        <span
                          className={`text-xs font-extrabold ${
                            paymentMethod === 'QRIS' ? 'text-brand-700' : 'text-charcoal'
                          }`}
                        >
                          QRIS
                        </span>
                      </div>
                      <p className="text-[11px] text-charcoal-muted mt-1 leading-snug">
                        Pindai QR &amp; verifikasi kasir
                      </p>
                    </button>

                    <button
                      type="button"
                      onClick={() => setPaymentMethod('CASH')}
                      aria-pressed={paymentMethod === 'CASH'}
                      className={`p-3.5 rounded-xl border text-left transition-all ${
                        paymentMethod === 'CASH'
                          ? 'border-brand-500 bg-brand-50 ring-1 ring-brand-500'
                          : 'border-cream-200 bg-white hover:border-cream-300'
                      }`}
                    >
                      <div className="flex items-center gap-2">
                        <Banknote
                          className={`w-4 h-4 shrink-0 ${
                            paymentMethod === 'CASH' ? 'text-brand-500' : 'text-charcoal-muted'
                          }`}
                        />
                        <span
                          className={`text-xs font-extrabold ${
                            paymentMethod === 'CASH' ? 'text-brand-700' : 'text-charcoal'
                          }`}
                        >
                          Bayar di Kasir
                        </span>
                      </div>
                      <p className="text-[11px] text-charcoal-muted mt-1 leading-snug">
                        Tunai langsung di kasir
                      </p>
                    </button>
                  </div>
                </div>
              </div>
            </>
          )}
        </div>

        {/* Sticky Checkout Footer */}
        {items.length > 0 && (
          <div className="p-5 border-t border-cream-200 bg-white space-y-3.5 shrink-0">
            <div className="flex items-baseline justify-between">
              <div>
                <p className="text-xs font-semibold text-charcoal-muted">Total Pembayaran</p>
                <p className="text-[11px] text-charcoal-light tabular-nums">{totalItems} item dipilih</p>
              </div>
              <span className="font-extrabold text-xl text-charcoal tabular-nums">
                {formatRupiah(total)}
              </span>
            </div>

            <button
              type="button"
              onClick={handleProcessCheckout}
              disabled={isSubmitting}
              className="w-full min-h-[52px] py-3.5 px-5 rounded-xl bg-brand-500 hover:bg-brand-600 text-white font-bold text-sm flex items-center justify-center gap-2.5 active:scale-[0.99] transition-all disabled:opacity-55 shadow-sm"
            >
              {isSubmitting ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin" />
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
