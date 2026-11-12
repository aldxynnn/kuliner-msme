import React from 'react';
import { Order } from '../types';
import { formatRupiah, formatDate } from '../utils/format';
import { X, Clock, QrCode, Utensils } from 'lucide-react';

interface OrderTrackingModalProps {
  order: Order | null;
  onClose: () => void;
}

export const OrderTrackingModal: React.FC<OrderTrackingModalProps> = ({ order, onClose }) => {
  if (!order) return null;

  const getStatusBadge = () => {
    switch (order.orderStatus) {
      case 'MENUNGGU_PEMBAYARAN':
        return {
          label: 'Menunggu Pembayaran',
          color: 'bg-amber-50 text-amber-900 border-amber-200',
          desc: 'Silakan lakukan pembayaran QRIS di bawah atau langsung ke meja kasir.',
        };
      case 'MENUNGGU_DIPROSES':
        return {
          label: 'Pesanan Diterima Kasir',
          color: 'bg-blue-50 text-blue-900 border-blue-200',
          desc: 'Pembayaran terkonfirmasi. Pesanan masuk antrean dapur.',
        };
      case 'DIPROSES':
        return {
          label: 'Sedang Dimasak Dapur',
          color: 'bg-indigo-50 text-indigo-900 border-indigo-200',
          desc: 'Koki sedang menyiapkan hidangan lezat AROMA.',
        };
      case 'SIAP_DISAJIKAN':
        return {
          label: 'Siap Disajikan / Diambil',
          color: 'bg-emerald-50 text-emerald-900 border-emerald-200',
          desc: 'Pesanan Anda sudah siap! Pelayan akan menyajikan ke meja Anda.',
        };
      case 'SELESAI':
        return {
          label: 'Pesanan Selesai',
          color: 'bg-gray-100 text-gray-900 border-gray-200',
          desc: 'Terima kasih telah menikmati sajian AROMA – Ayam Goreng Lengkuas!',
        };
      case 'DIBATALKAN':
        return {
          label: 'Pesanan Dibatalkan',
          color: 'bg-red-50 text-red-900 border-red-200',
          desc: 'Pesanan ini telah dibatalkan.',
        };
      default:
        return {
          label: order.orderStatus,
          color: 'bg-gray-100 text-gray-900 border-gray-200',
          desc: '',
        };
    }
  };

  const statusInfo = getStatusBadge();

  return (
    <div className="fixed inset-0 z-50 bg-black/65 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="bg-white w-full max-w-lg rounded-[32px] overflow-hidden shadow-2xl animate-in zoom-in-95 duration-200 flex flex-col max-h-[90vh]">
        {/* Header */}
        <div className="p-5 border-b border-cream-200 bg-cream-50 flex items-center justify-between shrink-0">
          <div>
            <span className="text-[10px] font-extrabold text-brand-600 uppercase tracking-widest">
              Status Live Pesanan
            </span>
            <h3 className="font-extrabold text-lg text-charcoal break-all">{order.orderNumber}</h3>
          </div>
          <button
            onClick={onClose}
            className="w-9 h-9 rounded-full bg-cream-200 text-charcoal flex items-center justify-center hover:bg-cream-300 transition-colors shadow-xs shrink-0"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Scrollable Body */}
        <div className="p-6 overflow-y-auto space-y-6">
          {/* Status Banner */}
          <div className={`p-4.5 rounded-2xl border ${statusInfo.color} space-y-2 shadow-xs`}>
            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-1">
              <span className="font-extrabold text-sm">{statusInfo.label}</span>
              <span className="text-[11px] font-semibold opacity-75 shrink-0">
                {formatDate(order.createdAt)}
              </span>
            </div>
            <p className="text-xs opacity-90 leading-relaxed font-medium">{statusInfo.desc}</p>
          </div>

          {/* QRIS Payment Card */}
          {order.paymentMethod === 'QRIS' && order.paymentStatus === 'PENDING' && (
            <div className="p-5 rounded-3xl border border-brand-200 bg-brand-50/60 text-center space-y-3.5 shadow-sm">
              <div className="flex items-center justify-center gap-1.5 text-brand-700 font-extrabold text-xs">
                <QrCode className="w-4 h-4" />
                <span>Pindai QRIS untuk Membayar</span>
              </div>

              {/* QR Image Placeholder */}
              <div className="w-48 h-48 mx-auto bg-white p-3 rounded-2xl border border-brand-200 shadow-md flex items-center justify-center">
                <img
                  src="https://api.qrserver.com/v1/create-qr-code/?size=180x180&data=AROMA-MSME-QRIS-MOCK"
                  alt="QRIS Payment"
                  className="w-full h-full object-contain"
                />
              </div>

              <div className="text-xs text-charcoal-muted space-y-1">
                <p className="font-extrabold text-charcoal text-sm">Nominal: {formatRupiah(order.total)}</p>
                <p className="text-[11px]">Setelah melakukan transfer QRIS, tunjukkan bukti transfer ke kasir.</p>
              </div>
            </div>
          )}

          {/* Order Details */}
          <div className="space-y-3">
            <h4 className="font-extrabold text-xs text-charcoal uppercase tracking-wider flex items-center gap-1.5">
              <Utensils className="w-3.5 h-3.5 text-brand-500" />
              <span>Rincian Item Pesanan</span>
            </h4>

            <div className="divide-y divide-cream-100 border border-cream-200 rounded-2xl overflow-hidden bg-cream-50/40">
              {order.orderDetails.map((detail) => (
                <div key={detail.id} className="p-3.5 flex items-center justify-between gap-3 text-xs">
                  <div className="min-w-0 flex-1">
                    <span className="font-extrabold text-charcoal">{detail.quantity}x </span>
                    <span className="font-semibold text-charcoal break-words">{detail.productName}</span>
                    {detail.notes && (
                      <p className="text-[11px] text-charcoal-muted italic mt-0.5 break-words">
                        Note: {detail.notes}
                      </p>
                    )}
                  </div>
                  <span className="font-extrabold text-charcoal shrink-0">
                    {formatRupiah(detail.subtotal)}
                  </span>
                </div>
              ))}
            </div>

            {/* Receipt Summary */}
            <div className="p-4 rounded-2xl bg-cream-100/70 text-xs space-y-2 border border-cream-200/80">
              <div className="flex justify-between text-charcoal-muted font-medium">
                <span>Tipe Pemesanan</span>
                <span className="font-bold text-charcoal">
                  {order.orderType === 'DINE_IN' ? `Dine In (${order.table?.tableNumber || '-'})` : 'Take Away'}
                </span>
              </div>
              <div className="flex justify-between text-charcoal-muted font-medium">
                <span>Metode Pembayaran</span>
                <span className="font-bold text-charcoal">{order.paymentMethod}</span>
              </div>
              <div className="flex justify-between text-charcoal-muted font-medium">
                <span>Status Pembayaran</span>
                <span
                  className={`font-extrabold ${
                    order.paymentStatus === 'PAID'
                      ? 'text-emerald-600'
                      : order.paymentStatus === 'CANCELLED'
                      ? 'text-red-600'
                      : 'text-amber-600'
                  }`}
                >
                  {order.paymentStatus === 'PAID' ? 'LUNAS' : order.paymentStatus}
                </span>
              </div>
              <div className="pt-2.5 border-t border-cream-200 flex justify-between font-extrabold text-sm text-charcoal">
                <span>Total Pembayaran</span>
                <span className="text-brand-600 text-base">{formatRupiah(order.total)}</span>
              </div>
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="p-5 border-t border-cream-200 bg-cream-50 shrink-0">
          <button
            onClick={onClose}
            className="w-full py-3.5 rounded-2xl bg-charcoal text-white font-extrabold text-xs hover:bg-black transition-colors shadow-md shadow-charcoal/10"
          >
            Tutup
          </button>
        </div>
      </div>
    </div>
  );
};
