export interface Category {
  id: string;
  name: string;
  description?: string;
  _count?: {
    products: number;
  };
}

export interface Product {
  id: string;
  categoryId: string;
  category?: Category;
  name: string;
  description?: string;
  price: number;
  image?: string;
  isAvailable: boolean;
}

export interface CartItem {
  product: Product;
  quantity: number;
  notes?: string;
}

export type OrderType = 'DINE_IN' | 'TAKE_AWAY';
export type PaymentMethod = 'QRIS' | 'CASH';
export type PaymentStatus = 'PENDING' | 'PAID' | 'CANCELLED';
export type OrderStatus =
  | 'MENUNGGU_PEMBAYARAN'
  | 'MENUNGGU_DIPROSES'
  | 'DIPROSES'
  | 'SIAP_DISAJIKAN'
  | 'SELESAI'
  | 'DIBATALKAN';

export interface OrderDetail {
  id: string;
  productId: string;
  productName: string;
  price: number;
  quantity: number;
  notes?: string;
  subtotal: number;
}

export interface Table {
  id: string;
  tableNumber: string;
  qrCode: string;
  isAvailable: boolean;
}

export interface Order {
  id: string;
  orderNumber: string;
  customerName?: string;
  orderType: OrderType;
  tableId?: string;
  table?: Table;
  paymentMethod: PaymentMethod;
  paymentStatus: PaymentStatus;
  orderStatus: OrderStatus;
  subtotal: number;
  total: number;
  notes?: string;
  createdAt: string;
  orderDetails: OrderDetail[];
}
