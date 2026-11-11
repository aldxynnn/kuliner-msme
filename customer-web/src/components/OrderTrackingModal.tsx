import React from 'react';
import { Order } from '../types';
import { formatRupiah, formatDate } from '../utils/format';
import { X, CheckCircle2, Clock, QrCode, Receipt, Utensils, AlertCircle } from 'lucide-react';

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
          color: 'bg-amber-100 text-amber-800 border-amber-300',
          desc: 'Silakan lakukan pembayaran QRIS di bawah atau langsung ke meja kasir.',
        };
      case 'MENUNGGU_DIPROSES':
        return {
          label: 'Pesanan Diterima Kasir',
          color: 'bg-blue-100 text-blue-800 border-blue-300',
          desc: 'Pembayaran terkonfirmasi. Pesanan masuk antrean dapur.',
        };
      case 'DIPROSES':
        return {
          label: 'Sedang Dimasak Dapur',
          color: 'bg-indigo-100 text-indigo-800 border-indigo-300',
          desc: 'Koki sedang menyiapkan hidangan lezat Anda.',
        };
      case 'SIAP_DISAJIKAN':
        return {
          label: 'Siap Disajikan / Diambil',
          color: 'bg-emerald-100 text-emerald-800 border-emerald-300',
          desc: 'Pesanan Anda sudah siap! Pelayan akan menyajikan ke meja Anda.',
        };
      case 'SELESAI':
        return {
          label: 'Pesanan Selesai',
          color: 'bg-gray-100 text-gray-800 border-gray-300',
          desc: 'Terima kasih telah berkunjung ke Kuliner Resto!',
        };
      case 'DIBATALKAN':
        return {
          label: 'Pesanan Dibatalkan',
          color: 'bg-red-100 text-red-800 border-red-300',
          desc: 'Pesanan ini telah dibatalkan.',
        };
      default:
        return {
          label: order.orderStatus,
          color: 'bg-gray-100 text-gray-800 border-gray-300',
          desc: '',
        };
    }
  };

  const statusInfo = getStatusBadge();

  return (
    <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="bg-white w-full max-w-lg rounded-3xl overflow-hidden shadow-2xl animate-in zoom-in-95 duration-200 flex flex-col max-h-[90vh]">
        {/* Header */}
        <div className="p-4 border-b border-cream-200 bg-cream-50 flex items-center justify-between shrink-0">
          <div>
            <span className="text-[10px] font-bold text-brand-600 uppercase tracking-wider">
              Status Live Pesanan
            </span>
            <h3 className="font-extrabold text-base text-charcoal">{order.orderNumber}</h3>
          </div>
          <button
            onClick={onClose}
            className="w-8 h-8 rounded-full bg-cream-200 text-charcoal flex items-center justify-center hover:bg-cream-300"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Scrollable Body */}
        <div className="p-5 overflow-y-auto space-y-5">
          {/* Status Banner */}
          <div className={`p-4 rounded-2xl border ${statusInfo.color} space-y-1`}>
            <div className="flex items-center justify-between">
              <span className="font-bold text-sm">{statusInfo.label}</span>
              <span className="text-[11px] font-semibold opacity-80">
                {formatDate(order.createdAt)}
              </span>
            </div>
            <p className="text-xs opacity-90 leading-relaxed">{statusInfo.desc}</p>
          </div>

          {/* QRIS Payment Card */}
          {order.paymentMethod === 'QRIS' && order.paymentStatus === 'PENDING' && (
            <div className="p-4 rounded-2xl border border-brand-200 bg-brand-50/60 text-center space-y-3">
              <div className="flex items-center justify-center gap-1.5 text-brand-700 font-bold text-xs">
                <QrCode className="w-4 h-4" />
                <span>Pindai QRIS untuk Membayar</span>
              </div>

              {/* QR Image Placeholder */}
              <div className="w-44 h-44 mx-auto bg-white p-2 rounded-2xl border border-brand-200 shadow-sm flex items-center justify-center">
                <img
                  src="https://api.qrserver.com/v1/create-qr-code/?size=180x180&data=KULINER-MSME-QRIS-MOCK"
                  alt="QRIS Payment"
                  className="w-full h-full object-contain"
                />
              </div>

              <div className="text-[11px] text-charcoal-muted space-y-1">
                <p className="font-semibold text-charcoal">Nominal: {formatRupiah(order.total)}</p>
                <p>Setelah melakukan transfer QRIS, tunjukkan bukti transfer ke kasir.</p>
              </div>
            </div>
          )}

          {/* Order Details */}
          <div className="space-y-3">
            <h4 className="font-bold text-xs text-charcoal uppercase tracking-wider flex items-center gap-1.5">
              <Utensils className="w-3.5 h-3.5 text-brand-500" />
              <span>Rincian Item Pesanan</span>
            </h4>

            <div className="divide-y divide-cream-100 border border-cream-200 rounded-xl overflow-hidden bg-cream-50/30">
              {order.orderDetails.map((detail) => (
                <div key={detail.id} className="p-3 flex items-center justify-between text-xs">
                  <div>
                    <span className="font-bold text-charcoal">{detail.quantity}x </span>
                    <span className="text-charcoal">{detail.productName}</span>
                    {detail.notes && (
                      <p className="text-[11px] text-charcoal-muted italic mt-0.5">
                        Note: {detail.notes}
                      </p>
                    )}
                  </div>
                  <span className="font-bold text-charcoal shrink-0">
                    {formatRupiah(detail.subtotal)}
                  </span>
                </div>
              ))}
            </div>

            {/* Receipt Summary */}
            <div className="p-3.5 rounded-xl bg-cream-100/70 text-xs space-y-1.5">
              <div className="flex justify-between text-charcoal-muted">
                <span>Tipe Pemesanan</span>
                <span className="font-semibold text-charcoal">
                  {order.orderType === 'DINE_IN' ? `Dine In (${order.table?.tableNumber || '-'})` : 'Take Away'}
                </span>
              </div>
              <div className="flex justify-between text-charcoal-muted">
                <span>Metode Pembayaran</span>
                <span className="font-semibold text-charcoal">{order.paymentMethod}</span>
              </div>
              <div className="flex justify-between text-charcoal-muted">
                <span>Status Pembayaran</span>
                <span
                  className={`font-bold ${
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
              <div className="pt-2 border-t border-cream-200 flex justify-between font-bold text-sm text-charcoal">
                <span>Total</span>
                <span className="text-brand-600">{formatRupiah(order.total)}</span>
              </div>
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="p-4 border-t border-cream-200 bg-cream-50 shrink-0">
          <button
            onClick={onClose}
            className="w-full py-3 rounded-xl bg-charcoal text-white font-bold text-xs hover:bg-black transition-colors"
          >
            Tutup
          </button>
        </div>
      </div>
    </div>
  );
};
