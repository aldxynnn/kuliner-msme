import { Category, Product, Table, Order, OrderType, PaymentMethod } from './types';

const API_BASE_URL = (import.meta as any).env?.VITE_API_BASE_URL || 'http://localhost:3000/api';
export const BACKEND_URL = API_BASE_URL.replace(/\/api\/?$/, '');

export function getImageUrl(imagePath?: string | null): string {
  if (!imagePath) return '';
  if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
    return imagePath;
  }
  return `${BACKEND_URL}${imagePath.startsWith('/') ? '' : '/'}${imagePath}`;
}

export async function fetchCategories(): Promise<Category[]> {
  const res = await fetch(`${API_BASE_URL}/categories`);
  if (!res.ok) throw new Error('Gagal mengambil daftar kategori');
  return res.json();
}

export async function fetchProducts(categoryId?: string, search?: string): Promise<Product[]> {
  const params = new URLSearchParams();
  if (categoryId) params.append('categoryId', categoryId);
  if (search) params.append('search', search);

  const res = await fetch(`${API_BASE_URL}/products?${params.toString()}`);
  if (!res.ok) throw new Error('Gagal mengambil daftar produk');
  return res.json();
}

export async function fetchTableByNumber(tableNumber: string): Promise<Table | null> {
  try {
    const res = await fetch(`${API_BASE_URL}/tables/by-number/${encodeURIComponent(tableNumber)}`);
    if (!res.ok) return null;
    return res.json();
  } catch (err) {
    return null;
  }
}

export interface CreateOrderPayload {
  customerName?: string;
  orderType: OrderType;
  tableNumber?: string;
  paymentMethod: PaymentMethod;
  notes?: string;
  items: {
    productId: string;
    quantity: number;
    notes?: string;
  }[];
}

export async function createOrder(payload: CreateOrderPayload): Promise<Order> {
  const res = await fetch(`${API_BASE_URL}/orders`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });

  if (!res.ok) {
    const errorData = await res.json().catch(() => ({}));
    throw new Error(errorData.message || 'Gagal membuat pesanan');
  }

  return res.json();
}

export async function trackOrder(orderNumber: string): Promise<Order> {
  const res = await fetch(`${API_BASE_URL}/orders/track/${encodeURIComponent(orderNumber)}`);
  if (!res.ok) {
    throw new Error('Pesanan tidak ditemukan');
  }
  return res.json();
}
