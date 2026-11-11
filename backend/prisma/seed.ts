import { PrismaClient, Role, OrderType, PaymentMethod, PaymentStatus, OrderStatus } from '@prisma/client';
import * as bcrypt from 'bcrypt';

const prisma = new PrismaClient();

async function main() {
  console.log('Seeding database...');

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
      name: 'Owner / Admin',
      email: 'admin@kuliner.com',
      password: hashedPasswordAdmin,
      role: Role.ADMIN,
    },
  });

  const cashier = await prisma.user.create({
    data: {
      name: 'Kasir Utama',
      email: 'kasir@kuliner.com',
      password: hashedPasswordCashier,
      role: Role.CASHIER,
    },
  });

  console.log('Users created:', { admin: admin.email, cashier: cashier.email });

  // Create Categories
  const catMakanan = await prisma.category.create({
    data: {
      name: 'Makanan Utama',
      description: 'Hidangan utama lezat dan mengenyangkan',
    },
  });

  const catMinuman = await prisma.category.create({
    data: {
      name: 'Minuman',
      description: 'Segar dan penghilang dahaga',
    },
  });

  const catCamilan = await prisma.category.create({
    data: {
      name: 'Makanan Ringan',
      description: 'Camilan gurih dan manis penyempurna suasana',
    },
  });

  const catPenutup = await prisma.category.create({
    data: {
      name: 'Dessert',
      description: 'Hidangan penutup manis khas Nusantara',
    },
  });

  // Create Products
  const productsData = [
    {
      categoryId: catMakanan.id,
      name: 'Nasi Goreng Spesial',
      description: 'Nasi goreng khas kuliner Nusantara dengan telur ceplok, ayam suwir, dan kerupuk',
      price: 25000,
      image: '/uploads/nasi-goreng.jpg',
      isAvailable: true,
    },
    {
      categoryId: catMakanan.id,
      name: 'Ayam Goreng Lengkuas',
      description: 'Ayam goreng renyah bumbu lengkuas gurih disajikan dengan nasi hangat dan sambal terasi',
      price: 28000,
      image: '/uploads/ayam-lengkuas.jpg',
      isAvailable: true,
    },
    {
      categoryId: catMakanan.id,
      name: 'Mie Goreng Jawa',
      description: 'Mie goreng bumbu rempah tradisional Jawa dengan sayuran segar dan bakso',
      price: 22000,
      image: '/uploads/mie-goreng.jpg',
      isAvailable: true,
    },
    {
      categoryId: catMakanan.id,
      name: 'Soto Ayam Lamongan',
      description: 'Soto ayam kuah kuning gurih dengan koya renyah, bihun, dan telur rebus',
      price: 24000,
      image: '/uploads/soto-ayam.jpg',
      isAvailable: true,
    },
    {
      categoryId: catMinuman.id,
      name: 'Es Teh Manis',
      description: 'Es teh manis segar penyegar dahaga',
      price: 6000,
      image: '/uploads/es-teh.jpg',
      isAvailable: true,
    },
    {
      categoryId: catMinuman.id,
      name: 'Es Jeruk Peras',
      description: 'Minuman jeruk peras alami kaya vitamin C',
      price: 10000,
      image: '/uploads/es-jeruk.jpg',
      isAvailable: true,
    },
    {
      categoryId: catMinuman.id,
      name: 'Kopi Susu Gula Aren',
      description: 'Kopi espresso dengan susu segar dan manis alami gula aren khas Jawa',
      price: 18000,
      image: '/uploads/kopi-susu.jpg',
      isAvailable: true,
    },
    {
      categoryId: catCamilan.id,
      name: 'Tempe Mendoan (4 pcs)',
      description: 'Tempe goreng tepung setengah matang gurih disajikan dengan sambal kecap pedas',
      price: 15000,
      image: '/uploads/tempe-mendoan.jpg',
      isAvailable: true,
    },
    {
      categoryId: catCamilan.id,
      name: 'Tahu Crispy Cabai Garam',
      description: 'Tahu renyah digoreng gurih dengan taburan cabai merah dan garam gurih',
      price: 16000,
      image: '/uploads/tahu-crispy.jpg',
      isAvailable: true,
    },
    {
      categoryId: catPenutup.id,
      name: 'Pisang Goreng Keju Cokelat',
      description: 'Pisang raja manis digoreng krispi dengan parutan keju melimpah dan susu cokelat',
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
  const tables = [];
  for (let i = 1; i <= 10; i++) {
    const num = i < 10 ? `0${i}` : `${i}`;
    const tableNum = `Meja ${num}`;
    const qrCodeUrl = `http://localhost:5173/?table=${tableNum}`;
    const t = await prisma.table.create({
      data: {
        tableNumber: tableNum,
        qrCode: qrCodeUrl,
        isAvailable: true,
      },
    });
    tables.push(t);
  }

  console.log('Tables created');

  // Create Expenses
  await prisma.expense.createMany({
    data: [
      {
        description: 'Pembelian Bahan Baku Sembako & Daging',
        amount: 850000,
        category: 'Bahan Baku',
        notes: 'Belanja pasar induk pagi',
        date: new Date(),
      },
      {
        description: 'Pembayaran Tagihan Listrik & Air Toko',
        amount: 320000,
        category: 'Operasional',
        notes: 'Bulan berjalan',
        date: new Date(),
      },
    ],
  });

  console.log('Expenses created');
  console.log('Database seeded successfully!');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
