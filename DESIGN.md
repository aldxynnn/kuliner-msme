# UI/UX DESIGN SYSTEM — AROMA POS

Panduan sistem desain resmi **AROMA POS** (*Ayam Goreng Lengkuas Aroma*) yang diterapkan secara konsisten pada **Customer Web** (`customer-web`) dan **Admin Android POS** (`admin-android`).

---

## 1. Arah Visual & Kepribadian Brand

- **Modern & Matang:** Tampilan setara produk POS komersial siap pakai, bersih, dan terstruktur.
- **Warm Culinary Character:** Nuansa hangat rempah Nusantara yang menggugah selera tanpa dekorasi berlebihan.
- **Clarity First:** Hierarki informasi jelas, status pesanan & pembayaran mudah dibedakan dalam sekilas pandang, serta nyaman digunakan dalam operasional kasir maupun pemesanan pelanggan lewat ponsel.

---

## 2. Palet Warna Resmi Aroma POS

### Warna Inti (Core Brand Tokens)

| Token | Hex | Web (Tailwind) | Android (Compose) | Fungsi |
| :--- | :--- | :--- | :--- | :--- |
| **Terracotta** | `#A9472D` | `brand-500` | `AromaTerracotta` | Warna primer, tombol CTA utama, state aktif |
| **Terracotta Dark** | `#923B24` / `#8C3923` | `brand-600` | `AromaTerracottaDark` | State hover/pressed & aksen teks kontras |
| **Terracotta Soft** | `#FBF2EE` | `brand-50` | `AromaTerracottaSoft` | Latar chip aktif, container ikon, highlight halus |
| **Terracotta Border** | `#EAC0AF` | `brand-200` | `AromaTerracottaBorder` | Border elemen terpilih & badge brand |
| **Warm Cream** | `#F6F0E6` | `cream-100` | `AromaCream` | Kanvas utama halaman/layar |
| **Cream Soft** | `#FAF7F2` | `cream-50` | `AromaCreamSoft` | Latar inset input, kotak ringkasan, footer |
| **Warm Border** | `#E8DFD1` / `#E4DACB` | `cream-200` | `AromaBorder` | Hairline border kartu, pembatas, track control |
| **White Surface** | `#FFFFFF` | `white` | `AromaSurface` | Permukaan kartu, modal, drawer, top/bottom bar |
| **Charcoal** | `#292722` | `charcoal` | `AromaCharcoal` | Teks utama, heading, floating cart dock |
| **Charcoal Secondary** | `#5C574F` | `charcoal-secondary` | `AromaTextSecondary` | Teks deskripsi & label sekunder |
| **Charcoal Muted** | `#7B746A` / `#827B71` | `charcoal-muted` | `AromaTextMuted` | Caption, timestamp, placeholder |

### Warna Semantik (Status & Feedback)

| Status | Teks / Aksen | Latar Halus | Border | Penggunaan |
| :--- | :--- | :--- | :--- | :--- |
| **Success** | `#1F7A4C` | `#E8F6EE` | `#BDE5CE` | Pembayaran `LUNAS`, `Siap Disajikan`, omzet, toast sukses |
| **Warning** | `#B76E14` | `#FDF4E3` | `#F2D6A2` | `Belum Bayar` / `MENUNGGU_PEMBAYARAN`, status live pesanan |
| **Error** | `#C4382B` | `#FCECEB` | `#F3BEB9` | `DIBATALKAN`, pengeluaran, validasi error, tombol hapus/keluar |
| **Info** | `#25638A` | `#EAF3F9` | `#BBD8EA` | `Masuk Antrean` (`MENUNGGU_DIPROSES`) |

---

## 3. Tipografi

- **Customer Web:** `Plus Jakarta Sans` (heading, label, tombol, harga) dan `Inter` (deskripsi & teks panjang), dengan `tabular-nums` untuk nominal harga dan kuantitas.
- **Admin Android:** Skala tipografi Material 3 terdefinisi pada `ui/theme/Type.kt` (`headlineMedium` 26sp, `headlineSmall` 22sp, `titleLarge` 19sp, `titleMedium` 16sp, `titleSmall` 14sp, `bodyLarge` 15sp, `bodyMedium` 13sp, `bodySmall` 12sp, `labelLarge` 14sp, `labelMedium` 12sp, `labelSmall` 11sp).
- Menghindari judul yang terlalu besar atau penggunaan bobot super-tebal secara berlebihan pada label kecil.

