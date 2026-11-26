# PROJECT OVERVIEW — AROMA POS

## Product Name
**AROMA POS** (*Ayam Goreng Lengkuas Aroma* — Sistem Pemesanan & Manajemen Kuliner UMKM)

## Project Description
AROMA POS adalah sistem pemesanan makanan mandiri dan manajemen operasional kasir yang dirancang khusus untuk bisnis kuliner UMKM. Sistem ini menyatukan tiga komponen dalam satu ekosistem data terpusat:

1. **Customer Web (`customer-web/`):** Website pemesanan mandiri untuk pelanggan melalui QR meja (*Dine In*) maupun pesan bawa pulang (*Take Away*).
2. **Admin Android POS (`admin-android/`):** Aplikasi kasir dan pengelolaan usaha native Android untuk operasional harian pemilik dan kasir.
3. **Backend REST API & Database (`backend/`):** Layanan API terpusat dengan PostgreSQL sebagai *Single Source of Truth*.

## Technology Stack

### Customer Website (`customer-web`)
- React 18
- Vite 5
- TypeScript 5
- Tailwind CSS 3
- Lucide React Icons
- `clsx` & `tailwind-merge`

### Android Application (`admin-android`)
- Native Android (Min SDK 24 / Target SDK 34)
- Kotlin
- Jetpack Compose & Material 3
- Material Icons Extended
- Retrofit 2 & OkHttp 4 (`AuthInterceptor`, `HttpLoggingInterceptor`)
- Coil Compose (image loading)
- Kotlin Coroutines

### Backend (`backend`)
- Node.js & NestJS 10
- TypeScript 5
- REST API dengan validasi DTO (`class-validator`, `class-transformer`)
- Prisma ORM 5
- Passport JWT & `bcrypt`
- Multer & Cloudinary SDK (dengan fallback storage lokal `/uploads`)

### Database
- PostgreSQL 16 (Docker lokal & Neon PostgreSQL untuk produksi)

### Architecture
- Client-server RESTful API architecture
- Single Source of Truth pada PostgreSQL
- Konfigurasi berbasis environment variable (`.env`)
- Containerized local stack via `docker-compose.yml`

## Main Objectives
- Memudahkan pelanggan memesan makanan melalui QR meja tanpa antre di kasir.
- Membantu kasir membuat pesanan manual, memverifikasi pembayaran Tunai/QRIS, memantau antrean dapur, dan mencetak/menyimpan PDF struk.
- Membantu pengelola mengatur katalog menu, foto produk, urutan tampil menu, kategori, dan meja QR secara real-time.
- Menyediakan pencatatan biaya operasional serta laporan surplus harian dan bulanan yang jelas.

## Language and Localization
- **Bahasa antarmuka:** Bahasa Indonesia
- **Mata uang:** Rupiah Indonesia (`Rp` / `IDR`)
- **Zona waktu:** `Asia/Jakarta` (WIB)
- **Identitas usaha pada struk:** Ayam Goreng Lengkuas Aroma — Ruko Puri Cempaka Blok R1 No. 10, Panancangan, Cipocok Jaya, Kota Serang, Banten 42124
