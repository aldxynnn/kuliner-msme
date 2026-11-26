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
  Receipt,
} from 'lucide-react';

export interface OrderTicketProps {
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
  onCloseAfterSubmit?: () => void;
}

interface CartDrawerProps extends OrderTicketProps {
  isOpen: boolean;
  onClose: () => void;
}

const OrderTicketBody: React.FC<
  OrderTicketProps & { compactPadding?: boolean; resetSignal?: boolean }
> = ({
  items,
  orderType,
  tableNumber,
  onUpdateQuantity,
  onRemoveItem,
  onCheckout,
  onCloseAfterSubmit,
  compactPadding = false,
  resetSignal = true,
}) => {
  const [customerName, setCustomerName] = useState('');
  const [paymentMethod, setPaymentMethod] = useState<PaymentMethod>('QRIS');
  const [manualTable, setManualTable] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  useEffect(() => {
    if (resetSignal) {
      setErrorMsg(null);
    }
  }, [resetSignal]);

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
      onCloseAfterSubmit?.();
    } catch (err: any) {
      setErrorMsg(err.message || 'Gagal mengirimkan pesanan. Silakan coba lagi.');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (items.length === 0) {
    return (
      <div className="p-8 text-center space-y-3">
        <div className="w-12 h-12 mx-auto rounded-2xl bg-paper-100 border border-paper-200 flex items-center justify-center text-ink-muted">
          <Receipt className="w-5 h-5" />
        </div>
        <div className="space-y-1">
          <p className="font-bold text-sm text-ink">Tiket Pesanan Kosong</p>
          <p className="text-xs text-ink-muted leading-relaxed">
            Pilih menu AROMA di samping untuk mencatat pesanan dan langsung checkout.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="flex flex-col h-full">
      <div className={`${compactPadding ? 'p-4' : 'p-5'} space-y-4 flex-1 overflow-y-auto`}>
        {errorMsg && (
          <div
            role="alert"
            className="p-3 bg-semantic-error-bg border border-semantic-error-border rounded-xl text-semantic-error text-xs font-semibold flex items-start gap-2 leading-relaxed"
          >
            <AlertCircle className="w-4 h-4 shrink-0 mt-0.5" />
            <span>{errorMsg}</span>
          </div>
        )}

        {/* Ticket Items */}
        <div className="space-y-2.5">
          {items.map((item) => {
            const thumbUrl = getImageUrl(item.product.image);
            return (
              <div
                key={item.product.id}
                className="p-3 rounded-xl border border-paper-200 bg-paper-50/60 space-y-2.5"
              >
                <div className="flex gap-2.5 items-start">
                  <div className="w-12 h-12 rounded-lg bg-white border border-paper-200 overflow-hidden shrink-0 flex items-center justify-center">
                    {thumbUrl ? (
                      <img
                        src={thumbUrl}
                        alt={item.product.name}
                        className="w-full h-full object-cover"
                      />
                    ) : (
                      <UtensilsCrossed className="w-4 h-4 text-ink-light" />
                    )}
                  </div>

                  <div className="flex-1 min-w-0">
                    <h4 className="font-bold text-[13px] text-ink leading-snug line-clamp-2">
                      {item.product.name}
                    </h4>
                    <p className="font-mono text-[11px] text-ink-muted mt-0.5 tabular-nums">
                      {formatRupiah(item.product.price)} / porsi
                    </p>
                    {item.notes && (
                      <p className="text-[11px] text-ink-secondary bg-white border border-paper-200 rounded-md px-2 py-0.5 mt-1 line-clamp-2">
                        {item.notes}
                      </p>
                    )}
                  </div>

                  <button
                    type="button"
                    onClick={() => onRemoveItem(item.product.id)}
                    aria-label={`Hapus ${item.product.name}`}
                    className="w-8 h-8 rounded-lg text-ink-muted hover:text-semantic-error hover:bg-semantic-error-bg flex items-center justify-center transition-colors shrink-0"
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                  </button>
                </div>

                <div className="flex items-center justify-between pt-2 receipt-edge">
                  <span className="font-mono font-bold text-xs text-ink tabular-nums">
                    {formatRupiah(item.product.price * item.quantity)}
                  </span>

                  <div className="flex items-center gap-1 bg-white border border-paper-200 rounded-full p-0.5">
                    <button
                      type="button"
                      onClick={() => onUpdateQuantity(item.product.id, item.quantity - 1)}
                      aria-label="Kurangi jumlah"
                      className="w-7 h-7 flex items-center justify-center text-ink hover:bg-paper-100 rounded-full transition-colors"
                    >
                      <Minus className="w-3 h-3" />
                    </button>
                    <span className="w-6 text-center font-mono text-xs font-bold text-ink tabular-nums">
                      {item.quantity}
                    </span>
                    <button
                      type="button"
                      onClick={() => onUpdateQuantity(item.product.id, item.quantity + 1)}
                      aria-label="Tambah jumlah"
                      className="w-7 h-7 flex items-center justify-center bg-ink text-white hover:bg-spice-500 rounded-full transition-colors"
                    >
                      <Plus className="w-3 h-3" />
                    </button>
                  </div>
                </div>
              </div>
            );
          })}
        </div>

        {/* Order Context & Payment Fields */}
        <div className="pt-3.5 receipt-edge space-y-3.5">
          <p className="font-mono text-[10px] font-bold text-ink-muted uppercase tracking-[0.14em]">
            Detail Pemesanan &amp; Bayar
          </p>

          {orderType === 'DINE_IN' && tableNumber && (
            <div className="px-3 py-2.5 rounded-xl bg-pandan-50 border border-pandan-200 flex items-center gap-2 text-xs font-semibold text-pandan-700">
              <CheckCircle2 className="w-4 h-4 shrink-0" />
              <span>
                Meja aktif: <strong className="font-mono font-bold">{tableNumber}</strong>
              </span>
            </div>
          )}

          {orderType === 'DINE_IN' && !tableNumber && (
            <div className="space-y-1.5">
              <label
                htmlFor={compactPadding ? 'desktop-manual-table' : 'drawer-manual-table'}
                className="text-xs font-bold text-ink flex items-center gap-1.5"
              >
                <MapPin className="w-3.5 h-3.5 text-spice-500" />
                <span>Nomor / Nama Meja</span>
                <span className="text-semantic-error">*</span>
              </label>
              <input
                id={compactPadding ? 'desktop-manual-table' : 'drawer-manual-table'}
                type="text"
                placeholder="Contoh: Meja 01"
                value={manualTable}
                onChange={(e) => {
                  setManualTable(e.target.value);
                  if (errorMsg) setErrorMsg(null);
                }}
                className="w-full px-3.5 py-2.5 rounded-xl border border-paper-200 bg-paper-50 text-sm text-ink placeholder:text-ink-light focus:outline-none focus:bg-white focus:border-ink transition-colors"
              />
            </div>
          )}

          <div className="space-y-1.5">
            <label
              htmlFor={compactPadding ? 'desktop-customer-name' : 'drawer-customer-name'}
              className="text-xs font-bold text-ink flex items-center gap-1.5"
            >
              <User className="w-3.5 h-3.5 text-spice-500" />
              <span>Nama Pemesan</span>
              <span className="font-normal text-ink-muted">(opsional)</span>
            </label>
            <input
              id={compactPadding ? 'desktop-customer-name' : 'drawer-customer-name'}
              type="text"
              placeholder="Masukkan nama Anda"
              value={customerName}
              onChange={(e) => setCustomerName(e.target.value)}
              className="w-full px-3.5 py-2.5 rounded-xl border border-paper-200 bg-paper-50 text-sm text-ink placeholder:text-ink-light focus:outline-none focus:bg-white focus:border-ink transition-colors"
            />
          </div>

          <div className="space-y-1.5">
            <span className="block text-xs font-bold text-ink">Metode Pembayaran</span>
            <div className="grid grid-cols-2 gap-2">
              <button
                type="button"
                onClick={() => setPaymentMethod('QRIS')}
                aria-pressed={paymentMethod === 'QRIS'}
                className={`p-3 rounded-xl border text-left transition-all ${
                  paymentMethod === 'QRIS'
                    ? 'border-ink bg-ink text-white'
                    : 'border-paper-200 bg-paper-50 text-ink hover:border-ink/40'
                }`}
              >
                <div className="flex items-center gap-1.5">
                  <QrCode className="w-4 h-4 shrink-0" />
                  <span className="text-xs font-bold">QRIS</span>
                </div>
                <p
                  className={`text-[10px] mt-1 leading-snug ${
                    paymentMethod === 'QRIS' ? 'text-paper-200' : 'text-ink-muted'
                  }`}
                >
                  Scan QR &amp; verifikasi
                </p>
              </button>

              <button
                type="button"
                onClick={() => setPaymentMethod('CASH')}
                aria-pressed={paymentMethod === 'CASH'}
                className={`p-3 rounded-xl border text-left transition-all ${
                  paymentMethod === 'CASH'
                    ? 'border-ink bg-ink text-white'
                    : 'border-paper-200 bg-paper-50 text-ink hover:border-ink/40'
                }`}
              >
                <div className="flex items-center gap-1.5">
                  <Banknote className="w-4 h-4 shrink-0" />
                  <span className="text-xs font-bold">Tunai</span>
                </div>
                <p
                  className={`text-[10px] mt-1 leading-snug ${
                    paymentMethod === 'CASH' ? 'text-paper-200' : 'text-ink-muted'
                  }`}
                >
                  Bayar di kasir
                </p>
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Footer Total & Submit CTA */}
      <div
        className={`${
          compactPadding ? 'p-4' : 'p-5'
        } border-t border-paper-200 bg-paper-50 space-y-3 shrink-0`}
      >
        <div className="flex items-baseline justify-between gap-2">
          <div>
            <p className="text-xs font-bold text-ink">Total Tagihan</p>
            <p className="font-mono text-[11px] text-ink-muted tabular-nums">{totalItems} item</p>
          </div>
          <span className="font-mono font-bold text-lg text-ink tabular-nums">
            {formatRupiah(total)}
          </span>
        </div>

        <button
          type="button"
          onClick={handleProcessCheckout}
          disabled={isSubmitting}
          className="w-full min-h-[48px] py-3 px-4 rounded-xl bg-spice-500 hover:bg-spice-600 text-white font-bold text-sm flex items-center justify-center gap-2 active:scale-[0.99] transition-colors disabled:opacity-55"
        >
          {isSubmitting ? (
            <>
              <Loader2 className="w-4 h-4 animate-spin" />
              <span>Memproses...</span>
            </>
          ) : (
            <>
              <span>Buat Pesanan Sekarang</span>
              <ArrowRight className="w-4 h-4" />
            </>
          )}
        </button>
      </div>
    </div>
  );
};

