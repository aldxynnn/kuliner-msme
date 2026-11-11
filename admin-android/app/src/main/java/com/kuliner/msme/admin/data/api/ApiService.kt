package com.kuliner.msme.admin.data.api

import com.kuliner.msme.admin.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("auth/me")
    suspend fun getMe(): Response<User>

    // Categories
    @GET("categories")
    suspend fun getCategories(): Response<List<Category>>

    @POST("categories")
    suspend fun createCategory(@Body request: CreateCategoryRequest): Response<Category>

    @PUT("categories/{id}")
    suspend fun updateCategory(@Path("id") id: String, @Body request: CreateCategoryRequest): Response<Category>

    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: String): Response<Unit>

    // Products
    @GET("products")
    suspend fun getProducts(
        @Query("categoryId") categoryId: String? = null,
        @Query("search") search: String? = null
    ): Response<List<Product>>

    @POST("products")
    suspend fun createProduct(@Body request: CreateProductRequest): Response<Product>

    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: String, @Body request: CreateProductRequest): Response<Product>

    @PATCH("products/{id}/availability")
    suspend fun toggleAvailability(@Path("id") id: String, @Body request: ToggleAvailabilityRequest): Response<Product>

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: String): Response<Unit>

    // Tables
    @GET("tables")
    suspend fun getTables(): Response<List<Table>>

    @POST("tables")
    suspend fun createTable(@Body request: CreateTableRequest): Response<Table>

    @PUT("tables/{id}")
    suspend fun updateTable(@Path("id") id: String, @Body request: CreateTableRequest): Response<Table>

    @DELETE("tables/{id}")
    suspend fun deleteTable(@Path("id") id: String): Response<Unit>

    // Orders
    @POST("orders/cashier")
    suspend fun createCashierOrder(@Body payload: CreateOrderPayload): Response<Order>

    @GET("orders")
    suspend fun getOrders(
        @Query("status") status: String? = null,
        @Query("paymentStatus") paymentStatus: String? = null,
        @Query("date") date: String? = null
    ): Response<List<Order>>

    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") id: String): Response<Order>

    @PATCH("orders/{id}/status")
    suspend fun updateOrderStatus(
        @Path("id") id: String,
        @Body request: UpdateOrderStatusRequest
    ): Response<Order>

    @POST("orders/{id}/cancel")
    suspend fun cancelOrder(@Path("id") id: String): Response<Order>

    @GET("orders/{id}/receipt")
    suspend fun getReceipt(@Path("id") id: String): Response<ReceiptInfo>

    // Payments
    @POST("payments/confirm")
    suspend fun confirmPayment(@Body request: ConfirmPaymentRequest): Response<Any>

    @GET("payments/unpaid")
    suspend fun getUnpaidOrders(): Response<List<Order>>

    @GET("payments/history")
    suspend fun getPaymentHistory(): Response<List<Payment>>

    // Expenses
    @GET("expenses")
    suspend fun getExpenses(@Query("date") date: String? = null): Response<List<Expense>>

    @POST("expenses")
    suspend fun createExpense(@Body request: CreateExpenseRequest): Response<Expense>

    @PUT("expenses/{id}")
    suspend fun updateExpense(@Path("id") id: String, @Body request: CreateExpenseRequest): Response<Expense>

    @DELETE("expenses/{id}")
    suspend fun deleteExpense(@Path("id") id: String): Response<Unit>

    // Reports
    @GET("reports/dashboard")
    suspend fun getDashboardSummary(): Response<DashboardSummary>

    @GET("reports/financial")
    suspend fun getFinancialReport(
        @Query("period") period: String = "daily",
        @Query("date") date: String? = null
    ): Response<FinancialReport>
}
