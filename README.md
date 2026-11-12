# AROMA – Ayam Goreng Lengkuas
*Sistem Pemesanan dan Manajemen Kuliner UMKM Terintegrasi (Customer Website, Android POS, dan Backend NestJS)*

---

## 📋 Daftar Isi
1. [Prasyarat Sistem](#prasyarat-sistem)
2. [Menjalankan Proyek dengan Docker Compose](#menjalankan-proyek-dengan-docker-compose)
3. [Manajemen Database & Seed Data](#manajemen-database--seed-data)
4. [Menghubungkan Aplikasi Android POS](#menghubungkan-aplikasi-android-pos)
5. [Akses Database via pgAdmin / Database Tool](#akses-database-via-pgadmin--database-tool)
6. [Panduan Penghentian & Pemeliharaan](#panduan-penghentian--pemeliharaan)

---

## 1. Prasyarat Sistem
Sebelum menjalankan proyek ini di komputer lokal Anda, pastikan telah menginstal:
- **Git**
- **Docker Desktop** (pastikan daemon Docker sudah aktif dan berjalan)
- **Android Studio** (jika ingin menjalankan aplikasi Android POS di emulator atau perangkat fisik)

---

## 2. Menjalankan Proyek dengan Docker Compose

Ikuti langkah-langkah berikut untuk menjalankan seluruh stack (*PostgreSQL*, *Backend NestJS*, dan *Customer Website*) secara lokal:

### Langkah 1: Clone Repository
```bash
git clone https://github.com/aldxynnn/kuliner-msme.git
cd kuliner-msme
```

### Langkah 2: Konfigurasi Environment (`.env`)
Salin file `.env.example` menjadi `.env` di root folder proyek:
```bash
cp .env.example .env
```
*(Catatan: Anda dapat menyesuaikan port atau konfigurasi di dalam file `.env` jika diperlukan).*

### Langkah 3: Build & Jalankan Container
Jalankan perintah berikut di root proyek untuk mem-build dan menjalankan seluruh layanan di latar belakang:
```bash
docker compose up --build -d
```

### Langkah 4: Memeriksa Status Layanan
Pastikan seluruh container berjalan dengan sehat:
```bash
docker compose ps
```
Anda akan melihat tiga container aktif:
- `kuliner-postgres` (Port `5433:5432`, berstatus *Healthy*)
- `kuliner-backend` (Port `3000:3000`)
- `customer-web` (Port `5173:80`)

### Langkah 5: Memeriksa Log Backend (Opsional)
Untuk melihat log atau status startup backend NestJS:
```bash
docker compose logs -f backend
```

### Langkah 6: Mengakses Aplikasi
- **Website Pelanggan:** Buka browser dan akses [http://localhost:5173](http://localhost:5173) (atau dengan parameter meja: `http://localhost:5173/?table=Meja%2001`).
- **Backend REST API:** Akses [http://localhost:3000/api/products](http://localhost:3000/api/products).

---

## 3. Manajemen Database & Seed Data

Setiap anggota kelompok memiliki database PostgreSQL lokal masing-masing di dalam container Docker. Untuk mengisi data master awal (akun admin, kasir, kategori, produk menu, dan meja 01-10) tanpa menghapus data transaksi:

Jalankan perintah *seed* secara manual:
```bash
docker compose exec backend npm run prisma:seed
```
*Aturan Seed:* Skrip seed bersifat *idempotent* (aman dijalankan berkali-kali tanpa menggandakan data dan tidak menghapus data transaksi/pesanan yang sudah ada).

---

## 4. Menghubungkan Aplikasi Android POS

Aplikasi Android POS dijalankan secara lokal melalui **Android Studio**, bukan melalui Docker Compose.

1. Buka folder `admin-android/` di Android Studio.
2. Sinkronkan proyek dengan Gradle (*Gradle Sync*).
3. **Konfigurasi Alamat API:**
   - **Android Emulator:** Secara default mengarah ke `http://10.0.2.2:3000/api/` (alias lokal komputer host).
   - **HP Android Fisik:** Hubungkan HP dan komputer ke jaringan Wi-Fi yang sama, lalu sesuaikan base URL di `admin-android/.../data/api/RetrofitClient.kt` menggunakan IP lokal komputer Anda (misal: `http://192.168.1.50:3000/api/`).
4. Jalankan aplikasi (*Run*) ke emulator atau perangkat fisik.
5. Login menggunakan kredensial default:
   - **Admin:** `admin@kuliner.com` / `admin123`
   - **Kasir:** `kasir@kuliner.com` / `kasir123`

---

## 5. Akses Database via pgAdmin / Database Tool
Jika Anda ingin melihat isi database secara visual menggunakan pgAdmin, DBeaver, atau Prisma Studio:
- **Host:** `localhost`
- **Port:** `5433` (atau sesuai `POSTGRES_PORT` di `.env`)
- **Database:** `kuliner_db`
- **Username:** `kuliner_user`
- **Password:** `kuliner_pass`

Atau jalankan Prisma Studio melalui container backend:
```bash
docker compose exec backend npx prisma studio
```

---

## 6. Panduan Penghentian & Pemeliharaan

### Menghentikan Container (Tanpa Menghapus Data)
Untuk menghentikan layanan tanpa merusak database atau data yang tersimpan:
```bash
docker compose down
```
*(Jangan pernah menjalankan `docker compose down -v` karena argumen `-v` akan menghapus volume database dan seluruh data lokal Anda).*

### Menyalakan Kembali
```bash
docker compose up -d
```
