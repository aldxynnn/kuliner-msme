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
| **Terracotta** | `#C94F1D` | `brand-500` | `AromaTerracotta` | Warna primer, tombol CTA utama, indikator aktif |
| **Terracotta Hover / Dark** | `#B04115` / `#A83E14` / `#983610` | `brand-600` / `brand-700` | `AromaTerracottaDark` | State hover/pressed & aksen teks kontras |
| **Terracotta Soft** | `#FFF3EC` | `brand-50` | `AromaTerracottaSoft` | Latar chip aktif, container ikon, highlight halus |
| **Terracotta Border** | `#F2D5C6` | `brand-200` | `AromaTerracottaBorder` | Border elemen terpilih & badge brand |
| **Warm Ivory (Canvas)** | `#FAF8F5` | `cream-50` | `AromaCream` | Kanvas utama halaman/layar terang |
| **Soft Linen** | `#F4EFE8` / `#EFE9E0` | `cream-100` | `AromaCreamSoft` / `AromaCreamTrack` | Latar inset input, thumbnail placeholder, track kontrol tersegmentasi |
| **Warm Hairline** | `#E8E0D6` | `cream-200` | `AromaBorder` | Hairline border kartu, pembatas list, pemisah antar bagian |
| **White Surface** | `#FFFFFF` | `white` | `AromaSurface` | Permukaan kartu, header, modal, drawer, navigation bar |
| **Espresso Charcoal** | `#2B211D` | `charcoal` | `AromaCharcoal` | Teks utama dan heading berkontras tinggi |
| **Charcoal Secondary** | `#635852` | `charcoal-secondary` | `AromaTextSecondary` | Teks deskripsi & label sekunder |
| **Charcoal Muted** | `#8C8078` | `charcoal-muted` | `AromaTextMuted` | Caption, timestamp, placeholder |

### Warna Semantik (Status & Feedback)

| Status | Teks / Aksen | Latar Halus | Border | Penggunaan |
| :--- | :--- | :--- | :--- | :--- |
| **Success** | `#1F7A52` | `#E9F7F0` | `#BCE3CE` | Pembayaran `LUNAS`, `Siap Disajikan`, omzet, toast sukses |
| **Warning** | `#B87314` | `#FEF4E6` | `#F2D6A2` | `Belum Bayar` / `MENUNGGU_PEMBAYARAN`, status live pesanan |
| **Error** | `#C43A2F` | `#FCEEEC` | `#F3BEB9` | `DIBATALKAN`, pengeluaran, validasi error, tombol hapus/keluar |
| **Info** | `#25638A` | `#EAF3F9` | `#BBD8EA` | `Masuk Antrean` (`MENUNGGU_DIPROSES`) |

---

## 3. Tipografi

- **Customer Web:** `Fraunces` (`font-display` untuk judul editorial, nama modal, dan identitas masthead), `Plus Jakarta Sans` (`font-sans` untuk UI, label, tombol, harga), dan `Inter` (`font-body` untuk deskripsi panjang), dengan `tabular-nums` untuk nominal harga dan kuantitas.
- **Admin Android:** Skala tipografi Material 3 terdefinisi pada `ui/theme/Type.kt` (`headlineMedium` 26sp, `headlineSmall` 22sp, `titleLarge` 19sp, `titleMedium` 16sp, `titleSmall` 14sp, `bodyLarge` 15sp, `bodyMedium` 13sp, `bodySmall` 12sp, `labelLarge` 14sp, `labelMedium` 12sp, `labelSmall` 11sp).
- Menghindari judul yang terlalu besar atau penggunaan bobot super-tebal secara berlebihan pada label kecil.

---

## 4. Spasi, Radius & Elevasi

- **Grid Spasi:** Kelipatan 4px / 4dp (`8`, `12`, `16`, `20`, `24`).
- **Border Radius Konsisten:**
  - Badge / Tag kecil: `8dp` / `rounded-lg`
  - Tombol, input, filter pill: `12dp–14dp` / `rounded-xl`
  - Kartu utama & container: `14dp–20dp` (`AromaCard` default `14.dp`) / `rounded-2xl`
  - Modal / Bottom Sheet: `22dp–24dp` / `rounded-3xl`
