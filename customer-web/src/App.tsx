import React, { useState, useEffect } from 'react';
import { Category, Product, CartItem, OrderType, Order, PaymentMethod } from './types';
import { fetchCategories, fetchProducts, createOrder, trackOrder } from './api';
import { Header } from './components/Header';
import { HeroSection } from './components/HeroSection';
import { CategoryBar } from './components/CategoryBar';
import { ProductCard } from './components/ProductCard';
import { ProductDetailModal } from './components/ProductDetailModal';
import { CartDrawer } from './components/CartDrawer';
import { OrderTrackingModal } from './components/OrderTrackingModal';
import { Search, ShoppingBag, UtensilsCrossed } from 'lucide-react';
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
    <div className="min-h-screen pb-28 bg-cream-50 text-charcoal font-sans">
      {/* Navbar Header */}
      <Header
        tableNumber={tableNumber}
        orderType={orderType}
        onOrderTypeChange={(type) => setOrderType(type)}
        activeOrderNumber={activeOrder?.orderNumber || null}
        onOpenOrderTracking={() => setIsTrackingModalOpen(true)}
      />

      {/* Main Container */}
      <main className="max-w-4xl mx-auto px-4 py-6 space-y-6">
        {/* Hero Banner Section */}
        <HeroSection />

        {/* Search Input */}
        <div className="relative max-w-lg mx-auto">
          <Search className="w-4 h-4 absolute left-4 top-3.5 text-charcoal-muted" />
          <input
            type="text"
            placeholder="Cari menu ayam goreng, minuman, atau camilan..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-11 pr-4 py-3 rounded-2xl border border-cream-200 bg-white text-xs font-semibold focus:outline-none focus:border-brand-500 shadow-xs transition-all"
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
          <div className="text-center py-20 space-y-3 bg-white rounded-3xl border border-cream-200 shadow-xs">
            <UtensilsCrossed className="w-12 h-12 mx-auto text-charcoal-muted opacity-40" />
            <h3 className="font-extrabold text-base text-charcoal">Tidak ada menu ditemukan</h3>
            <p className="text-xs text-charcoal-muted max-w-xs mx-auto">
              Coba cari dengan kata kunci lain atau pilih kategori berbeda.
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
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
        <div className="fixed bottom-5 inset-x-4 max-w-xl mx-auto z-30">
          <button
            onClick={() => setIsCartOpen(true)}
            className="w-full bg-brand-500 hover:bg-brand-600 text-white rounded-2xl p-4 shadow-xl shadow-brand-500/25 flex items-center justify-between active:scale-[0.99] transition-all"
          >
            <div className="flex items-center gap-3">
              <div className="w-9 h-9 rounded-xl bg-white/20 backdrop-blur-md flex items-center justify-center font-extrabold text-xs">
                {totalCartCount}
              </div>
              <div className="text-left">
                <p className="text-[11px] font-semibold text-brand-100 tracking-wider uppercase">Keranjang</p>
                <p className="font-extrabold text-sm md:text-base">{formatRupiah(totalCartPrice)}</p>
              </div>
            </div>

            <div className="flex items-center gap-2 font-extrabold text-xs bg-white text-brand-600 px-4 py-2.5 rounded-xl shadow-sm">
              <ShoppingBag className="w-4 h-4" />
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

      {isTrackingModalOpen && activeOrder && (
        <OrderTrackingModal
          order={activeOrder}
          onClose={() => setIsTrackingModalOpen(false)}
        />
      )}
    </div>
  );
};
