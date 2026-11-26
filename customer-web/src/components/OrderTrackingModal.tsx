import React, { useEffect } from 'react';
import { Order, OrderStatus } from '../types';
import { formatRupiah, formatDate } from '../utils/format';
import {
  X,
  QrCode,
  Receipt,
  Check,
  Clock,
  Banknote,
  ChefHat,
  CheckCircle2,
  XCircle,
} from 'lucide-react';

interface OrderTrackingModalProps {
  order: Order | null;
  onClose: () => void;
}

const PROGRESS_STEPS: { key: OrderStatus; label: string }[] = [
  { key: 'MENUNGGU_PEMBAYARAN', label: 'Bayar' },
  { key: 'MENUNGGU_DIPROSES', label: 'Antrean' },
  { key: 'DIPROSES', label: 'Dimasak' },
  { key: 'SIAP_DISAJIKAN', label: 'Siap' },
  { key: 'SELESAI', label: 'Selesai' },
];

export const OrderTrackingModal: React.FC<OrderTrackingModalProps> = ({ order, onClose }) => {
  useEffect(() => {
    if (!order) return;
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [order, onClose]);

  if (!order) return null;

  const getStatusMeta = () => {
    switch (order.orderStatus) {
      case 'MENUNGGU_PEMBAYARAN':
        return {
          label: 'Menunggu Pembayaran',
          badgeClass: 'bg-semantic-warning-bg text-semantic-warning border-semantic-warning-border',
          icon: <Clock className="w-4 h-4 shrink-0" />,
          desc:
            order.paymentMethod === 'QRIS'
              ? 'Silakan pindai QRIS di bawah ini, lalu tunjukkan bukti transfer ke kasir untuk verifikasi.'
              : 'Silakan selesaikan pembayaran tunai di meja kasir dengan menyebutkan nomor pesanan Anda.',
        };
      case 'MENUNGGU_DIPROSES':
        return {
          label: 'Pesanan Diterima Kasir',
          badgeClass: 'bg-semantic-info-bg text-semantic-info border-semantic-info-border',
          icon: <CheckCircle2 className="w-4 h-4 shrink-0" />,
          desc: 'Pembayaran terkonfirmasi. Pesanan Anda telah masuk antrean dapur AROMA.',
        };
      case 'DIPROSES':
        return {
          label: 'Sedang Dimasak Dapur',
          badgeClass: 'bg-brand-50 text-brand-600 border-brand-200',
          icon: <ChefHat className="w-4 h-4 shrink-0" />,
          desc: 'Koki sedang menyiapkan hidangan hangat Anda. Mohon tunggu sebentar.',
        };
      case 'SIAP_DISAJIKAN':
        return {
          label: 'Siap Disajikan / Diambil',
          badgeClass: 'bg-semantic-success-bg text-semantic-success border-semantic-success-border',
          icon: <CheckCircle2 className="w-4 h-4 shrink-0" />,
          desc: 'Pesanan Anda sudah siap! Pelayan akan segera menyajikan atau dapat diambil di kasir.',
        };
      case 'SELESAI':
        return {
          label: 'Pesanan Selesai',
          badgeClass: 'bg-semantic-success-bg text-semantic-success border-semantic-success-border',
          icon: <CheckCircle2 className="w-4 h-4 shrink-0" />,
          desc: 'Terima kasih telah menikmati sajian AROMA – Ayam Goreng Lengkuas.',
        };
      case 'DIBATALKAN':
        return {
          label: 'Pesanan Dibatalkan',
          badgeClass: 'bg-semantic-error-bg text-semantic-error border-semantic-error-border',
          icon: <XCircle className="w-4 h-4 shrink-0" />,
          desc: 'Pesanan ini telah dibatalkan oleh kasir.',
        };
      default:
        return {
          label: order.orderStatus,
          badgeClass: 'bg-cream-100 text-charcoal border-cream-200',
          icon: <Clock className="w-4 h-4 shrink-0" />,
          desc: '',
        };
    }
  };

  const statusInfo = getStatusMeta();
  const currentStepIndex = PROGRESS_STEPS.findIndex((s) => s.key === order.orderStatus);
  const isCancelled = order.orderStatus === 'DIBATALKAN';
  const isPaid = order.paymentStatus === 'PAID';

  const paymentStatusLabel =
    order.paymentStatus === 'PAID'
      ? 'LUNAS'
      : order.paymentStatus === 'CANCELLED'
      ? 'DIBATALKAN'
      : 'MENUNGGU BAYAR';

  const paymentStatusClass =
    order.paymentStatus === 'PAID'
      ? 'bg-semantic-success-bg text-semantic-success border-semantic-success-border'
      : order.paymentStatus === 'CANCELLED'
      ? 'bg-semantic-error-bg text-semantic-error border-semantic-error-border'
      : 'bg-semantic-warning-bg text-semantic-warning border-semantic-warning-border';

  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-labelledby="tracking-modal-title"
      onClick={onClose}
      className="fixed inset-0 z-50 bg-charcoal/60 backdrop-blur-sm flex items-end sm:items-center justify-center p-0 sm:p-4 animate-aroma-fade"
    >
      <div
        onClick={(e) => e.stopPropagation()}
        className="bg-white w-full sm:max-w-lg rounded-t-3xl sm:rounded-3xl overflow-hidden shadow-2xl flex flex-col max-h-[92vh] animate-aroma-sheet sm:animate-aroma-modal"
      >
        {/* Header */}
        <div className="px-5 py-4 border-b border-cream-200 bg-cream-50 flex items-center justify-between gap-3 shrink-0">
          <div className="min-w-0 flex-1">
            <div className="flex items-center gap-2">
              <span className="inline-flex items-center gap-1.5 text-[11px] font-bold text-brand-600 uppercase tracking-wider">
                {!isCancelled && order.orderStatus !== 'SELESAI' && (
                  <span className="w-2 h-2 rounded-full bg-brand-500 animate-pulse" />
                )}
                Status Pesanan
              </span>
              <span className="text-[11px] text-charcoal-muted">• Auto-update</span>
            </div>
            <h3
              id="tracking-modal-title"
              className="font-extrabold text-base sm:text-lg text-charcoal break-all leading-tight mt-0.5 tabular-nums"
            >
              {order.orderNumber}
            </h3>
          </div>
          <button
            type="button"
            onClick={onClose}
            aria-label="Tutup pelacakan pesanan"
            className="w-10 h-10 rounded-xl bg-white border border-cream-200 text-charcoal flex items-center justify-center hover:bg-cream-100 transition-colors shrink-0"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Scrollable Body */}
        <div className="p-5 sm:p-6 overflow-y-auto space-y-5 bg-white">
          {/* Step Progression Indicator */}
          {!isCancelled && currentStepIndex >= 0 && (
            <div className="px-1">
              <div className="grid grid-cols-5 gap-1">
                {PROGRESS_STEPS.map((step, index) => {
                  const isCompleted = index < currentStepIndex || order.orderStatus === 'SELESAI';
                  const isActive = index === currentStepIndex && order.orderStatus !== 'SELESAI';
                  return (
                    <div key={step.key} className="flex flex-col items-center text-center gap-1.5">
                      <div className="w-full flex items-center">
                        <div
                          className={`h-0.5 flex-1 ${
                            index === 0
                              ? 'bg-transparent'
                              : index <= currentStepIndex
                              ? 'bg-brand-500'
                              : 'bg-cream-200'
                          }`}
                        />
                        <div
                          className={`w-6 h-6 rounded-full flex items-center justify-center text-[10px] font-extrabold shrink-0 transition-colors ${
                            isCompleted
                              ? 'bg-semantic-success text-white'
                              : isActive
                              ? 'bg-brand-500 text-white ring-4 ring-brand-100'
                              : 'bg-cream-100 text-charcoal-light border border-cream-200'
                          }`}
                        >
                          {isCompleted ? <Check className="w-3.5 h-3.5" /> : index + 1}
                        </div>
                        <div
                          className={`h-0.5 flex-1 ${
                            index === PROGRESS_STEPS.length - 1
                              ? 'bg-transparent'
                              : index < currentStepIndex
                              ? 'bg-brand-500'
                              : 'bg-cream-200'
                          }`}
                        />
                      </div>
                      <span
                        className={`text-[10px] font-bold leading-tight ${
                          isActive
                            ? 'text-brand-600'
                            : isCompleted
                            ? 'text-charcoal'
                            : 'text-charcoal-light'
                        }`}
                      >
                        {step.label}
                      </span>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {/* Status Banner */}
          <div className={`p-4 rounded-2xl border ${statusInfo.badgeClass} space-y-1.5`}>
            <div className="flex flex-wrap items-center justify-between gap-2">
              <div className="flex items-center gap-2 font-extrabold text-sm">
                {statusInfo.icon}
                <span>{statusInfo.label}</span>
              </div>
              <span className="text-[11px] font-semibold opacity-80 tabular-nums">
                {formatDate(order.createdAt)}
              </span>
            </div>
            <p className="text-xs font-body leading-relaxed opacity-95">{statusInfo.desc}</p>
          </div>

          {/* QRIS Payment Instructions */}
          {order.paymentMethod === 'QRIS' && order.paymentStatus === 'PENDING' && !isCancelled && (
            <div className="p-5 rounded-2xl border border-brand-200 bg-brand-50/70 text-center space-y-3">
              <div className="inline-flex items-center justify-center gap-1.5 text-brand-700 font-extrabold text-xs uppercase tracking-wider">
                <QrCode className="w-4 h-4" />
                <span>Pindai QRIS untuk Membayar</span>
              </div>

              <div className="w-44 h-44 sm:w-48 sm:h-48 mx-auto bg-white p-3 rounded-2xl border border-brand-200 shadow-card flex items-center justify-center">
                <img
                  src="https://api.qrserver.com/v1/create-qr-code/?size=180x180&data=AROMA-MSME-QRIS-MOCK"
                  alt="Kode QRIS Pembayaran AROMA"
                  className="w-full h-full object-contain"
                />
              </div>

              <div className="space-y-1">
                <p className="font-extrabold text-charcoal text-base tabular-nums">
                  Nominal: {formatRupiah(order.total)}
                </p>
                <p className="text-xs font-body text-charcoal-secondary max-w-xs mx-auto leading-relaxed">
                  Setelah transfer QRIS berhasil, tunjukkan bukti pembayaran ke kasir untuk konfirmasi.
                </p>
              </div>
            </div>
          )}

          {/* Cash Payment Instructions */}
          {order.paymentMethod === 'CASH' && order.paymentStatus === 'PENDING' && !isCancelled && (
            <div className="p-4 rounded-2xl border border-cream-200 bg-cream-50 flex items-start gap-3">
              <div className="w-9 h-9 rounded-xl bg-white border border-cream-200 flex items-center justify-center text-brand-500 shrink-0">
                <Banknote className="w-4 h-4" />
              </div>
              <div className="text-xs space-y-1">
                <p className="font-bold text-charcoal">Pembayaran Tunai di Kasir</p>
                <p className="font-body text-charcoal-muted leading-relaxed">
                  Tunjukkan nomor pesanan <strong className="text-charcoal">{order.orderNumber}</strong> di
                  meja kasir sebesar{' '}
                  <strong className="text-charcoal tabular-nums">{formatRupiah(order.total)}</strong>.
                </p>
              </div>
            </div>
          )}

          {/* Digital Receipt / Order Summary Card */}
          <div className="rounded-2xl border border-cream-200 bg-cream-50/50 overflow-hidden">
            <div className="px-4 py-3 border-b border-cream-200 bg-cream-50 flex items-center justify-between gap-2">
              <div className="flex items-center gap-2">
                <Receipt className="w-4 h-4 text-brand-500" />
                <h4 className="font-extrabold text-xs text-charcoal uppercase tracking-wider">
                  {isPaid ? 'Struk Digital Pesanan' : 'Ringkasan Pesanan'}
                </h4>
              </div>
              <span
                className={`text-[10px] font-extrabold px-2.5 py-0.5 rounded-md border ${paymentStatusClass}`}
              >
                {paymentStatusLabel}
              </span>
            </div>

            {isPaid && (
              <div className="px-4 pt-3.5 pb-2 text-center border-b border-dashed border-cream-300 bg-white">
                <p className="font-extrabold text-xs text-charcoal">AROMA – Ayam Goreng Lengkuas</p>
                <p className="text-[11px] text-charcoal-muted mt-0.5">
                  Terima kasih, pembayaran Anda telah terverifikasi.
                </p>
              </div>
            )}

            {/* Itemized List */}
            <div className="divide-y divide-cream-200/70 bg-white">
              {order.orderDetails.map((detail) => (
                <div
                  key={detail.id}
                  className="px-4 py-3 flex items-start justify-between gap-3 text-xs"
                >
                  <div className="min-w-0 flex-1">
                    <p className="text-charcoal leading-snug">
                      <span className="font-extrabold tabular-nums">{detail.quantity}×</span>{' '}
                      <span className="font-semibold break-words">{detail.productName}</span>
                    </p>
                    {detail.notes && (
                      <p className="text-[11px] text-charcoal-muted mt-0.5 break-words">
                        Catatan: {detail.notes}
                      </p>
                    )}
                  </div>
                  <span className="font-bold text-charcoal shrink-0 tabular-nums">
                    {formatRupiah(detail.subtotal)}
                  </span>
                </div>
              ))}
            </div>

            {/* Metadata & Total */}
            <div className="p-4 border-t border-dashed border-cream-300 bg-cream-50 space-y-2 text-xs">
              {order.customerName && (
                <div className="flex justify-between gap-2">
                  <span className="text-charcoal-muted font-medium">Pemesan</span>
                  <span className="font-bold text-charcoal text-right">{order.customerName}</span>
                </div>
              )}
              <div className="flex justify-between gap-2">
                <span className="text-charcoal-muted font-medium">Tipe Pesanan</span>
                <span className="font-bold text-charcoal text-right">
                  {order.orderType === 'DINE_IN'
                    ? `Dine In (${order.table?.tableNumber || '-'})`
                    : 'Take Away'}
                </span>
              </div>
              <div className="flex justify-between gap-2">
                <span className="text-charcoal-muted font-medium">Metode Pembayaran</span>
                <span className="font-bold text-charcoal text-right">
                  {order.paymentMethod === 'CASH' ? 'Tunai di Kasir' : order.paymentMethod}
                </span>
              </div>
              <div className="pt-2.5 mt-1 border-t border-cream-200 flex items-baseline justify-between">
                <span className="font-extrabold text-sm text-charcoal">Total Pembayaran</span>
                <span className="font-extrabold text-base text-brand-500 tabular-nums">
                  {formatRupiah(order.total)}
                </span>
              </div>
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="p-4 sm:p-5 border-t border-cream-200 bg-cream-50 shrink-0">
          <button
            type="button"
            onClick={onClose}
            className="w-full min-h-[48px] py-3 rounded-xl bg-white border border-cream-200 text-charcoal font-bold text-sm hover:bg-cream-100 active:scale-[0.99] transition-all"
          >
            Tutup
          </button>
        </div>
      </div>
    </div>
  );
};
