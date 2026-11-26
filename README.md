# AROMA POS — Sistem Pemesanan Mandiri & Operasional Kasir Kuliner UMKM

[![NestJS](https://img.shields.io/badge/Backend-NestJS_v10-E0234E?logo=nestjs&logoColor=white)](https://nestjs.com)
[![React](https://img.shields.io/badge/Customer_Web-React_v18-61DAFB?logo=react&logoColor=black)](https://react.dev)
[![Kotlin](https://img.shields.io/badge/Android_POS-Native_Kotlin-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI_Framework-Jetpack_Compose-4285F4?logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Prisma ORM](https://img.shields.io/badge/Database_ORM-Prisma_v5-2D3748?logo=prisma&logoColor=white)](https://www.prisma.io)
[![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL_v16-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org)
[![Tailwind CSS](https://img.shields.io/badge/Styling-Tailwind_CSS_v3-06B6D4?logo=tailwindcss&logoColor=white)](https://tailwindcss.com)
[![Cloudinary](https://img.shields.io/badge/Media_Storage-Cloudinary_CDN-3448C5?logo=cloudinary&logoColor=white)](https://cloudinary.com)

**AROMA POS** adalah sistem pemesanan makanan mandiri dan manajemen operasional kasir terintegrasi untuk UMKM kuliner **AROMA – Ayam Goreng Lengkuas**. Seluruh platform terhubung ke satu **Backend REST API (NestJS + Prisma)** dan satu database **PostgreSQL** sebagai *Single Source of Truth*.

---

## 1. Ringkasan Sistem

Repository ini terdiri dari tiga komponen utama:

| Komponen | Folder | Target Pengguna | Teknologi Utama |
| :--- | :--- | :--- | :--- |
| **Customer Web** | `customer-web/` | Pelanggan (*Dine In* via QR meja & *Take Away*) | React 18, Vite 5, TypeScript, Tailwind CSS 3, Lucide React |
| **Admin Android POS** | `admin-android/` | Kasir & pengelola operasional resto | Native Kotlin, Jetpack Compose, Material 3, Retrofit 2, Coil |
| **Backend REST API** | `backend/` | Layanan data terpusat untuk Web & Android | NestJS 10, TypeScript, Prisma ORM 5, PostgreSQL 16, JWT, Multer + Cloudinary |

---

## 2. Fitur yang Diimplementasikan

### Customer Web (`customer-web`)
- **Deteksi Meja Otomatis via URL QR:** Membaca parameter `?table=Meja 01` secara otomatis saat pelanggan memindai QR meja, dengan input nomor meja manual jika pelanggan mengakses tanpa parameter QR pada mode *Dine In*.
- **Pilihan Tipe Pesanan:** *Dine In* (Makan di Tempat) dan *Take Away* (Bawa Pulang).
- **Penjelajahan Menu Responsif:** Filter kategori *sticky* (terurut mengikuti `sortOrder` dari backend), pencarian menu, kartu produk responsif (mobile-first hingga desktop), serta indikator status ketersediaan (*Tersedia* / *Habis*).
- **Detail Produk & Catatan Khusus:** Modal detail produk dengan pengaturan kuantitas dan catatan khusus per item.
- **Keranjang & Checkout:** Drawer keranjang dengan pengaturan kuantitas, penghapusan item, input nama pemesan opsional, dan pilihan metode pembayaran (**QRIS** atau **Bayar di Kasir / Tunai**).
- **Pelacakan Pesanan Live & Ringkasan Struk:** Pembaruan status otomatis setiap 5 detik (`GET /api/orders/track/:orderNumber`), tampilan tahapan progres dapur, tampilan kode QRIS untuk verifikasi kasir, serta ringkasan struk digital saat pembayaran lunas.

### Admin Android POS (`admin-android`)
- **Autentikasi Kasir/Admin:** Login berbasis JWT (`TokenManager` + `AuthInterceptor`) dan halaman **Profil & Akun** dengan konfirmasi logout.
- **Dashboard Operasional:** Ringkasan omzet lunas hari ini, jumlah transaksi lunas, pesanan selesai, pesanan aktif dapur, dan tagihan belum bayar, serta akses cepat pengelolaan resto.
- **Kasir POS Manual:** Pembuatan pesanan langsung dari kasir untuk *Dine In* (pilih meja) maupun *Take Away* dengan keranjang bottom sheet dan metode bayar Tunai/QRIS.
- **Manajemen Pesanan & Alur Dapur:** Filter status (`Belum Bayar`, `Masuk Antrean`, `Sedang Dimasak`, `Siap Disajikan`, `Selesai`, `Dibatalkan`), detail pesanan, pembaruan status dapur bertahap untuk pesanan lunas, pembatalan pesanan belum bayar, serta struk digital.
- **Konfirmasi & Riwayat Pembayaran:** Daftar pesanan belum lunas untuk verifikasi pembayaran Tunai/QRIS oleh kasir serta tab riwayat pembayaran lunas.
- **Manajemen Menu & Penataan Urutan:** Tambah/edit/hapus menu, toggle ketersediaan langsung tersinkronisasi ke Customer Web, unggah foto dari galeri HP (Cloudinary / fallback lokal), serta mode penataan urutan produk (*custom sorting* via `PATCH /api/products/reorder`).
- **Manajemen Kategori & Meja QR:** Pengelolaan kategori menu (tambah, edit, hapus) serta pengelolaan meja (tambah, hapus, lihat tautan QR meja).
- **Biaya Operasional & Laporan Surplus:** Pencatatan dan penghapusan pengeluaran operasional harian serta laporan keuangan harian dan bulanan (*Surplus Operasional = Total Omzet Lunas − Total Pengeluaran*).
- **Struk Digital, Cetak & Ekspor PDF:** Tampilan struk resmi *Ayam Goreng Lengkuas Aroma*, cetak melalui Android `PrintManager`, dan simpan PDF ke folder `Downloads`.
- **Notifikasi Pesanan Masuk & Pusat Notifikasi:** Pemantauan pesanan baru melalui `OrderMonitorService`, badge angka pada navigasi, dan halaman daftar notifikasi (`NotificationsScreen`).

### Backend REST API (`backend`)
- **REST API Modular (NestJS):** Modul `auth`, `categories`, `products`, `tables`, `orders`, `payments`, `expenses`, dan `reports`.
- **Dual-Mode Upload Gambar:** Mengunggah ke Cloudinary jika kredensial tersedia di environment, atau menyimpan ke folder lokal `/uploads` saat pengembangan lokal.
- **Keamanan & Integritas Data:** Hash kata sandi dengan `bcrypt`, proteksi endpoint dengan `JwtAuthGuard`, validasi DTO global (`ValidationPipe`), dan transaksi Prisma (`prisma.$transaction`) pada operasi kritikal.

---

## 3. Identitas Visual (Aroma POS Design System)

Customer Web dan Admin Android berbagi sistem desain yang seragam:

| Token | Kode Warna | Penggunaan |
| :--- | :--- | :--- |
| **Deep Roast Ink** | `#171311` | Teks utama, kontrol aktif, header tiket & ledger |
| **Lengkuas Vermilion (Primary)** | `#C84319` | Aksi utama, highlight brand, CTA checkout & pos |
| **Pandan Leaf Green (Success)** | `#14533C` | Tag meja aktif, status lunas, konfirmasi bayar |
| **Alabaster Paper (Canvas)** | `#FBF9F5` | Latar belakang utama halaman & layar |
| **Pure White (Surface)** | `#FFFFFF` | Kartu, panel tiket, form input, modal, drawer |
| **Warning / Error / Info** | `#B4690E` / `#B92D26` / `#1D5A7E` | Menunggu bayar, pembatalan/biaya, antrean dapur |

Detail lengkap sistem desain terdokumentasi pada [DESIGN.md](DESIGN.md).

---

## 4. Arsitektur Sistem

```mermaid
flowchart TD
    subgraph Clients["Aplikasi Klien"]
        A["Customer Web\n(React + Vite + Tailwind)"]
        B["Admin Android POS\n(Kotlin + Jetpack Compose)"]
    end

    subgraph Server["Server & Penyimpanan"]
        C["Backend REST API\n(NestJS + Prisma ORM)"]
        D["PostgreSQL 16\n(Neon / Docker Lokal)"]
        E["Media Storage\n(Cloudinary CDN / Local /uploads)"]
    end

    A -->|REST API| C
    B -->|Retrofit 2 + JWT| C
    C -->|Prisma Client| D
    C -->|Upload Foto Menu| E
    A -.->|Tampil Foto Menu| E
    B -.->|Tampil Foto Menu| E
```

Penjelasan arsitektur dan skema relasional database tersedia pada [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

---

## 5. Struktur Repository

```text
kuliner-msme/
├── admin-android/                       # Aplikasi Native Android POS (Kotlin + Compose + Material 3)
│   ├── app/src/main/java/com/kuliner/msme/admin/
│   │   ├── MainActivity.kt              # Entry point & penanganan sesi login
│   │   ├── data/
│   │   │   ├── api/                     # ApiService, RetrofitClient, AuthInterceptor, TokenManager
│   │   │   └── model/                   # Data models & helper URL gambar/QR
│   │   ├── notification/                # OrderMonitorService, NotificationCenter, OrderNotifier, dll.
│   │   └── ui/
│   │       ├── MainScreen.kt            # Shell navigasi utama & bottom navigation bar
│   │       ├── components/              # AromaComponents, NotificationComponents
│   │       ├── screens/                 # Login, Dashboard, Orders, CashierOrder, Payments,
│   │       │                            # Products, Categories, Tables, Expenses, Reports,
│   │       │                            # ReceiptDialog, Notifications, Profile
│   │       ├── theme/                   # Color.kt, Type.kt, Theme.kt (Aroma POS Design System)
│   │       └── util/                    # UserDisplay.kt
│   ├── local.properties.example         # Template sdk.dir & aroma.apiBaseUrl lokal
│   └── app/build.gradle.kts             # BuildConfig API_BASE_URL (debug: 10.0.2.2:3000)
│
├── backend/                             # Backend REST API (NestJS + Prisma ORM)
│   ├── prisma/                          # schema.prisma, migrations/, seed.ts
│   ├── src/
│   │   ├── auth/                        # Login JWT, guard, decorator
│   │   ├── categories/                  # CRUD kategori menu
│   │   ├── products/                    # CRUD produk, upload foto, toggle stok, reorder
│   │   ├── tables/                      # CRUD meja & generator tautan QR
│   │   ├── orders/                      # Order pelanggan, kasir, tracking, status, cancel, struk
│   │   ├── payments/                    # Konfirmasi bayar, daftar belum lunas, riwayat bayar
│   │   ├── expenses/                    # CRUD pengeluaran operasional
│   │   ├── reports/                     # Ringkasan dashboard & laporan surplus harian/bulanan
│   │   └── prisma/                      # PrismaService & PrismaModule
│   ├── .env.example                     # Template env backend lokal
│   ├── Dockerfile
│   └── package.json
│
├── customer-web/                        # Customer Ordering Website (React + Vite + Tailwind)
│   ├── public/logo-aroma.png
│   ├── src/
│   │   ├── components/                  # Header, HeroSection, CategoryBar, ProductCard,
│   │   │                                # ProductDetailModal, CartDrawer, OrderTrackingModal
│   │   ├── utils/format.ts              # Format Rupiah & tanggal Indonesia
│   │   ├── api.ts                       # REST API client & resolver getImageUrl()
│   │   ├── types.ts                     # TypeScript interfaces
│   │   ├── App.tsx                      # Halaman pemesanan utama
│   │   └── index.css                    # Design tokens & animasi halus
│   ├── .env.example                     # Template VITE_API_BASE_URL lokal
│   ├── Dockerfile
│   ├── tailwind.config.js
│   └── package.json
│
├── docs/
│   ├── ARCHITECTURE.md                  # Arsitektur sistem & ERD database
│   └── INSTALLATION.md                  # Panduan instalasi lokal (Docker, manual, Android Studio)
├── .env.example                         # Contoh konfigurasi environment lokal
├── docker-compose.yml                   # Orkestrasi PostgreSQL, Backend, dan Customer Web
├── PROJECT.md                           # Ringkasan proyek & tech stack
├── FEATURES.md                          # Spesifikasi fitur aktual & aturan bisnis
├── DESIGN.md                            # Panduan Design System Aroma POS
└── README.md                            # Dokumentasi utama repository
```

---

## 6. Menjalankan Proyek Secara Lokal

### Opsi A: Docker Compose (Disarankan untuk Stack Penuh)

```bash
# 1. Clone repository
git clone https://github.com/aldxynnn/kuliner-msme.git
cd kuliner-msme

# 2. Salin konfigurasi environment
cp .env.example .env

# 3. Build dan jalankan PostgreSQL, Backend, serta Customer Web
docker compose up --build -d

# 4. Jalankan seed data awal (idempotent)
docker compose exec backend npm run prisma:seed
```

Akses layanan lokal:
- **Customer Web:** `http://localhost:5173` (uji QR meja: `http://localhost:5173/?table=Meja%2001`)
- **Backend API:** `http://localhost:3000/api/products`

### Opsi B: Menjalankan Customer Web & Backend Tanpa Docker

```bash
# 1. Backend (pastikan PostgreSQL berjalan & salin template .env)
cd backend
cp .env.example .env    # sesuaikan DATABASE_URL / DIRECT_URL jika perlu
npm install
npx prisma generate
npx prisma migrate deploy
npx prisma db seed      # memuat .env otomatis & mengisi data awal secara idempotent
npm run build
npm run start:dev       # atau: node dist/src/main.js

# 2. Customer Web (terminal terpisah)
cd customer-web
cp .env.example .env    # VITE_API_BASE_URL=http://localhost:3000/api
npm install
npm run dev             # akses di http://localhost:5173
```

### Menjalankan Admin Android (`admin-android`)
1. Salin `admin-android/local.properties.example` menjadi `admin-android/local.properties` lalu isi `sdk.dir` (atau biarkan Android Studio membuatnya otomatis saat membuka folder `admin-android/`).
2. `BuildConfig.API_BASE_URL` di `admin-android/app/build.gradle.kts` secara otomatis dikonfigurasi per build type:
   - **Debug (default Emulator ke backend lokal):** `http://10.0.2.2:3000/api/`
   - **Override lokal (opsional di `admin-android/local.properties`):** tambahkan `aroma.apiBaseUrl=http://<IP-LOKAL-KOMPUTER>:3000/api/` untuk perangkat fisik pada Wi-Fi yang sama
   - **Release:** `https://aroma-backend-15717.containers.snapdeploy.app/api/`
3. Jalankan aplikasi ke Emulator/HP atau build APK debug, lalu login dengan akun seed awal:
   - Admin: `admin@kuliner.com` / `admin123`
   - Kasir: `kasir@kuliner.com` / `kasir123`

Panduan instalasi lengkap, perintah menghentikan layanan, akses Prisma Studio, dan troubleshooting tersedia di [docs/INSTALLATION.md](docs/INSTALLATION.md).

---

## 7. Konfigurasi Environment

File `.env.example` menyediakan variabel dasar pengembangan lokal:

```env
# Database Configuration (Docker Compose / PostgreSQL)
POSTGRES_USER=kuliner_user
POSTGRES_PASSWORD=kuliner_pass
POSTGRES_DB=kuliner_db
POSTGRES_PORT=5433
DATABASE_URL="postgresql://kuliner_user:kuliner_pass@localhost:5433/kuliner_db?schema=public"
DIRECT_URL="postgresql://kuliner_user:kuliner_pass@localhost:5433/kuliner_db?schema=public"

# Backend Configuration
PORT=3000
JWT_SECRET=kuliner_msme_jwt_secret_dev_key_2025
FRONTEND_URL=http://localhost:5173

# Frontend Configuration (Build-time Vite API URL)
VITE_API_BASE_URL=http://localhost:3000/api
```

Untuk lingkungan produksi dengan upload gambar cloud, tambahkan variabel opsional berikut pada environment backend (jangan pernah commit kredensial asli ke Git):

```env
CLOUDINARY_CLOUD_NAME=<nama_cloud>
CLOUDINARY_API_KEY=<api_key>
CLOUDINARY_API_SECRET=<api_secret>
```

---

## 8. Ringkasan Endpoint API (`/api`)

| Modul | Method & Path | Autentikasi | Deskripsi |
| :--- | :--- | :--- | :--- |
| **Auth** | `POST /api/auth/login`, `GET /api/auth/me` | Publik / JWT | Login kasir/admin & profil pengguna aktif |
| **Categories** | `GET /api/categories`, `GET /api/categories/:id`, `POST /api/categories`, `PUT /api/categories/:id`, `DELETE /api/categories/:id` | GET Publik, Mutasi JWT | Pengelolaan kategori menu |
| **Products** | `GET /api/products`, `GET /api/products/:id`, `POST /api/products`, `PUT /api/products/:id`, `PATCH /api/products/:id/availability`, `PATCH /api/products/reorder`, `POST /api/products/upload`, `DELETE /api/products/:id` | GET Publik, Mutasi JWT | Pengelolaan katalog menu, foto, stok, dan urutan |
| **Tables** | `GET /api/tables`, `GET /api/tables/by-number/:tableNumber`, `GET /api/tables/:id`, `POST /api/tables`, `PUT /api/tables/:id`, `DELETE /api/tables/:id` | GET Publik, Mutasi JWT | Pengelolaan meja & URL QR pemesanan |
| **Orders** | `POST /api/orders`, `POST /api/orders/cashier`, `GET /api/orders`, `GET /api/orders/track/:orderNumber`, `GET /api/orders/:id`, `GET /api/orders/:id/receipt`, `PATCH /api/orders/:id/status`, `POST /api/orders/:id/cancel` | Pelanggan Publik, Kasir/Admin JWT | Pemesanan, tracking live, proses dapur, pembatalan, dan data struk |
| **Payments** | `POST /api/payments/confirm`, `GET /api/payments/unpaid`, `GET /api/payments/history` | JWT | Konfirmasi pembayaran lunas & riwayat pembayaran |
| **Expenses** | `GET /api/expenses`, `GET /api/expenses/:id`, `POST /api/expenses`, `PUT /api/expenses/:id`, `DELETE /api/expenses/:id` | JWT | Pencatatan dan pengelolaan biaya operasional |
| **Reports** | `GET /api/reports/dashboard`, `GET /api/reports/financial?period=daily\|monthly` | JWT | Ringkasan dashboard & laporan surplus operasional |

---

## 9. Deployment Produksi

| Komponen | Platform | Endpoint / URL |
| :--- | :--- | :--- |
| **Customer Web** | Vercel | `https://aroma-ayam-goreng-lengkuas.vercel.app` |
| **Backend REST API** | SnapDeploy Container | `https://aroma-backend-15717.containers.snapdeploy.app/api` |
| **Database** | Neon PostgreSQL | Serverless PostgreSQL (Pooled + Direct URL) |
| **Media Storage** | Cloudinary CDN | Penyimpanan foto produk produksi |

---

## 10. Dokumentasi Terkait

- [PROJECT.md](PROJECT.md) — Tujuan proyek, stack teknologi aktual, dan ruang lingkup.
- [FEATURES.md](FEATURES.md) — Daftar fitur terimplementasi & aturan bisnis.
- [DESIGN.md](DESIGN.md) — Sistem desain visual Aroma POS untuk Web & Android.
- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) — Diagram arsitektur, alur data, dan ERD database.
- [docs/INSTALLATION.md](docs/INSTALLATION.md) — Panduan setup lokal, database, dan Android Studio.
