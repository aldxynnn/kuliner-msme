# Panduan Instalasi dan Pengoperasian Lokal (Local Setup & Development Guide)
**Proyek:** AROMA POS – Sistem Pemesanan dan Manajemen Kuliner UMKM Terintegrasi

---

## 📋 Daftar Isi
1. [Prasyarat Sistem](#1-prasyarat-sistem)
2. [Langkah Instalasi Menggunakan Docker Compose](#2-langkah-instalasi-menggunakan-docker-compose)
3. [Manajemen Database & Inisialisasi Seed Data](#3-manajemen-database--inisialisasi-seed-data)
4. [Menghubungkan Aplikasi Android POS](#4-menghubungkan-aplikasi-android-pos)
5. [Akses Database via pgAdmin / Database Tool](#5-akses-database-via-pgadmin--database-tool)
6. [Panduan Penghentian & Pemeliharaan](#6-panduan-penghentian--pemeliharaan)
7. [Troubleshooting Umum](#7-troubleshooting-umum)

---

## 1. Prasyarat Sistem
Sebelum menjalankan proyek ini di lingkungan lokal Anda, pastikan perangkat telah terpasang:
- **Git** (versi 2.30+)
- **Docker Desktop** (pastikan daemon Docker Engine dalam kondisi aktif dan berjalan)
- **Node.js** (v18+ atau v20+) — *Opsional, jika ingin menguji skrip tanpa container*
- **Android Studio** (Hedgehog 2023.1.1+ atau versi terbaru untuk menjalankan aplikasi Android POS di emulator/HP fisik)

---

## 2. Langkah Instalasi Menggunakan Docker Compose

Sistem AROMA POS telah dirancang agar seluruh *stack* (*PostgreSQL 16*, *Backend NestJS REST API*, dan *Customer Website*) dapat dijalankan secara instan menggunakan satu perintah Docker Compose.

### Langkah 1: Clone Repository
Buka terminal / PowerShell, lalu jalankan:
```bash
git clone https://github.com/aldxynnn/kuliner-msme.git
cd kuliner-msme
```

### Langkah 2: Konfigurasi Environment (`.env`)
Salin file `.env.example` menjadi `.env` pada folder utama proyek:
```bash
cp .env.example .env
```
*(File `.env` sudah dikonfigurasi dengan nilai default yang aman untuk lingkungan pengembangan lokal).*

### Langkah 3: Build & Jalankan Container
Eksekusi perintah berikut untuk mem-build image Docker dan menyalakan seluruh container di latar belakang:
```bash
docker compose up --build -d
```

### Langkah 4: Memeriksa Status Layanan Container
Pastikan ketiga container utama telah berjalan dengan status sehat (*Healthy/Up*):
```bash
docker compose ps
```
Layanan yang aktif meliputi:
- `kuliner-postgres` (Port Host `5433` -> Port Container `5432`, status *Healthy*)
- `kuliner-backend` (Port Host `3000` -> Port Container `3000`)
- `customer-web` (Port Host `5173` -> Port Container `80`)

### Langkah 5: Memeriksa Log Server Backend
Untuk melihat log keluaran atau proses migrasi otomatis NestJS:
```bash
docker compose logs -f backend
```

### Langkah 6: Mengakses Antarmuka Aplikasi
- **Website Pelanggan (Customer Web):** Buka browser dan akses [http://localhost:5173](http://localhost:5173) (atau dengan parameter meja otomatis: [http://localhost:5173/?table=Meja%2001](http://localhost:5173/?table=Meja%2001)).
- **Backend REST API:** Akses [http://localhost:3000/api/products](http://localhost:3000/api/products).

---

## 3. Manajemen Database & Inisialisasi Seed Data

Setiap anggota tim pengembang memiliki database PostgreSQL lokal masing-masing di dalam container Docker. Saat pertama kali menjalankan proyek, jalankan skrip *seed* manual untuk mengisi data master awal (akun admin, kasir, 3 kategori menu resmi, 10 produk menu AROMA, dan Meja 01–10):

Jalankan perintah *seed* melalui container backend:
```bash
docker compose exec backend npm run prisma:seed
```

> 💡 **Aturan Keamanan Seed:** Skrip seed AROMA bersifat *idempotent* (aman dijalankan berulang kali). Skrip hanya akan memasukkan data master yang belum ada tanpa menghapus atau menimpa data transaksi, pesanan, maupun pencatatan pengeluaran yang sudah dibuat oleh pengguna.

---

## 4. Menghubungkan Aplikasi Android POS

Aplikasi Android POS dikembangkan menggunakan **Native Kotlin + Jetpack Compose** dan dijalankan secara terpisah melalui **Android Studio**, bukan melalui Docker Compose.

### Langkah-langkah:
1. Buka folder `admin-android/` menggunakan Android Studio.
2. Tunggu proses *Gradle Sync* selesai.
3. **Pengaturan Alamat Base URL API (`RetrofitClient.kt`):**
   - **Android Emulator (Default):** Menggunakan rute alias emulator `http://10.0.2.2:3000/api/` untuk terhubung ke backend lokal di komputer host.
   - **HP Android Fisik:** Hubungkan HP fisik dan komputer ke jaringan Wi-Fi / LAN yang sama. Ubah `DEFAULT_BASE_URL` di `admin-android/app/src/main/java/com/kuliner/msme/admin/data/api/RetrofitClient.kt` menggunakan IP lokal komputer Anda (contoh: `http://192.168.1.50:3000/api/`).
   - **Mode Rilis (Production):** Menggunakan endpoint produksi `https://aroma-backend-15717.containers.snapdeploy.app/api/`.
4. Jalankan aplikasi (*Run* `Shift + F10`) ke emulator atau HP fisik.
5. **Kredensial Default Login Kasir/Admin:**
   - **Admin / Owner:** `admin@kuliner.com` / `admin123`
   - **Kasir:** `kasir@kuliner.com` / `kasir123`

---

## 5. Akses Database via pgAdmin / Database Tool
Untuk melihat dan mengelola isi tabel PostgreSQL secara visual menggunakan pgAdmin, DBeaver, TablePlus, atau VS Code Database Client:
- **Host:** `localhost`
- **Port:** `5433` (sesuai `POSTGRES_PORT` pada `.env`)
- **Database Name:** `kuliner_db`
- **Username:** `kuliner_user`
- **Password:** `kuliner_pass`
- **SSL Mode:** `Disable` (untuk lokal)

Atau buka antarmuka Prisma Studio secara visual melalui terminal backend:
```bash
docker compose exec backend npx prisma studio
```

---

## 6. Panduan Penghentian & Pemeliharaan

### Menghentikan Layanan (Tanpa Menghapus Data)
Untuk menghentikan container tanpa merusak atau menghapus isi database lokal:
```bash
docker compose down
```

> ⚠️ **PERINGATAN:** Jangan pernah memanggil perintah `docker compose down -v`. Opsi `-v` akan menghapus seluruh Docker Volume persisten (`postgres_data` dan `backend_uploads`), yang mengakibatkan data database dan foto yang diunggah terhapus secara permanen.

### Menyalakan Kembali Layanan
```bash
docker compose up -d
```

---

## 7. Troubleshooting Umum

### A. Port 3000, 5173, atau 5433 Sudah Digunakan
Jika muncul pesan error `ports are not available` saat menjalankan `docker compose up`:
- Hentikan proses Node.js / Vite / PostgreSQL lain yang berjalan di komputer lokal.
- Atau ubah alokasi port host di file `.env` (misal `PORT=3001` atau `POSTGRES_PORT=5434`).

### B. Android Emulator Gagal Login / Network Error
- Pastikan backend container `kuliner-backend` sedang menyala (`docker compose ps`).
- Pastikan URL di `RetrofitClient.kt` menggunakan `http://10.0.2.2:3000/api/`.
- Periksa koneksi internet komputer host.

### C. Gambar Produk Tidak Tampil / Error 404
- Pastikan skrip seed telah dijalankan (`docker compose exec backend npm run prisma:seed`).
- Verifikasi bahwa direktori persisten `/app/uploads` telah di-mount dengan benar di Docker Compose.
