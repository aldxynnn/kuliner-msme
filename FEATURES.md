# SYSTEM FEATURES — AROMA POS

Dokumen ini mendokumentasikan seluruh fitur yang **benar-benar diimplementasikan** pada repository AROMA POS beserta aturan bisnis yang berlaku.

---

## A. CUSTOMER ORDERING WEBSITE (`customer-web`)

Pengalaman pemesanan makanan berbasis web yang responsif (*mobile-first* hingga desktop).

### 1. Akses Meja via QR & Tipe Pesanan
- Mendeteksi parameter URL `?table=<Nama Meja>` secara otomatis saat pelanggan memindai QR meja.
- Mendukung pilihan tipe pesanan **Dine In** (Makan di Tempat) dan **Take Away** (Bawa Pulang).
- Nomor meja wajib tersedia untuk pesanan *Dine In* (otomatis dari URL QR atau input manual pada drawer keranjang bila tidak ada parameter URL); tidak diperlukan untuk *Take Away*.

### 2. Katalog Menu & Pencarian
- Menampilkan daftar kategori dan menu dari backend (`GET /api/categories`, `GET /api/products`) sesuai urutan `sortOrder`.
- Bar kategori *sticky* yang mudah diakses saat menggulir menu.
- Pencarian menu berdasarkan kata kunci.
- Menampilkan foto produk, kategori, nama, deskripsi, harga dalam Rupiah, serta status ketersediaan (*Tersedia* / *Habis*).
- Modal detail produk untuk memilih kuantitas dan menambahkan catatan khusus per item.

### 3. Keranjang & Checkout
- Floating cart dock yang menampilkan jumlah item dan total biaya secara langsung.
- Cart drawer untuk mengubah kuantitas, menghapus item, melihat subtotal per item dan total pembayaran.
- Input nama pemesan (opsional, default `"Pelanggan AROMA"`).
- Pemilihan metode pembayaran: **QRIS** atau **Bayar di Kasir (Tunai)**.

### 4. Pembayaran & Pelacakan Pesanan Live
- **QRIS:** Menampilkan kode QRIS dan nominal pembayaran; verifikasi pembayaran dilakukan secara manual oleh kasir (bukan verifikasi otomatis gateway).
- **Tunai di Kasir:** Pesanan berstatus belum dibayar hingga kasir melakukan konfirmasi.
- Pelacakan pesanan real-time dengan polling otomatis setiap 5 detik (`GET /api/orders/track/:orderNumber`) dan penyimpanan nomor pesanan aktif di `localStorage`.
- Menampilkan tahapan progres pesanan, status pembayaran, rincian item, serta tampilan ringkasan struk digital saat pembayaran berstatus `PAID`.

---

## B. ANDROID CASHIER & ADMIN APPLICATION (`admin-android`)

Aplikasi POS native Android berbasis Jetpack Compose & Material 3.

### 1. Autentikasi & Profil Akun
- Login menggunakan email & password ke `POST /api/auth/login` dengan penyimpanan token JWT di `TokenManager`.
- Proteksi akses layar operasional hingga pengguna login.
- Layar **Profil & Akun** (`ProfileScreen`) untuk melihat informasi akun, status sesi aktif, dan melakukan logout aman (menghentikan layanan monitor notifikasi & membersihkan sesi).

### 2. Dashboard Operasional (`DashboardScreen`)
- Menampilkan omzet lunas hari ini, jumlah transaksi lunas, jumlah pesanan selesai hari ini, pesanan aktif dapur, dan jumlah pembayaran menunggu konfirmasi (`GET /api/reports/dashboard`).
- Akses cepat menuju daftar pesanan, pembayaran, pusat notifikasi, pengelolaan kategori, meja QR, biaya operasional, laporan surplus, dan profil akun.

### 3. Kasir POS Manual (`CashierOrderScreen`)
- Pembuatan pesanan langsung dari meja kasir (`POST /api/orders/cashier`).
- Mendukung pilihan *Dine In* (dengan pemilih meja) dan *Take Away*, filter kategori, grid menu, keranjang bottom sheet dengan pengaturan kuantitas, nama pelanggan opsional, serta metode pembayaran Tunai/QRIS.

### 4. Manajemen Pesanan & Dapur (`OrdersScreen`)
- Menampilkan daftar pesanan pelanggan dan kasir dengan filter status:
  - `MENUNGGU_PEMBAYARAN`
  - `MENUNGGU_DIPROSES`
  - `DIPROSES`
  - `SIAP_DISAJIKAN`
  - `SELESAI`
  - `DIBATALKAN`
- Status pembayaran (`PENDING`, `PAID`, `CANCELLED`) ditampilkan terpisah dari status pesanan.
- Pesanan yang belum dibayar tidak dapat masuk ke proses dapur dan dapat dibatalkan oleh kasir (`POST /api/orders/:id/cancel`).
- Pembaruan status dapur bertahap (`MENUNGGU_DIPROSES` → `DIPROSES` → `SIAP_DISAJIKAN` → `SELESAI`) melalui `PATCH /api/orders/:id/status`.