---

## 4. Spasi, Radius & Elevasi

- **Grid Spasi:** Kelipatan 4px / 4dp (`8`, `12`, `16`, `20`, `24`).
- **Border Radius Konsisten:**
  - Badge / Tag kecil: `8dp` / `rounded-lg`
  - Tombol, input, filter pill: `12dp–14dp` / `rounded-xl`
  - Kartu utama & container: `16dp–20dp` / `rounded-2xl`
  - Modal / Bottom Sheet: `22dp–24dp` / `rounded-3xl`
- **Elevasi & Border:** Mengutamakan permukaan putih bersih dengan *hairline border* hangat (`1dp` `AromaBorder` / `border-cream-200`) di atas kanvas *Warm Cream* (`#F6F0E6`), meminimalkan bayangan tebal dan menghindari gradient dekoratif.
- **Touch Target:** Minimal `40dp–48dp` pada tombol navigasi, tombol aksi utama (`48dp–52dp`), serta stepper kuantitas yang nyaman disentuh.

---

## 5. Komponen Standar

### Customer Web (`customer-web/src/components/`)
- `Header.tsx`: Sticky header dengan identitas brand, tombol status pesanan live, badge meja QR, dan *segmented switcher* Dine In / Take Away yang adaptif di mobile maupun desktop.
- `CategoryBar.tsx`: Bar kategori *sticky* penuh di bawah header dengan jumlah item per kategori.
- `HeroSection.tsx`: Masthead kuliner ringkas berlatar Charcoal `#292722` sehingga menu tetap cepat terlihat di atas lipatan layar mobile.
- `ProductCard.tsx`: Tata letak responsif — kartu horizontal ringkas pada layar ponsel (`< 640px`) dan grid vertikal 2–3 kolom berfoto dominan 4:3 pada tablet/desktop.
- `ProductDetailModal.tsx`: Bottom sheet di mobile / dialog terpusat di desktop dengan stepper kuantitas besar dan kolom catatan.
- `CartDrawer.tsx`: Slide-over checkout drawer dengan thumbnail item, subtotal per baris, konfirmasi meja, dan pilihan metode pembayaran.
- `OrderTrackingModal.tsx`: Indikator progres pesanan 5 langkah, kartu QRIS / instruksi tunai, dan ringkasan struk digital.

### Admin Android (`admin-android/.../ui/`)
- `ui/theme/Color.kt`, `Type.kt`, `Theme.kt`: Token warna, tipografi, dan konfigurasi Material 3 + status bar.
- `ui/components/AromaComponents.kt`: Komponen bersama (`AromaCard`, `AromaStatusBadge`, `AromaTag`, `AromaFilterPill`, `AromaSectionHeader`, `AromaLoadingState`, `AromaEmptyState`, `AromaFeedbackBanner`, `aromaTextFieldColors`, `formatRupiah`).
- `ui/components/NotificationComponents.kt`: `CountBadge` dan `NotificationBellButton`.
- `ui/MainScreen.kt`: Top bar bersih dengan tombol kembali dan bottom navigation bar 5 tab berlabel lengkap (`Beranda`, `Pesanan`, `POS`, `Bayar`, `Menu`).

---

## 6. Prinsip Anti-Clutter

- Tidak menggunakan gradient dekoratif yang berlebihan, glassmorphism berat, atau dekorasi blur abstrak.
- Tidak menggunakan emoji sebagai pengganti ikon (gunakan Lucide Icons pada Web dan Material Icons pada Android).
- Tidak menambahkan elemen kartu berlapis-lapis tanpa fungsi atau statistik/grafik palsu.
- Setiap layar menyediakan *loading state*, *empty state*, dan *feedback/error state* yang jelas dan konsisten.
