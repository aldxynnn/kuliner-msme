import React, { useState, useEffect } from 'react';
import { Category, Product, CartItem, OrderType, Order, PaymentMethod } from './types';
import { fetchCategories, fetchProducts, createOrder, trackOrder } from './api';
import { Header } from './components/Header';
import { CategoryBar } from './components/CategoryBar';
import { ProductCard } from './components/ProductCard';
import { ProductDetailModal } from './components/ProductDetailModal';
import { CartDrawer } from './components/CartDrawer';
import { OrderTrackingModal } from './components/OrderTrackingModal';
import { Search, ShoppingBag, UtensilsCrossed, AlertTriangle } from 'lucide-react';
import { formatRupiah } from './utils/format';

export const App: React.FC = () => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [selectedCategoryId, setSelectedCategoryId] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [orderType, setOrderType] = useState<OrderType>('DINE_IN');
  const [tableNumber, setTableNumber] = useState<string | null>(null);

  // Cart State
  const [cartItems, setCartItems] = useState<CartItem[]>([]);
  const [isCartOpen, setIsCartOpen] = useState(false);
  const [selectedProductForModal, setSelectedProductForModal] = useState<Product | null>(null);

  // Order Tracking State
  const [activeOrder, setActiveOrder] = useState<Order | null>(null);
  const [isTrackingModalOpen, setIsTrackingModalOpen] = useState(false);

  // Initial Load: Read URL params for table
  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const tableParam = urlParams.get('table');
    if (tableParam) {
      setTableNumber(tableParam);
      setOrderType('DINE_IN');
    }

    // Read stored active order number
    const savedOrderNumber = localStorage.getItem('kuliner_active_order_number');
    if (savedOrderNumber) {
      trackOrder(savedOrderNumber)
        .then((ord) => setActiveOrder(ord))
        .catch(() => localStorage.removeItem('kuliner_active_order_number'));
    }
  }, []);

  // Poll Active Order Status every 5 seconds
  useEffect(() => {
    if (!activeOrder) return;

    const interval = setInterval(() => {
      trackOrder(activeOrder.orderNumber)
        .then((updated) => setActiveOrder(updated))
        .catch(() => {});
    }, 5000);

    return () => clearInterval(interval);
  }, [activeOrder?.orderNumber]);

  // Load Categories & Products
  useEffect(() => {
    fetchCategories()
      .then((data) => setCategories(data))
      .catch((err) => console.error(err));
  }, []);

  useEffect(() => {
    fetchProducts(selectedCategoryId || undefined, searchQuery || undefined)
      .then((data) => setProducts(data))
      .catch((err) => console.error(err));
  }, [selectedCategoryId, searchQuery]);

  // Cart Actions
  const handleAddToCart = (product: Product, quantity: number, notes: string) => {
    setCartItems((prev) => {
      const existingIndex = prev.findIndex((item) => item.product.id === product.id);
      if (existingIndex > -1) {
        const updated = [...prev];
        updated[existingIndex] = {
          ...updated[existingIndex],
          quantity: updated[existingIndex].quantity + quantity,
          notes: notes || updated[existingIndex].notes,
        };
        return updated;
      }
      return [...prev, { product, quantity, notes }];
    });
  };

  const handleUpdateQuantity = (productId: string, newQuantity: number) => {
    if (newQuantity <= 0) {
      handleRemoveItem(productId);
      return;
    }
    setCartItems((prev) =>
      prev.map((item) =>
        item.product.id === productId ? { ...item, quantity: newQuantity } : item
      )
    );
  };

  const handleRemoveItem = (productId: string) => {
    setCartItems((prev) => prev.filter((item) => item.product.id !== productId));
  };

  // Checkout Action
  const handleCheckout = async (
    customerName: string,
    paymentMethod: PaymentMethod,
    manualTableNumber?: string
  ) => {
    const payload = {
      customerName,
      orderType,
      tableNumber: orderType === 'DINE_IN' ? tableNumber || manualTableNumber : undefined,
      paymentMethod,
      items: cartItems.map((item) => ({
        productId: item.product.id,
        quantity: item.quantity,
        notes: item.notes,
      })),
    };

    const newOrder = await createOrder(payload);
    setActiveOrder(newOrder);
    localStorage.setItem('kuliner_active_order_number', newOrder.orderNumber);
    setCartItems([]);
    setIsTrackingModalOpen(true);
  };

  const totalCartCount = cartItems.reduce((acc, item) => acc + item.quantity, 0);
  const totalCartPrice = cartItems.reduce(
    (acc, item) => acc + item.product.price * item.quantity,
    0
  );

  return (
    <div className="min-h-screen pb-24 bg-cream-50 text-charcoal">
      {/* Navbar Header */}
      <Header
        tableNumber={tableNumber}
        orderType={orderType}
        onOrderTypeChange={(type) => setOrderType(type)}
        activeOrderNumber={activeOrder?.orderNumber || null}
        onOpenOrderTracking={() => setIsTrackingModalOpen(true)}
      />

      {/* Main Container */}
      <main className="max-w-xl mx-auto px-4 py-4 space-y-4">
        {/* Search Input */}
        <div className="relative">
          <Search className="w-4 h-4 absolute left-3.5 top-3 text-charcoal-light" />
          <input
            type="text"
            placeholder="Cari makanan atau minuman lezat..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-10 pr-4 py-2.5 rounded-2xl border border-cream-200 bg-white text-xs font-medium focus:outline-none focus:border-brand-500 shadow-sm"
          />
        </div>

        {/* Category Filter Bar */}
        <CategoryBar
          categories={categories}
          selectedCategoryId={selectedCategoryId}
          onSelectCategory={(id) => setSelectedCategoryId(id)}
        />

        {/* Product Cards Grid */}
        {products.length === 0 ? (
          <div className="text-center py-16 space-y-2">
            <UtensilsCrossed className="w-10 h-10 mx-auto text-charcoal-light opacity-50" />
            <p className="font-bold text-sm text-charcoal">Tidak ada menu ditemukan</p>
            <p className="text-xs text-charcoal-muted">
              Coba cari dengan kata kunci lain atau pilih kategori berbeda.
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-1 gap-3">
            {products.map((product) => {
              const inCart = cartItems.find((i) => i.product.id === product.id);
              return (
                <ProductCard
                  key={product.id}
                  product={product}
                  cartQuantity={inCart ? inCart.quantity : 0}
                  onSelectProduct={(p) => setSelectedProductForModal(p)}
                />
              );
            })}
          </div>
        )}
      </main>

      {/* Floating Bottom Cart Bar */}
      {totalCartCount > 0 && (
        <div className="fixed bottom-4 inset-x-4 max-w-xl mx-auto z-30">
          <button
            onClick={() => setIsCartOpen(true)}
            className="w-full bg-brand-500 hover:bg-brand-600 text-white rounded-2xl p-4 shadow-xl flex items-center justify-between active:scale-[0.99] transition-all"
          >
            <div className="flex items-center gap-3">
              <div className="w-8 h-8 rounded-full bg-white/20 flex items-center justify-center font-extrabold text-xs">
                {totalCartCount}
              </div>
              <div className="text-left">
                <p className="text-xs font-medium opacity-90">Keranjang Belanja</p>
                <p className="font-extrabold text-sm">{formatRupiah(totalCartPrice)}</p>
              </div>
            </div>

            <div className="flex items-center gap-1.5 font-bold text-xs bg-white text-brand-600 px-3 py-1.5 rounded-xl shadow-sm">
              <ShoppingBag className="w-3.5 h-3.5" />
              <span>Lihat Pesanan</span>
            </div>
          </button>
        </div>
      )}

      {/* Modals & Drawers */}
      <ProductDetailModal
        product={selectedProductForModal}
        onClose={() => setSelectedProductForModal(null)}
        onAddToCart={handleAddToCart}
      />

      <CartDrawer
        isOpen={isCartOpen}
        onClose={() => setIsCartOpen(false)}
        items={cartItems}
        orderType={orderType}
        tableNumber={tableNumber}
        onUpdateQuantity={handleUpdateQuantity}
        onRemoveItem={handleRemoveItem}
        onClearCart={() => setCartItems([])}
        onCheckout={handleCheckout}
      />

      <OrderTrackingModal
        order={activeOrder}
        onClose={() => setIsTrackingModalOpen(false)}
      />
    </div>
  );
};