### 5. Manajemen Pembayaran (`PaymentsScreen`)
- Tab **Menunggu Bayar** (`GET /api/payments/unpaid`) dengan tombol konfirmasi lunas (`POST /api/payments/confirm`) untuk pembayaran Tunai maupun verifikasi manual QRIS.
- Tab **Riwayat Lunas** (`GET /api/payments/history`) menampilkan transaksi terverifikasi beserta metode dan verifikatornya.
- Mencegah konfirmasi pembayaran ganda pada sisi backend.

### 6. Struk Digital, Cetak & Ekspor PDF (`ReceiptDialog`)
- Mengambil data struk resmi dari `GET /api/orders/:id/receipt`.
- Menampilkan identitas resmi *Ayam Goreng Lengkuas Aroma*, nomor pesanan, waktu, tipe pesanan, metode & status pembayaran, rincian item beserta catatan, serta total pembayaran.
- Mendukung cetak melalui Android `PrintManager` dan penyimpanan file PDF ke direktori `Downloads`.

### 7. Manajemen Produk (`ProductsScreen`)
- Filter produk per kategori, tambah menu baru, edit menu, hapus menu, dan toggle ketersediaan (`PATCH /api/products/:id/availability`).
- Unggah foto menu langsung dari galeri perangkat (`POST /api/products/upload` ke Cloudinary atau fallback lokal).
- Mode **Urutkan** (*custom sorting*) menggunakan tombol panah naik/turun yang disimpan secara transaksional via `PATCH /api/products/reorder` dan langsung terefleksi di Customer Web.

### 8. Manajemen Kategori (`CategoriesScreen`)
- Menampilkan daftar kategori beserta jumlah produk per kategori.
- Tambah, edit, dan hapus kategori menu.

### 9. Manajemen Meja & QR Code (`TablesScreen`)
- Menampilkan daftar meja beserta status ketersediaannya.
- Tambah meja baru, hapus meja, dan lihat tautan URL QR pemesanan pelanggan untuk setiap meja (endpoint edit meja juga tersedia pada backend API).

### 10. Pengeluaran Operasional (`ExpensesScreen`)
- Menampilkan total pengeluaran tercatat dan daftar catatan biaya.
- Tambah catatan pengeluaran baru (keterangan, nominal, kategori) dan hapus catatan pengeluaran (endpoint edit pengeluaran juga tersedia pada backend API).

### 11. Laporan Keuangan & Surplus (`ReportsScreen`)
- Toggle periode **Harian** dan **Bulanan** (`GET /api/reports/financial?period=daily|monthly`).
- Menampilkan **Surplus Operasional**, **Total Omzet Lunas** beserta jumlah transaksi, **Total Pengeluaran** beserta rincian pengeluaran periode berjalan.
- **Definisi:** `Surplus Operasional = Total Omzet Lunas − Total Pengeluaran Tercatat` (bukan laporan laba rugi akuntansi penuh).

### 12. Notifikasi Pesanan & Pusat Notifikasi (`NotificationsScreen`, `OrderMonitorService`)
- Pemantauan pesanan baru di latar depan/aplikasi melalui `OrderMonitorService` & notifikasi sistem Android (`OrderNotifier`).
- Pusat notifikasi in-app (`NotificationsScreen`) untuk pesanan baru, pembayaran menunggu konfirmasi, dan pesanan siap disajikan, lengkap dengan badge jumlah belum dibaca di dashboard dan bottom navigation bar.

---

## C. BACKEND & DATABASE (`backend`)

Entitas relasional pada Prisma Schema (`backend/prisma/schema.prisma`):
- `User` (`ADMIN`, `CASHIER`)
- `Category`
- `Product` (termasuk `isAvailable` dan `sortOrder`)
- `Table` (`tableNumber`, `qrCode`, `isAvailable`)
- `Order` (`orderType`, `paymentMethod`, `paymentStatus`, `orderStatus`, `subtotal`, `total`)
- `OrderDetail`
- `Payment`
- `Expense`

---

## D. ATURAN BISNIS UTAMA

1. Setiap tautan QR mengidentifikasi nomor meja spesifik (`?table=...`).
2. Pesanan *Dine In* wajib memiliki meja; pesanan *Take Away* tidak memerlukan meja.
3. Pesanan belum dibayar tidak masuk antrean proses dapur.
4. Konfirmasi pembayaran hanya dicatat satu kali per pesanan.
5. Hanya pesanan dengan status pembayaran `PAID` yang dihitung sebagai omzet.
6. Pesanan `DIBATALKAN` tidak dihitung sebagai omzet.
7. Perubahan ketersediaan, harga, foto, dan urutan produk langsung tersinkronisasi antara Android POS dan Customer Web.
8. Pesanan pelanggan dan pesanan manual kasir dikelola pada sistem antrean pesanan yang sama.
9. Data struk dihasilkan dari data transaksi aktual di database.
