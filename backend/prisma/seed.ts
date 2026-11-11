import { PrismaClient, Role, OrderType, PaymentMethod, PaymentStatus, OrderStatus } from '@prisma/client';
import * as bcrypt from 'bcrypt';

const prisma = new PrismaClient();

async function main() {
  console.log('Seeding database for AROMA – Ayam Goreng Lengkuas...');

  // Clean existing data
  await prisma.expense.deleteMany();
  await prisma.payment.deleteMany();
  await prisma.orderDetail.deleteMany();
  await prisma.order.deleteMany();
  await prisma.table.deleteMany();
  await prisma.product.deleteMany();
  await prisma.category.deleteMany();
  await prisma.user.deleteMany();

  // Create Users
  const hashedPasswordAdmin = await bcrypt.hash('admin123', 10);
  const hashedPasswordCashier = await bcrypt.hash('kasir123', 10);

  const admin = await prisma.user.create({
    data: {
      name: 'Owner / Admin AROMA',
      email: 'admin@kuliner.com',
      password: hashedPasswordAdmin,
      role: Role.ADMIN,
    },
  });

  const cashier = await prisma.user.create({
    data: {
      name: 'Kasir AROMA',
      email: 'kasir@kuliner.com',
      password: hashedPasswordCashier,
      role: Role.CASHIER,
    },
  });

  console.log('Users created:', { admin: admin.email, cashier: cashier.email });

  // Create Categories (Only 3: Makanan, Minuman, Tambahan)
  const catMakanan = await prisma.category.create({
    data: {
      name: 'Makanan',
      description: 'Hidangan utama ayam goreng rempah dan menu lezat lainnya',
    },
  });

  const catMinuman = await prisma.category.create({
    data: {
      name: 'Minuman',
      description: 'Minuman segar penawar dahaga',
    },
  });

  const catTambahan = await prisma.category.create({
    data: {
      name: 'Tambahan',
      description: 'Menu pelengkap, camilan, dan dessert gurih',
    },
  });

  // Create Products mapped to Makanan, Minuman, Tambahan
  const productsData = [
    {
      categoryId: catMakanan.id,
      name: 'Ayam Goreng Lengkuas (Dada/Paha)',
      description: 'Ayam goreng khas AROMA dengan taburan lengkuas rempah gurih, disajikan dengan nasi hangat dan sambal terasi',
      price: 28000,
      image: '/uploads/ayam-lengkuas.jpg',
      isAvailable: true,
    },
    {
      categoryId: catMakanan.id,
      name: 'Nasi Goreng Spesial AROMA',
      description: 'Nasi goreng rempah gurih dengan suwiran ayam lengkuas, telur ceplok, dan kerupuk',
      price: 25000,
      image: '/uploads/nasi-goreng.jpg',
      isAvailable: true,
    },
    {
      categoryId: catMakanan.id,
      name: 'Mie Goreng Jawa',
      description: 'Mie goreng bumbu tradisional dengan sayuran segar dan potongan bakso',
      price: 22000,
      image: '/uploads/mie-goreng.jpg',
      isAvailable: true,
    },
    {
      categoryId: catMakanan.id,
      name: 'Soto Ayam Lamongan',
      description: 'Soto ayam kuah kuning rempah gurih dengan koya renyah',
      price: 24000,
      image: '/uploads/soto-ayam.jpg',
      isAvailable: true,
    },
    {
      categoryId: catMinuman.id,
      name: 'Es Teh Manis',
      description: 'Teh melati pilihan dengan es segar',
      price: 6000,
      image: '/uploads/es-teh.jpg',
      isAvailable: true,
    },
    {
      categoryId: catMinuman.id,
      name: 'Es Jeruk Peras',
      description: 'Jeruk segar diperas langsung kaya vitamin C',
      price: 10000,
      image: '/uploads/es-jeruk.jpg',
      isAvailable: true,
    },
    {
      categoryId: catMinuman.id,
      name: 'Kopi Susu Gula Aren',
      description: 'Kopi susu dengan manis alami gula aren',
      price: 18000,
      image: '/uploads/kopi-susu.jpg',
      isAvailable: true,
    },
    {
      categoryId: catTambahan.id,
      name: 'Tempe Mendoan (4 pcs)',
      description: 'Tempe mendoan hangat gurih dengan sambal kecap pedas',
      price: 15000,
      image: '/uploads/tempe-mendoan.jpg',
      isAvailable: true,
    },
    {
      categoryId: catTambahan.id,
      name: 'Tahu Crispy Cabai Garam',
      description: 'Tahu goreng renyah dengan taburan cabai garam',
      price: 16000,
      image: '/uploads/tahu-crispy.jpg',
      isAvailable: true,
    },
    {
      categoryId: catTambahan.id,
      name: 'Pisang Goreng Keju Cokelat',
      description: 'Pisang raja goreng krispi dengan parutan keju dan cokelat',
      price: 18000,
      image: '/uploads/pisang-goreng.jpg',
      isAvailable: true,
    },
  ];

  for (const p of productsData) {
    await prisma.product.create({ data: p });
  }

  console.log('Products created');

  // Create Tables (Meja 01 - 10)
  for (let i = 1; i <= 10; i++) {
    const num = i < 10 ? `0${i}` : `${i}`;
    const tableNum = `Meja ${num}`;
    const qrCodeUrl = `http://localhost:5173/?table=${tableNum}`;
    await prisma.table.create({
      data: {
        tableNumber: tableNum,
        qrCode: qrCodeUrl,
        isAvailable: true,
      },
    });
  }

  console.log('Tables created');

  // Create Expenses
  await prisma.expense.createMany({
    data: [
      {
        description: 'Pembelian Ayam Segar & Bumbu Rempah',
        amount: 950000,
        category: 'Bahan Baku',
        notes: 'Belanja harian pasar',
        date: new Date(),
      },
      {
        description: 'Listrik & Gas LPG 3kg',
        amount: 250000,
        category: 'Operasional',
        notes: 'Bulanan',
        date: new Date(),
      },
    ],
  });

  console.log('Expenses created');
  console.log('Database seeded successfully for AROMA!');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
