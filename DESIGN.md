# UI/UX DESIGN SYSTEM — AROMA POS

Panduan sistem desain resmi **AROMA POS** (*Ayam Goreng Lengkuas Aroma*) yang diterapkan secara konsisten pada **Customer Web** (`customer-web`) dan **Admin Android POS** (`admin-android`).

---

## 1. Arah Visual & Kepribadian Brand

- **Contemporary Nusantara Atelier & Commercial POS:** Identitas desain arsitektural bertema *Architectural Spice & Paper* yang memadukan keterbacaan POS komersial modern dengan kehangatan rempah Nusantara.
- **Tactile Paper & Crisp Hairlines:** Permukaan putih bersih dan *Warm Alabaster Paper* (`#FBF9F5`) dipisahkan garis tipis `1px/1dp` *Warm Sand* (`#E6DFD5`), ditopang blok kontras **Deep Roast Ink** (`#171311`) untuk header tiket, ledger omzet, dan kontrol aktif.
- **Clarity First:** Hierarki informasi cepat dipindai, kode pesanan dan nominal harga memakai angka monospace (`tabular-nums`), dan setiap layar menampilkan status operasional secara jelas.

---

## 2. Palet Warna Resmi Aroma POS

### Warna Inti (Core Brand Tokens)

| Token | Hex | Web (Tailwind) | Android (Compose) | Fungsi |
| :--- | :--- | :--- | :--- | :--- |
| **Deep Roast Ink** | `#171311` | `ink` | `AromaCharcoal` | Teks utama, kontrol aktif, header tiket pesanan, ledger eksekutif |
| **Lengkuas Vermilion** | `#C84319` | `spice-500` | `AromaTerracotta` | Aksi primer checkout/tambah, indikator aktif, tombol CTA utama |
| **Vermilion Hover / Dark** | `#A93612` / `#8C2A0C` | `spice-600` / `spice-700` | `AromaTerracottaDark` | State hover/pressed & aksen teks kontras |
| **Vermilion Soft** | `#FDF1EC` | `spice-50` | `AromaTerracottaSoft` | Highlight halus, banner status dimasak, tag kategori |
| **Vermilion Border** | `#F2C9B8` | `spice-200` | `AromaTerracottaBorder` | Border elemen terpilih & badge |
| **Pandan Leaf Green** | `#14533C` | `pandan-600` | `AromaSuccess` | Badge meja aktif, status lunas/siap saji, switch tersedia, tombol konfirmasi lunas |
| **Alabaster Paper** | `#FBF9F5` | `paper-50` | `AromaCream` | Kanvas utama halaman & layar |
| **Warm Stone Tint** | `#F3EFE8` / `#EBE5DC` | `paper-100` | `AromaCreamSoft` / `AromaCreamTrack` | Latar inset input, placeholder thumbnail, track segmented control |
| **Warm Sand Hairline** | `#E6DFD5` | `paper-200` | `AromaBorder` | Hairline border kartu & pembatas antar bagian (`#D5CBBF` untuk garis putus-putus tiket) |
| **White Surface** | `#FFFFFF` | `white` | `AromaSurface` | Permukaan kartu, panel tiket, header, modal, bottom bar |
| **Ink Secondary / Muted** | `#4E4640` / `#7C726B` | `ink-secondary` / `ink-muted` | `AromaTextSecondary` / `AromaTextMuted` | Deskripsi produk, metadata, timestamp, placeholder |

### Warna Semantik (Status & Feedback)

| Status | Teks / Aksen | Latar Halus | Border | Penggunaan |
| :--- | :--- | :--- | :--- | :--- |
| **Success (Pandan)** | `#14533C` | `#EAF5F0` | `#B9DEC9` | Pembayaran `LUNAS`, `Siap Disajikan`, badge meja aktif, toast sukses |
| **Warning (Turmeric)** | `#B4690E` | `#FEF5E7` | `#F2D49B` | `Belum Bayar` / `MENUNGGU_PEMBAYARAN`, notifikasi pesanan masuk |
| **Error (Crimson)** | `#B92D26` | `#FDEEEC` | `#F3C1BE` | `DIBATALKAN`, pengeluaran, validasi error, tombol hapus/keluar |
| **Info (Slate)** | `#1D5A7E` | `#ECF5FA` | `#BCD9EA` | `Masuk Antrean` (`MENUNGGU_DIPROSES`) |

---

## 3. Tipografi

- **Customer Web:** `Playfair Display` (`font-display` untuk judul editorial, wordmark brand, dan nama hidangan modal), `DM Sans` (`font-sans` untuk UI, deskripsi, tombol), dan `JetBrains Mono` (`font-mono` untuk nomor pesanan, kode meja, metadata struk, dan nominal harga `tabular-nums`).
- **Admin Android:** Skala tipografi Material 3 pada `ui/theme/Type.kt` yang memadukan `FontFamily.SansSerif` untuk keterbacaan UI, `AromaBrandSerifStyle` (`FontFamily.Serif`) untuk wordmark `"AROMA"` dan tajuk toko pada struk, serta `FontFamily.Monospace` untuk kode tiket (`ORD-...`), angka KPI, daftar nomor meja, dan nominal transaksi.

---

## 4. Spasi, Radius & Elevasi

