package com.kuliner.msme.admin.notification

/**
 * Notifikasi dalam aplikasi yang diturunkan dari kondisi pesanan nyata
 * (belum dibayar, menunggu diproses, atau siap disajikan).
 */
data class AppNotification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val route: String,
    val orderId: String,
    val createdAt: String,
    val isRead: Boolean = false
)

enum class NotificationType {
    PAYMENT,
    NEW_ORDER,
    READY_TO_SERVE
}