export const CartOrderTicketPanel: React.FC<OrderTicketProps> = (props) => {
  const totalItems = props.items.reduce((acc, i) => acc + i.quantity, 0);

  return (
    <aside
      aria-label="Tiket Pesanan"
      className="bg-white rounded-2xl border border-paper-200 shadow-ticket overflow-hidden flex flex-col max-h-[calc(100vh-148px)]"
    >
      {/* Ticket Slip Header */}
      <div className="px-4 py-3.5 bg-ink text-white flex items-center justify-between gap-2 shrink-0">
        <div className="min-w-0">
          <div className="flex items-center gap-2">
            <span className="font-mono text-[10px] uppercase tracking-[0.16em] text-spice-200 font-semibold">
              Tiket Pesanan
            </span>
          </div>
          <p className="text-xs text-paper-200 truncate mt-0.5">
            {props.orderType === 'DINE_IN'
              ? `Dine In${props.tableNumber ? ` · ${props.tableNumber}` : ''}`
              : 'Take Away · Bawa Pulang'}
          </p>
        </div>

        <span className="font-mono text-[11px] font-bold px-2.5 py-1 rounded-md bg-white/10 text-white tabular-nums whitespace-nowrap shrink-0">
          {totalItems} item
        </span>
      </div>

      <div className="flex-1 min-h-0 flex flex-col">
        <OrderTicketBody {...props} compactPadding />
      </div>
    </aside>
  );
};