- **Grid Spasi:** Kelipatan 4px / 4dp (`8`, `12`, `16`, `20`, `24`).
- **Border Radius Konsisten:**
  - Badge / Tag & Filter Pill: `6dp–8dp` / `rounded-md` – `rounded-lg`
  - Tombol & Kontrol Tersegmentasi: `10dp–12dp` / `rounded-xl` atau `rounded-full`
  - Kartu utama & Panel Tiket: `12dp–16dp` (`AromaCard` default `12.dp`) / `rounded-2xl`
  - Modal / Bottom Sheet: `18dp–24dp` / `rounded-3xl`
- **Elevasi & Border:** Mengutamakan komposisi datar dengan *hairline border* `1dp/1px` dan `surfaceTint = AromaSurface` agar dialog maupun kartu tetap bersih di atas kanvas *Alabaster Paper* (`#FBF9F5`).
- **Touch Target:** Minimal `40dp–48dp` pada navigasi utama, `48dp–54dp` pada CTA transaksi utama, serta kontrol kuantitas yang ergonomis.

---

## 5. Komponen Standar

### Customer Web (`customer-web/src/components/`)
- `Header.tsx`: Sticky bar arsitektural `64px` berlatar `paper-50/95` dengan wordmark `Playfair Display`, pemisah vertikal tipis, mode switcher *pill* (`Dine In` | `Take Away`) bertombol aktif `Deep Roast Ink`, status pesanan live, dan tag meja Pandan Green.
- `CategoryBar.tsx`: Tab kategori *sticky* berbaris tunggal dengan garis bawah aktif `border-ink` dan superscript jumlah menu `font-mono`.
- `HeroSection.tsx`: Etalase dua panel asimetris (*Editorial Copy Panel* di kiri + *Live Signature Menu Showcase* bergambar foto menu nyata dari backend di kanan, yang dapat diklik untuk membuka detail menu).
- `ProductCard.tsx`: Kartu galeri editorial — baris horizontal berfoto kanan dengan pill `+ Tambah` / `✓ N×` di atas thumbnail pada mobile (`< sm`), serta kartu vertikal foto `4:3` berkontras tinggi pada tablet/desktop (`sm+`).
- `ProductDetailModal.tsx`: Bottom sheet / dialog detail menu dengan foto penuh, catatan dapur, pemilih kuantitas bulat, dan CTA gelap `#171311`.
- `CartDrawer.tsx`: Menyediakan **`CartOrderTicketPanel`** (panel tiket pesanan persisten di sisi kanan layar desktop `lg+` lengkap dengan garis putus-putus struk) dan **`CartDrawer`** (drawer geser untuk layar mobile/tablet) menggunakan logika checkout tunggal.
- `OrderTrackingModal.tsx`: Pelacak tiket pesanan dengan header *Deep Roast* (`LIVE TRACKING`), progres 5 langkah Pandan/Vermilion, blok QRIS/Tunai, dan rincian struk digital.

### Admin Android (`admin-android/.../ui/`)
- `ui/theme/Color.kt`, `Type.kt`, `Theme.kt`: Palet `Deep Roast` + `Alabaster Paper` + `Vermilion` + `Pandan Green`, tipografi Serif/Sans/Monospace, serta `surfaceTint = AromaSurface`.
- `ui/components/AromaComponents.kt`: Komponen bersama (`AromaCard`, `AromaStatusBadge`, `AromaTag`, `AromaFilterPill`, `AromaSegmentedControl`, `AromaDashedDivider`, `AromaSectionHeader`, `AromaLoadingState`, `AromaEmptyState`, `AromaFeedbackBanner`, `aromaTextFieldColors`, `formatRupiah`).
- `ui/components/NotificationComponents.kt`: `CountBadge` dan `NotificationBellButton`.
- `ui/MainScreen.kt`: Top bar kontekstual (header top-level berlonceng notifikasi tanpa panah kembali berlebih, dan header tombol kembali pada sub-layar) serta bottom bar `68.dp` dengan **pill terminal `POS` menonjol di tengah**.
- `ui/screens/LoginScreen.kt`, `DashboardScreen.kt`, `ReportsScreen.kt`, `ExpensesScreen.kt`, `ProfileScreen.kt`: Blok ledger *Deep Roast Ink* (`#171311`), strip KPI gabungan *Pesanan Aktif | Belum Bayar*, bilah proporsi omzet vs pengeluaran, serta direktori manajemen usaha bernomor indeks monospace (`01`–`04`).
- `ui/screens/OrdersScreen.kt`, `PaymentsScreen.kt`, `CashierOrderScreen.kt`, `ProductsScreen.kt`, `CategoriesScreen.kt`, `TablesScreen.kt`, `NotificationsScreen.kt`, `ReceiptDialog.kt`: Tiket dapur bergaris status kiri vertikal, *checkout bar* *Deep Roast* di kasir POS, verifikasi bayar Pandan Green, kartu meja QR monospace, dan dialog struk termal.

---

## 6. Prinsip Anti-Clutter

- Tidak menggunakan gradient dekoratif yang berlebihan, glassmorphism berat, atau dekorasi blur abstrak.
- Tidak menggunakan emoji sebagai pengganti ikon (gunakan Lucide Icons pada Web dan Material Icons pada Android).
- Tidak menambahkan elemen kartu berlapis-lapis tanpa fungsi atau statistik/grafik palsu.
- Setiap layar menyediakan *loading state*, *empty state*, dan *feedback/error state* yang jelas dan konsisten.
