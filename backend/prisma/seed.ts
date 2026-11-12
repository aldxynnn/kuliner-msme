import { PrismaClient, Role } from '@prisma/client';
import * as bcrypt from 'bcrypt';

const prisma = new PrismaClient();

async function main() {
  console.log('Seeding initial data safely (create-missing-only) for AROMA...');

  // 1. Create Default Users (Admin & Cashier) only if they don't exist yet
  const hashedPasswordAdmin = await bcrypt.hash('admin123', 10);
  const hashedPasswordCashier = await bcrypt.hash('kasir123', 10);

  const existingAdmin = await prisma.user.findUnique({ where: { email: 'admin@kuliner.com' } });
  if (!existingAdmin) {
    await prisma.user.create({
      data: {
        name: 'Owner / Admin AROMA',
        email: 'admin@kuliner.com',
        password: hashedPasswordAdmin,
        role: Role.ADMIN,
      },
    });
    console.log('Admin user created.');
  } else {
    console.log('Admin user already exists. Skipping update.');
  }

  const existingCashier = await prisma.user.findUnique({ where: { email: 'kasir@kuliner.com' } });
  if (!existingCashier) {
    await prisma.user.create({
      data: {
        name: 'Kasir AROMA',
        email: 'kasir@kuliner.com',
        password: hashedPasswordCashier,
        role: Role.CASHIER,
      },
    });
    console.log('Cashier user created.');
  } else {
    console.log('Cashier user already exists. Skipping update.');
  }

  // 2. Create Categories only if not found (Makanan, Minuman, Tambahan)
  const categoriesConfig = [
    { name: 'Makanan', description: 'Hidangan utama ayam goreng rempah dan menu lezat lainnya' },
    { name: 'Minuman', description: 'Minuman segar penawar dahaga' },
    { name: 'Tambahan', description: 'Menu pelengkap, camilan, dan dessert gurih' },
  ];

  const categoryMap: Record<string, string> = {};

  for (const catConf of categoriesConfig) {
    let cat = await prisma.category.findFirst({ where: { name: catConf.name } });
    if (!cat) {
      cat = await prisma.category.create({
        data: { name: catConf.name, description: catConf.description },
      });
      console.log(`Category created: ${catConf.name}`);
    } else {
      console.log(`Category already exists: ${catConf.name}. Skipping update.`);
    }
    categoryMap[catConf.name] = cat.id;
  }

  // 3. Create Products only if not found (using existing valid image paths in backend/uploads)
  const productsData = [
    {
      categoryId: categoryMap['Makanan'],
      name: 'Ayam Goreng Lengkuas (Dada/Paha)',
      description: 'Ayam goreng khas AROMA dengan taburan lengkuas rempah gurih, disajikan dengan nasi hangat dan sambal terasi',
      price: 28000,
      image: '/uploads/product-1794448729281-793234223.jpg',
      isAvailable: true,
    },
    {
      categoryId: categoryMap['Makanan'],
      name: 'Nasi Goreng Spesial AROMA',
      description: 'Nasi goreng rempah gurih dengan suwiran ayam lengkuas, telur ceplok, dan kerupuk',
      price: 25000,
      image: '/uploads/product-1794446811611-221735035.jpg',
      isAvailable: true,
    },
    {
      categoryId: categoryMap['Makanan'],
      name: 'Mie Goreng Jawa',
      description: 'Mie goreng bumbu tradisional dengan sayuran segar dan potongan bakso',
      price: 22000,
      image: '/uploads/product-1794447744868-752481955.jpg',
      isAvailable: true,
    },
    {
      categoryId: categoryMap['Makanan'],
      name: 'Soto Ayam Lamongan',
      description: 'Soto ayam kuah kuning rempah gurih dengan koya renyah',
      price: 24000,
      image: '/uploads/product-1794448792350-148880683.jpg',
      isAvailable: true,
    },
    {
      categoryId: categoryMap['Minuman'],
      name: 'Es Teh Manis',
      description: 'Teh melati pilihan dengan es segar',
      price: 6000,
      image: '/uploads/product-1794450081303-827282289.jpg',
      isAvailable: true,
    },
    {
      categoryId: categoryMap['Minuman'],
      name: 'Es Jeruk Peras',
      description: 'Jeruk segar diperas langsung kaya vitamin C',
      price: 10000,
      image: '/uploads/product-1794449536068-569550258.jpg',
      isAvailable: true,
    },
    {
      categoryId: categoryMap['Minuman'],
      name: 'Kopi Susu Gula Aren',
      description: 'Kopi susu dengan manis alami gula aren',
      price: 18000,
      image: '/uploads/product-1794449560619-210224480.jpg',
      isAvailable: true,
    },
    {
      categoryId: categoryMap['Tambahan'],
      name: 'Tempe Mendoan (4 pcs)',
      description: 'Tempe mendoan hangat gurih dengan sambal kecap pedas',
      price: 15000,
      image: '/uploads/product-1794450114344-672798759.jpg',
      isAvailable: true,
    },
    {
      categoryId: categoryMap['Tambahan'],
      name: 'Tahu Crispy Cabai Garam',
      description: 'Tahu goreng renyah dengan taburan cabai garam',
      price: 16000,
      image: '/uploads/product-1794449501897-396675996.jpg',
      isAvailable: true,
    },
    {
      categoryId: categoryMap['Tambahan'],
      name: 'Pisang Goreng Keju Cokelat',
      description: 'Pisang raja goreng krispi dengan parutan keju dan cokelat',
      price: 18000,
      image: '/uploads/product-1794450025322-443968584.jpg',
      isAvailable: true,
    },
  ];

  for (const p of productsData) {
    const existing = await prisma.product.findFirst({
      where: { name: p.name },
    });
    if (!existing) {
      await prisma.product.create({ data: p });
      console.log(`Product created: ${p.name}`);
    } else {
      console.log(`Product already exists: ${p.name}. Skipping update.`);
    }
  }

  // 4. Create Tables (Meja 01 - 10) only if not found, using dynamic FRONTEND_URL & URL searchParams
  const frontendBaseUrl = process.env.FRONTEND_URL || 'http://localhost:5173';

  for (let i = 1; i <= 10; i++) {
    const num = i < 10 ? `0${i}` : `${i}`;
    const tableNum = `Meja ${num}`;

    const existingTable = await prisma.table.findUnique({
      where: { tableNumber: tableNum },
    });

    if (!existingTable) {
      const qrUrl = new URL(frontendBaseUrl);
      qrUrl.searchParams.set('table', tableNum);

      await prisma.table.create({
        data: {
          tableNumber: tableNum,
          qrCode: qrUrl.toString(),
          isAvailable: true,
        },
      });
      console.log(`Table created: ${tableNum}`);
    } else {
      console.log(`Table already exists: ${tableNum}. Skipping.`);
    }
  }

  console.log('Safe initial seeding completed successfully for AROMA!');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
