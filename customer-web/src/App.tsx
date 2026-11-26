import React, { useState, useEffect, useCallback } from 'react';
import { Category, Product, CartItem, OrderType, Order, PaymentMethod } from './types';
import { fetchCategories, fetchProducts, createOrder, trackOrder } from './api';
import { Header } from './components/Header';
import { HeroSection } from './components/HeroSection';
import { CategoryBar } from './components/CategoryBar';
import { ProductCard } from './components/ProductCard';
import { ProductDetailModal } from './components/ProductDetailModal';
import { CartDrawer, CartOrderTicketPanel } from './components/CartDrawer';
import { OrderTrackingModal } from './components/OrderTrackingModal';
import {
  Search,
  ShoppingBag,
  UtensilsCrossed,
  X,
  RefreshCw,
  AlertCircle,
  CheckCircle2,
  ArrowRight,
} from 'lucide-react';
import { formatRupiah } from './utils/format';

export const App: React.FC = () => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [heroFeaturedProduct, setHeroFeaturedProduct] = useState<Product | null>(null);
  const [selectedCategoryId, setSelectedCategoryId] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [orderType, setOrderType] = useState<OrderType>('DINE_IN');
  const [tableNumber, setTableNumber] = useState<string | null>(null);

  // Visual Loading & Error States
  const [isLoadingProducts, setIsLoadingProducts] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [addedToast, setAddedToast] = useState<string | null>(null);

  // Cart State
  const [cartItems, setCartItems] = useState<CartItem[]>([]);
  const [isCartOpen, setIsCartOpen] = useState(false);
  const [selectedProductForModal, setSelectedProductForModal] = useState<Product | null>(null);

  // Order Tracking State
  const [activeOrder, setActiveOrder] = useState<Order | null>(null);
  const [isTrackingModalOpen, setIsTrackingModalOpen] = useState(false);

  // Initial Load: Read URL params for table & stored order tracking
  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const tableParam = urlParams.get('table');
    if (tableParam) {
      setTableNumber(tableParam);
      setOrderType('DINE_IN');
    }

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

  // Load Categories
  const loadCategories = useCallback(() => {
    fetchCategories()
      .then((data) => setCategories(data))
      .catch((err) => console.error(err));
  }, []);

  useEffect(() => {
    loadCategories();
  }, [loadCategories]);

  // Load Products
  const loadProducts = useCallback(() => {
    let active = true;
    setIsLoadingProducts(true);
    setLoadError(null);

    fetchProducts(selectedCategoryId || undefined, searchQuery || undefined)
      .then((data) => {
        if (!active) return;
        setProducts(data);
        if (!selectedCategoryId && !searchQuery.trim() && data.length > 0) {
          const spotlight =
            data.find((p) => p.isAvailable && Boolean(p.image)) ||
            data.find((p) => p.isAvailable) ||
            data[0];
          setHeroFeaturedProduct(spotlight);
        }
      })
      .catch((err) => {
        if (!active) return;
        console.error(err);
        setLoadError('Tidak dapat memuat daftar menu. Pastikan backend aktif dan koneksi lancar.');
      })
      .finally(() => {
        if (active) setIsLoadingProducts(false);
      });

    return () => {
      active = false;
    };
  }, [selectedCategoryId, searchQuery]);

  useEffect(() => {
    return loadProducts();
  }, [loadProducts]);

  // Auto-dismiss cart toast feedback
  useEffect(() => {
    if (!addedToast) return;
    const timer = setTimeout(() => setAddedToast(null), 2200);
    return () => clearTimeout(timer);
  }, [addedToast]);

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
    setAddedToast(`${quantity}× ${product.name} masuk ke tiket`);
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

  const selectedCategoryName = selectedCategoryId
    ? categories.find((c) => c.id === selectedCategoryId)?.name || 'Menu Pilihan'
    : 'Semua Menu';

  const hasActiveFilters = Boolean(selectedCategoryId || searchQuery.trim());

  return (
    <div className="min-h-screen flex flex-col bg-paper-50 text-ink font-sans">
      {/* Unified Sticky Navigation Header + Underlined Category Rail */}
      <div className="sticky top-0 z-30">
        <Header
          tableNumber={tableNumber}
          orderType={orderType}
          onOrderTypeChange={(type) => setOrderType(type)}
          activeOrderNumber={activeOrder?.orderNumber || null}
          activeOrderStatus={activeOrder?.orderStatus}
          onOpenOrderTracking={() => setIsTrackingModalOpen(true)}
        />

        <CategoryBar
          categories={categories}
          selectedCategoryId={selectedCategoryId}
          onSelectCategory={(id) => setSelectedCategoryId(id)}
        />
      </div>

      {/* Main Content */}
      <main className="flex-1 w-full max-w-7xl mx-auto px-4 sm:px-6 pt-5 sm:pt-7 pb-32 lg:pb-16 space-y-6 sm:space-y-8">
        {/* Two-Panel Editorial Culinary Hero */}
        <HeroSection
          tableNumber={tableNumber}
          orderType={orderType}
          featuredProduct={heroFeaturedProduct}
          onSelectFeatured={(p) => setSelectedProductForModal(p)}
        />

        {/* Desktop Split Workspace: Left Menu Catalog + Right Persistent Order Ticket */}
        <div className="grid grid-cols-1 lg:grid-cols-[minmax(0,1fr)_360px] xl:grid-cols-[minmax(0,1fr)_380px] gap-6 xl:gap-8 items-start">
          {/* Left Catalog Workspace */}
          <div className="space-y-5 min-w-0">
            {/* Search & Section Toolbar */}
            <section
              aria-label="Pencarian Menu"
              className="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-3 pb-3 border-b border-paper-200"
            >
              <div>
                <h2 className="font-display font-bold text-2xl sm:text-[28px] text-ink tracking-tight leading-none">
                  {selectedCategoryName}
                </h2>
                <p className="font-mono text-[11px] text-ink-muted mt-1.5 uppercase tracking-wider">
                  {isLoadingProducts
                    ? 'Memuat daftar hidangan...'
                    : `${products.length} menu tersedia`}
                </p>
              </div>

              <div className="relative w-full sm:w-72">
                <Search className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-ink-muted pointer-events-none" />
                <input
                  type="text"
                  placeholder="Cari ayam, minuman, sambal..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  aria-label="Cari menu"
                  className="w-full min-h-[42px] pl-10 pr-10 py-2 rounded-full border border-paper-200 bg-white text-sm text-ink placeholder:text-ink-light focus:outline-none focus:border-ink transition-colors"
                />
                {searchQuery && (
                  <button
                    type="button"
                    onClick={() => setSearchQuery('')}
                    aria-label="Hapus pencarian"
                    className="absolute right-2.5 top-1/2 -translate-y-1/2 w-7 h-7 rounded-full text-ink-muted hover:text-ink hover:bg-paper-100 flex items-center justify-center transition-colors"
                  >
                    <X className="w-4 h-4" />
                  </button>
                )}
              </div>
            </section>

            {/* Menu Grid States */}
            {isLoadingProducts ? (
              <div
                aria-label="Memuat menu"
                className="grid grid-cols-1 sm:grid-cols-2 gap-4 sm:gap-5"
              >
                {Array.from({ length: 6 }).map((_, idx) => (
                  <div
                    key={idx}
                    className="bg-white rounded-2xl border border-paper-200 overflow-hidden flex flex-row sm:flex-col animate-pulse"
                  >
                    <div className="flex-1 p-4 space-y-2.5">
                      <div className="h-3 w-16 rounded bg-paper-100" />
                      <div className="h-4 w-3/4 rounded bg-paper-200" />
                      <div className="h-3 w-full rounded bg-paper-100" />
                      <div className="pt-3 flex items-center justify-between">
                        <div className="h-4 w-20 rounded bg-paper-200" />
                        <div className="h-8 w-20 rounded-full bg-paper-100 hidden sm:block" />
                      </div>
                    </div>
                    <div className="w-[104px] h-[104px] m-3.5 sm:m-0 sm:order-first sm:w-full sm:h-44 rounded-xl sm:rounded-none bg-paper-100 shrink-0" />
                  </div>
                ))}
              </div>
            ) : loadError ? (
              <div className="bg-white rounded-2xl border border-semantic-error-border p-8 sm:p-10 text-center space-y-4">
                <div className="w-12 h-12 mx-auto rounded-2xl bg-semantic-error-bg text-semantic-error flex items-center justify-center">
                  <AlertCircle className="w-6 h-6" />
                </div>
                <div className="space-y-1">
                  <h3 className="font-bold text-base text-ink">Gagal Memuat Menu</h3>
                  <p className="text-xs sm:text-sm text-ink-muted max-w-sm mx-auto">
                    {loadError}
                  </p>
                </div>
                <button
                  type="button"
                  onClick={() => {
                    loadCategories();
                    loadProducts();
                  }}
                  className="min-h-[42px] inline-flex items-center gap-2 px-5 py-2.5 rounded-full bg-ink hover:bg-spice-500 text-white text-xs font-bold transition-colors"
                >
                  <RefreshCw className="w-4 h-4" />
                  <span>Coba Muat Ulang</span>
                </button>
              </div>
            ) : products.length === 0 ? (
              <div className="bg-white rounded-2xl border border-paper-200 p-10 sm:p-14 text-center space-y-4">
                <div className="w-14 h-14 mx-auto rounded-2xl bg-paper-100 border border-paper-200 flex items-center justify-center text-ink-muted">
                  <UtensilsCrossed className="w-6 h-6" />
                </div>
                <div className="space-y-1">
                  <h3 className="font-bold text-base text-ink">Tidak ada menu ditemukan</h3>
                  <p className="text-xs sm:text-sm text-ink-muted max-w-xs mx-auto">
                    Coba gunakan kata kunci lain atau lihat seluruh kategori menu yang tersedia.
                  </p>
                </div>
                {hasActiveFilters && (
                  <button
                    type="button"
                    onClick={() => {
                      setSelectedCategoryId(null);
                      setSearchQuery('');
                    }}
                    className="min-h-[40px] inline-flex items-center gap-2 px-4 py-2 rounded-full bg-paper-100 hover:bg-paper-200 text-ink text-xs font-bold border border-paper-200 transition-colors"
                  >
                    <span>Tampilkan Semua Menu</span>
                  </button>
                )}
              </div>
            ) : (
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 sm:gap-5">
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
          </div>

          {/* Desktop Persistent Live Order Ticket */}
          <div className="hidden lg:block sticky top-[132px]">
            <CartOrderTicketPanel
              items={cartItems}
              orderType={orderType}
              tableNumber={tableNumber}
              onUpdateQuantity={handleUpdateQuantity}
              onRemoveItem={handleRemoveItem}
              onClearCart={() => setCartItems([])}
              onCheckout={handleCheckout}
            />
          </div>
        </div>
      </main>

      {/* Architectural Store Footer */}
      <footer className="border-t border-paper-200 bg-white py-6 px-4 text-center">
        <p className="font-display font-bold text-sm text-ink">
          AROMA – Ayam Goreng Lengkuas
        </p>
        <p className="text-xs text-ink-muted mt-1">
          Ruko Puri Cempaka Blok R1 No. 10, Panancangan, Cipocok Jaya, Kota Serang, Banten
        </p>
      </footer>

      {/* Cart Feedback Toast */}
      {addedToast && (
        <div
          role="status"
          className={`fixed left-1/2 -translate-x-1/2 z-40 px-4 py-2.5 rounded-full bg-ink text-white text-xs font-bold shadow-dock flex items-center gap-2 max-w-[90vw] animate-aroma-sheet ${
            totalCartCount > 0 ? 'bottom-24 lg:bottom-6' : 'bottom-6'
          }`}
        >
          <CheckCircle2 className="w-4 h-4 text-pandan-200 shrink-0" />
          <span className="truncate">{addedToast}</span>
        </div>
      )}

      {/* Mobile & Tablet Floating Deep Roast Bottom Dock */}
      {totalCartCount > 0 && (
        <div className="lg:hidden fixed bottom-4 inset-x-4 max-w-xl mx-auto z-30">
          <button
            type="button"
            onClick={() => setIsCartOpen(true)}
            className="w-full min-h-[64px] bg-ink hover:bg-ink-soft text-white rounded-2xl px-4 py-3 shadow-dock flex items-center justify-between gap-3 active:scale-[0.99] transition-all"
          >
            <div className="flex items-center gap-3 min-w-0">
              <div className="w-10 h-10 rounded-xl bg-spice-500 text-white flex items-center justify-center font-mono font-bold text-sm tabular-nums shrink-0">
                {totalCartCount}
              </div>
              <div className="text-left min-w-0">
                <p className="font-mono text-[10px] font-semibold text-paper-300 uppercase tracking-wider whitespace-nowrap">
                  Tiket Pesanan
                </p>
                <p className="font-mono font-bold text-base text-white tabular-nums whitespace-nowrap">
                  {formatRupiah(totalCartPrice)}
                </p>
              </div>
            </div>

            <div className="inline-flex items-center gap-1.5 font-bold text-xs bg-white text-ink px-4 py-2.5 rounded-xl shrink-0">
              <ShoppingBag className="w-4 h-4" />
              <span>Lihat Pesanan</span>
              <ArrowRight className="w-3.5 h-3.5" />
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
