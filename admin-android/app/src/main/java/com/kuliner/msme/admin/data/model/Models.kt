package com.kuliner.msme.admin.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    val token: String,
    val user: User
)

data class Category(
    val id: String,
    val name: String,
    val description: String?,
    @SerializedName("_count") val count: CategoryCount? = null
)

data class CategoryCount(
    val products: Int = 0
)

data class CreateCategoryRequest(
    val name: String,
    val description: String? = null
)

data class Product(
    val id: String,
    val categoryId: String,
    val category: Category?,
    val name: String,
    val description: String?,
    val price: Int,
    val image: String?,
    val isAvailable: Boolean
)

data class CreateProductRequest(
    val categoryId: String,
    val name: String,
    val description: String? = null,
    val price: Int,
    val image: String? = null,
    val isAvailable: Boolean = true
)

data class ToggleAvailabilityRequest(
    val isAvailable: Boolean
)

data class Table(
    val id: String,
    val tableNumber: String,
    val qrCode: String,
    val isAvailable: Boolean
)

data class CreateTableRequest(
    val tableNumber: String,
    val isAvailable: Boolean = true
)

data class OrderDetail(
    val id: String,
    val productId: String?,
    val productName: String,
    val price: Int,
    val quantity: Int,
    val notes: String?,
    val subtotal: Int
)

data class Payment(
    val id: String,
    val orderId: String,
    val amount: Int,
    val paymentMethod: String,
    val status: String,
    val confirmedAt: String?,
    val confirmedByUser: User?,
    val order: Order? = null
)

data class Order(
    val id: String,
    val orderNumber: String,
    val customerName: String?,
    val orderType: String, // DINE_IN, TAKE_AWAY
    val tableId: String?,
    val table: Table?,
    val paymentMethod: String, // QRIS, CASH
    val paymentStatus: String, // PENDING, PAID, CANCELLED
    val orderStatus: String, // MENUNGGU_PEMBAYARAN, MENUNGGU_DIPROSES, DIPROSES, SIAP_DISAJIKAN, SELESAI, DIBATALKAN
    val subtotal: Int,
    val total: Int,
    val notes: String?,
    val createdAt: String,
    val orderDetails: List<OrderDetail> = emptyList(),
    val payments: List<Payment> = emptyList()
)

data class OrderItemPayload(
    val productId: String,
    val quantity: Int,
    val notes: String? = null
)

data class CreateOrderPayload(
    val customerName: String? = null,
    val orderType: String,
    val tableNumber: String? = null,
    val tableId: String? = null,
    val paymentMethod: String,
    val notes: String? = null,
    val items: List<OrderItemPayload>,
    val paymentStatus: String? = null
)

data class UpdateOrderStatusRequest(
    val orderStatus: String
)

data class ConfirmPaymentRequest(
    val orderId: String,
    val notes: String? = null
)

data class Expense(
    val id: String,
    val description: String,
    val amount: Int,
    val category: String,
    val date: String,
    val notes: String?
)

data class CreateExpenseRequest(
    val description: String,
    val amount: Int,
    val category: String,
    val notes: String? = null
)

data class DashboardSummary(
    val todayRevenue: Int,
    val todayTransactionCount: Int,
    val pendingPaymentsCount: Int,
    val activeOrdersCount: Int,
    val completedOrdersTodayCount: Int
)

data class FinancialReport(
    val period: String,
    val transactionCount: Int,
    val totalRevenue: Int,
    val totalExpenses: Int,
    val operatingSurplus: Int,
    val note: String,
    val expenses: List<Expense> = emptyList()
)

data class ReceiptStoreInfo(
    val name: String,
    val address: String,
    val phone: String
)

data class ReceiptItem(
    val productName: String,
    val price: Int,
    val quantity: Int,
    val subtotal: Int,
    val notes: String?
)

data class ReceiptInfo(
    val storeInfo: ReceiptStoreInfo,
    val orderNumber: String,
    val dateTime: String,
    val customerName: String?,
    val orderType: String,
    val tableName: String?,
    val paymentMethod: String,
    val paymentStatus: String,
    val items: List<ReceiptItem>,
    val total: Int
)
