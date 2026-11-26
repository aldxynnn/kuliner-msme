# Panduan Instalasi dan Pengoperasian Lokal — AROMA POS

Panduan lengkap menjalankan **AROMA POS** (*PostgreSQL*, *Backend NestJS*, *Customer Web*, dan *Admin Android POS*) pada lingkungan pengembangan lokal.

---

## Daftar Isi
1. [Prasyarat Sistem](#1-prasyarat-sistem)
2. [Opsi 1: Menjalankan Stack dengan Docker Compose](#2-opsi-1-menjalankan-stack-dengan-docker-compose)
3. [Opsi 2: Menjalankan Backend & Customer Web Secara Manual](#3-opsi-2-menjalankan-backend--customer-web-secara-manual)
4. [Inisialisasi Seed Data](#4-inisialisasi-seed-data)
5. [Menjalankan & Menghubungkan Aplikasi Admin Android POS](#5-menjalankan--menghubungkan-aplikasi-admin-android-pos)
6. [Akses Database (pgAdmin / Prisma Studio)](#6-akses-database-pgadmin--prisma-studio)
7. [Pemeliharaan & Troubleshooting](#7-pemeliharaan--troubleshooting)

---

## 1. Prasyarat Sistem

- **Git** (versi 2.30+)
- **Docker Desktop** (jika menggunakan opsi Docker Compose)
- **Node.js v20+** & **npm** (jika menjalankan `backend` dan `customer-web` secara langsung)
- **Android Studio** + **JDK 17/21** & **Android SDK 34** (untuk membangun dan menjalankan `admin-android`)

---

## 2. Opsi 1: Menjalankan Stack dengan Docker Compose

Docker Compose menjalankan tiga layanan sekaligus: `kuliner-postgres` (port host `5433`), `kuliner-backend` (port `3000`), dan `customer-web` (port `5173`).

```bash
# 1. Clone repository
git clone https://github.com/aldxynnn/kuliner-msme.git
cd kuliner-msme

# 2. Salin environment example
cp .env.example .env

# 3. Build dan jalankan seluruh container
docker compose up --build -d

# 4. Pastikan seluruh container aktif
docker compose ps
```

Akses lokal setelah container berjalan:
- **Customer Web:** `http://localhost:5173` (simulasi QR meja: `http://localhost:5173/?table=Meja%2001`)
- **Backend REST API:** `http://localhost:3000/api/products`

Untuk memantau log backend:
```bash
docker compose logs -f backend
```

---

## 3. Opsi 2: Menjalankan Backend & Customer Web Secara Manual

### Backend (`backend/`)
Pastikan PostgreSQL berjalan, lalu salin template environment dan sesuaikan kredensial database bila perlu:

```bash
cd backend
cp .env.example .env
npm install
npx prisma generate
npx prisma migrate deploy
npx prisma db seed
npm run build
npm run start:dev
```

> **Catatan:** Gunakan `npx prisma db seed` agar file `backend/.env` dimuat otomatis. Untuk menjalankan hasil build produksi secara lokal, gunakan `node dist/src/main.js`.

### Customer Web (`customer-web/`)
```bash
cd customer-web
cp .env.example .env
npm install
npm run dev
```

Secara default `customer-web/.env.example` mengarah ke `VITE_API_BASE_URL=http://localhost:3000/api`. Buka `http://localhost:5173` (atau simulasi QR meja `http://localhost:5173/?table=Meja%2001`).

Untuk memeriksa build produksi dan preview lokal Customer Web:
```bash
cd customer-web
npm run build
npx vite preview --host 127.0.0.1 --port 5173
```

### Menghentikan Layanan Lokal (Manual)
- Hentikan masing-masing terminal `backend` (`start:dev`) dan `customer-web` (`vite`) menggunakan `Ctrl+C`.
- Jika menggunakan instance PostgreSQL terpisah, hentikan layanan Postgres setelah selesai.

---

## 4. Inisialisasi Seed Data

Skrip seed (`backend/prisma/seed.ts`) mengisi data master awal:
- 2 akun default (`admin@kuliner.com` / `admin123` dan `kasir@kuliner.com` / `kasir123`)
- 3 kategori menu (`Makanan`, `Minuman`, `Tambahan`)
- 10 menu awal AROMA
- 10 meja (`Meja 01` – `Meja 10`)

Jalankan seed melalui Docker:
```bash
docker compose exec backend npm run prisma:seed
```

> **Catatan:** Skrip seed bersifat *idempotent* (*create-missing-only*) sehingga aman dijalankan ulang tanpa menghapus pesanan, pembayaran, atau pengeluaran yang sudah tercatat.

---

## 5. Menjalankan & Menghubungkan Aplikasi Admin Android POS

Aplikasi Android POS berada di direktori `admin-android/`.

1. Salin `admin-android/local.properties.example` menjadi `admin-android/local.properties` dan isi `sdk.dir` mengarah ke Android SDK lokal (atau langsung buka folder `admin-android/` di **Android Studio** agar `local.properties` dibuat otomatis).
2. Alamat backend dikonfigurasi otomatis via `BuildConfig.API_BASE_URL` di `admin-android/app/build.gradle.kts` (`RetrofitClient.kt` dan helper gambar di `Models.kt`):
   - **Android Emulator ke backend lokal (`debug` default):** `http://10.0.2.2:3000/api/`
   - **Perangkat Android fisik via Wi-Fi yang sama (opsional):** tambahkan `aroma.apiBaseUrl=http://<IP-LOKAL-KOMPUTER>:3000/api/` di `admin-android/local.properties`
   - **Build `release`:** `https://aroma-backend-15717.containers.snapdeploy.app/api/`
   *(Alternatif jika menggunakan perangkat fisik lewat kabel USB: jalankan `adb reverse tcp:3000 tcp:3000` dengan URL `http://127.0.0.1:3000/api/`)*
3. Jalankan aplikasi ke emulator/perangkat fisik dari Android Studio atau build APK debug melalui terminal (`admin-android/`):
   ```bash
   cd admin-android
   ./gradlew assembleDebug
   ```
   *(Di Windows PowerShell dapat menjalankan `.\gradlew.bat assembleDebug` jika `gradle-wrapper.jar` tersedia, atau `gradle assembleDebug` / Run langsung dari Android Studio menggunakan JDK 17–21.)*
4. Kredensial login default hasil seed:
   - **Admin:** `admin@kuliner.com` / `admin123`
   - **Kasir:** `kasir@kuliner.com` / `kasir123`

---

## 6. Akses Database (pgAdmin / Prisma Studio)

Parameter koneksi PostgreSQL lokal (sesuai `.env.example`):
- **Host:** `localhost`
- **Port:** `5433`
- **Database:** `kuliner_db`
- **Username:** `kuliner_user`
- **Password:** `kuliner_pass`
- **SSL Mode:** `Disable`

Atau buka Prisma Studio:
```bash
docker compose exec backend npx prisma studio
```

---

## 7. Pemeliharaan & Troubleshooting

### Menghentikan Docker Tanpa Menghapus Data
```bash
docker compose down
```
> **Perhatian:** Hindari `docker compose down -v` karena opsi `-v` menghapus volume persisten `postgres_data` dan `backend_uploads`.

### Masalah Umum
- **Port `3000`, `5173`, atau `5433` sudah dipakai:** Hentikan layanan yang menggunakan port tersebut atau ubah nilai `PORT` / `POSTGRES_PORT` pada `.env`.
- **Android tidak bisa terhubung ke backend lokal:** Pastikan server backend aktif pada port `3000`, `BuildConfig.API_BASE_URL` mengarah ke `http://10.0.2.2:3000/api/` (emulator) atau `aroma.apiBaseUrl` pada `admin-android/local.properties` diisi IP LAN komputer (HP fisik), serta lakukan Rebuild APK.
- **Foto produk tidak tampil saat lokal:** Pastikan seed data telah dijalankan dan direktori `/uploads` pada backend berisi gambar atau kredensial Cloudinary telah dikonfigurasi untuk produksi.