export const CartDrawer: React.FC<CartDrawerProps> = ({
  isOpen,
  onClose,
  ...ticketProps
}) => {
  useEffect(() => {
    if (!isOpen) return;
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  const totalItems = ticketProps.items.reduce((acc, i) => acc + i.quantity, 0);

  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-labelledby="cart-drawer-title"
      onClick={onClose}
      className="fixed inset-0 z-50 bg-ink/65 backdrop-blur-sm flex justify-end animate-aroma-fade"
    >
      <div
        onClick={(e) => e.stopPropagation()}
        className="bg-white w-full sm:max-w-md h-full flex flex-col shadow-2xl animate-aroma-drawer"
      >
        {/* Drawer Header */}
        <div className="px-5 py-4 border-b border-paper-200 bg-white flex items-center justify-between gap-3 shrink-0">
          <div className="flex items-center gap-3 min-w-0">
            <div className="w-10 h-10 rounded-xl bg-ink text-white flex items-center justify-center shrink-0">
              <ShoppingBag className="w-5 h-5" />
            </div>
            <div className="min-w-0">
              <div className="flex items-center gap-2 min-w-0">
                <h2
                  id="cart-drawer-title"
                  className="font-display font-bold text-base sm:text-lg text-ink truncate"
                >
                  Tiket Pesanan
                </h2>
                {totalItems > 0 && (
                  <span className="font-mono text-[11px] font-bold px-2 py-0.5 rounded-md bg-spice-50 text-spice-600 border border-spice-200 tabular-nums whitespace-nowrap shrink-0">
                    {totalItems} item
                  </span>
                )}
              </div>
              <p className="text-xs text-ink-muted font-medium truncate mt-0.5">
                {ticketProps.orderType === 'DINE_IN'
                  ? `Dine In${ticketProps.tableNumber ? ` · ${ticketProps.tableNumber}` : ' · Makan di Tempat'}`
                  : 'Take Away · Bawa Pulang'}
              </p>
            </div>
          </div>

          <button
            type="button"
            onClick={onClose}
            aria-label="Tutup keranjang"
            className="w-10 h-10 rounded-xl bg-paper-100 text-ink flex items-center justify-center hover:bg-paper-200 transition-colors shrink-0"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="flex-1 min-h-0 flex flex-col">
          <OrderTicketBody
            {...ticketProps}
            onCloseAfterSubmit={onClose}
            resetSignal={isOpen}
          />
        </div>
      </div>
    </div>
  );
};