- **Elevasi & Border:** Mengutamakan permukaan putih terang dengan *hairline border* hangat (`1dp` `AromaBorder` / `border-cream-200`) di atas kanvas *Warm Ivory* (`#FAF8F5`), meminimalkan bayangan tebal dan menghindari gradient dekoratif serta blok warna gelap berlebihan.
- **Touch Target:** Minimal `40dp–48dp` pada tombol navigasi, tombol aksi utama (`48dp–52dp`), serta stepper kuantitas yang nyaman disentuh.

---

## 5. Komponen Standar

### Customer Web (`customer-web/src/components/`)
- `Header.tsx`: Sticky header putih bersih dengan identitas brand, tombol status pesanan live, badge meja QR, dan *segmented switcher* Dine In / Take Away berlatar linen `#F4EFE8`.
- `CategoryBar.tsx`: Bar navigasi kategori *sticky* berlatar putih dengan garis bawah aktif terakota dan jumlah item per kategori.
- `HeroSection.tsx`: Masthead kuliner editorial berlatar putih terang dengan tipografi `Fraunces` dan pill info ringkas (`100% Rempah Asli`, `Disajikan Hangat`, `Serang, Banten`).
- `ProductCard.tsx`: Tata letak responsif — kartu horizontal ringkas berthumbnail kanan pada layar ponsel (`< 640px`) dan grid vertikal 2–3 kolom berfoto dominan 4:3 pada tablet/desktop.
- `ProductDetailModal.tsx`: Bottom sheet di mobile / dialog terpusat di desktop dengan stepper kuantitas besar dan kolom catatan.
- `CartDrawer.tsx`: Slide-over checkout drawer dengan thumbnail item, subtotal per baris, konfirmasi meja, dan pilihan metode pembayaran.
- `OrderTrackingModal.tsx`: Indikator progres pesanan 5 langkah, kartu QRIS / instruksi tunai, tombol tutup outlined yang nyaman disentuh, dan ringkasan struk digital.

### Admin Android (`admin-android/.../ui/`)
- `ui/theme/Color.kt`, `Type.kt`, `Theme.kt`: Token warna terang hangat, tipografi, dan konfigurasi Material 3 + status bar.
- `ui/components/AromaComponents.kt`: Komponen bersama (`AromaCard`, `AromaStatusBadge`, `AromaTag`, `AromaFilterPill`, `AromaSectionHeader`, `AromaLoadingState`, `AromaEmptyState`, `AromaFeedbackBanner`, `aromaTextFieldColors`, `formatRupiah`).
- `ui/components/NotificationComponents.kt`: `CountBadge` dan `NotificationBellButton`.
- `ui/MainScreen.kt`: Top bar putih bersih `56.dp` dan bottom navigation bar datar `64.dp` (5 tab berlabel lengkap: `Beranda`, `Pesanan`, `POS`, `Bayar`, `Menu`) dengan indikator garis terakota di tepi atas.
- `ui/screens/DashboardScreen.kt`, `ReportsScreen.kt`, `ProfileScreen.kt`: Lembaran kartu putih beraksen garis terakota `3.dp` di sisi atas dan panel menu terkelompok dengan `HorizontalDivider` sehingga bersih dan bebas kotak berlebihan.
- `ui/screens/CashierOrderScreen.kt`, `PaymentsScreen.kt`: Kontrol tersegmentasi putih di atas track linen `#EFE9E0` serta bar ringkasan keranjang POS terakota `#C94F1D`.

---

## 6. Prinsip Anti-Clutter

- Tidak menggunakan gradient dekoratif yang berlebihan, glassmorphism berat, atau dekorasi blur abstrak.
- Tidak menggunakan emoji sebagai pengganti ikon (gunakan Lucide Icons pada Web dan Material Icons pada Android).
- Tidak menambahkan elemen kartu berlapis-lapis tanpa fungsi atau statistik/grafik palsu.
- Setiap layar menyediakan *loading state*, *empty state*, dan *feedback/error state* yang jelas dan konsisten.
