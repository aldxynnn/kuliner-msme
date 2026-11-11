
# SYSTEM FEATURES

## A. CUSTOMER ORDERING WEBSITE

Build a mobile-first customer ordering experience.

### 1. QR Table Access
- Each table has a unique QR code.
- QR contains the customer website URL and table ID.
- Automatically identify the table.
- Validate table availability.

### 2. Ordering Type
- Dine In.
- Take Away.
- Table selection is required only for Dine In.

### 3. Menu
- Display product categories.
- Display product images.
- Display names, descriptions and prices.
- Display product availability.
- Allow customers to select quantities.
- Add products to cart.
- Add notes to orders.

### 4. Cart and Checkout
- View cart.
- Change quantities.
- Remove products.
- Calculate subtotal and total.
- Enter customer name optionally.
- Select payment method.

### 5. Payment
Supported methods:
- QRIS.
- Cash at cashier.

QRIS MVP:
- Display configured QRIS payment information.
- Cashier manually verifies payment.
- Do not claim automatic verification.

Cash payment:
- Order remains unpaid until cashier confirms payment.

### 6. Order Tracking
- Generate unique order number.
- Show payment status.
- Show order processing status.
- Display order summary.
- Display digital receipt after payment.

---

## B. ANDROID CASHIER AND ADMIN APPLICATION

Build a native Android POS application.

### 1. Authentication
- Login.
- Logout.
- Protected admin features.

### 2. Dashboard
- Today's revenue.
- Today's transaction count.
- Pending payments.
- Active orders.
- Completed orders.
- Simple sales summary.

### 3. Category Management
- Add category.
- Edit category.
- Delete category.
- Display categories.

### 4. Product Management
- Add products.
- Edit products.
- Delete products.
- Upload product photos.
- Set name and description.
- Set price.
- Assign category.
- Enable or disable availability.

Changes must immediately be reflected
on the customer website.

### 5. Table Management
- Create tables.
- Edit table information.
- Delete tables when safe.
- Generate unique QR codes.
- Display and share QR codes.

### 6. Order Management
- View incoming orders.
- View order details.
- Filter orders by status.
- Update order status.
- Cancel unpaid orders.

### 7. Manual Cashier Ordering
Cashiers must be able to create orders manually.

- Select Dine In or Take Away.
- Select table for Dine In.
- Select products.
- Set quantities.
- Add order notes.
- Enter customer name optionally.
- Select payment method.
- Confirm cash payment.

### 8. Payment Management
- Display unpaid orders.
- Confirm cash payments.
- Manually verify QRIS payments.
- Display payment history.
- Prevent duplicate payment confirmation.

Only paid orders count as revenue.

### 9. Order Processing

Use the following order statuses:

- MENUNGGU_PEMBAYARAN
- MENUNGGU_DIPROSES
- DIPROSES
- SIAP_DISAJIKAN
- SELESAI
- DIBATALKAN

Separate payment status from order status.

Unpaid orders must not enter the kitchen processing queue.

### 10. Receipt
- Generate digital receipt.
- Provide printable receipt.
- Include business information.
- Include order number.
- Include date and time.
- Include purchased products.
- Include quantities and prices.
- Include total and payment method.

### 11. Financial Management
- Automatically record confirmed sales.
- Manually record business expenses.
- Edit and delete expenses.
- Display daily reports.
- Display monthly reports.
- Display transaction counts.
- Display total revenue.
- Display total expenses.
- Display simple operating surplus.

Operating surplus = revenue - recorded expenses.

Do not describe this as complete accounting profit.

---

## C. BACKEND AND DATABASE

Create REST API endpoints for all major features.

Required database entities:

- users
- categories
- products
- tables
- orders
- order_details
- payments
- expenses

Requirements:
- Proper relational relationships.
- Foreign key constraints.
- Appropriate indexes.
- Created and updated timestamps.
- Prisma migrations.
- Seed data.
- Password hashing.
- Input validation.
- Authentication and authorization.
- Safe payment confirmation.
- Database transactions for critical operations.

## D. BUSINESS RULES

1. Each QR code identifies a specific table.
2. Dine In orders must have a valid table.
3. Take Away orders do not require a table.
4. Unpaid orders cannot be processed.
5. Payment confirmation must be recorded exactly once.
6. Only confirmed payments contribute to revenue.
7. Product availability must be reflected on the website.
8. Manual cashier orders and customer orders use
   the same order management system.
9. Receipts must be generated from actual order data.
10. Cancelled orders must not count as revenue.

## E. MVP PRIORITIES

Mandatory:
- Customer QR ordering.
- Menu management.
- Cart and checkout.
- Manual cashier ordering.
- Payment confirmation.
- Order management.
- Receipt generation.
- Expense recording.
- Sales reports.

Optional if time permits:
- Basic inventory.
- Sales charts.
- Thermal printer integration.
- Automatic payment gateway integration.

Do not sacrifice mandatory features for optional ones.
