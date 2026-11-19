package com.kuliner.msme.admin.notification

import android.content.Context
import com.kuliner.msme.admin.data.api.RetrofitClient
import com.kuliner.msme.admin.data.model.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import java.text.NumberFormat
import java.util.Locale

/**
 * Pusat notifikasi dalam aplikasi.
 *
 * Notifikasi dibangun dari data pesanan yang benar-benar ada di backend:
 * - Pesanan dengan pembayaran PENDING  -> notifikasi konfirmasi pembayaran.
 * - Pesanan lunas yang belum diproses  -> notifikasi pesanan baru.
 * - Pesanan yang siap disajikan        -> notifikasi siap disajikan.
 *
 * Notifikasi otomatis hilang saat pekerjaannya selesai (mis. pembayaran
 * sudah dikonfirmasi), sehingga jumlah badge selalu mencerminkan beban kerja.
 */
object NotificationCenter {
    private const val ROUTE_ORDERS = "orders"
    private const val ROUTE_PAYMENTS = "payments"

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val mutex = Mutex()
    private var store: NotificationStore? = null

    fun init(context: Context) {
        if (store == null) {
            val created = NotificationStore(context.applicationContext)
            store = created
            _notifications.value = created.load().sortedByDescending { it.createdAt }
        }
    }

    /** Mengambil pesanan terbaru dan menyusun ulang daftar notifikasi. */
    suspend fun refresh(context: Context) {
        init(context)
        if (!mutex.tryLock()) return
        try {
            val response = RetrofitClient.getApiService(context).getOrders()
            if (!response.isSuccessful) return
            val orders = response.body().orEmpty()

            val previousByKey = _notifications.value.associateBy { it.id }
            val derived = mutableListOf<AppNotification>()

            orders.forEach { order ->
                if (order.orderStatus == "DIBATALKAN" || order.paymentStatus == "CANCELLED") {
                    return@forEach
                }
                when {
                    order.paymentStatus == "PENDING" -> derived += paymentNotification(order)
                    order.orderStatus == "MENUNGGU_DIPROSES" -> derived += newOrderNotification(order)
                    order.orderStatus == "SIAP_DISAJIKAN" -> derived += readyNotification(order)
                }
            }

            val merged = derived.map { item ->
                val previous = previousByKey[item.id]
                if (previous != null) {
                    item.copy(isRead = previous.isRead, createdAt = previous.createdAt)
                } else {
                    item
                }
            }.sortedByDescending { it.createdAt }

            if (merged != _notifications.value) {
                _notifications.value = merged
                store?.save(merged)
            }
        } catch (_: Exception) {
            // Jaringan bermasalah: pertahankan notifikasi terakhir yang diketahui.
        } finally {
            mutex.unlock()
        }
    }

    fun markAllRead(context: Context) {
        init(context)
        val current = _notifications.value
        if (current.none { !it.isRead }) return
        val updated = current.map { it.copy(isRead = true) }
        _notifications.value = updated
        store?.save(updated)
    }

    fun markRead(context: Context, id: String) {
        init(context)
        val current = _notifications.value
        if (current.none { it.id == id && !it.isRead }) return
        val updated = current.map { if (it.id == id) it.copy(isRead = true) else it }
        _notifications.value = updated
        store?.save(updated)
    }

    fun clear(context: Context) {
        init(context)
        _notifications.value = emptyList()
        store?.save(emptyList())
    }

    fun unreadForRoute(items: List<AppNotification>, route: String): Int =
        items.count { it.route == route && !it.isRead }

    private fun paymentNotification(order: Order): AppNotification {
        val customer = order.customerName?.takeIf { it.isNotBlank() } ?: "Pelanggan"
        val place = when {
            order.orderType == "DINE_IN" && order.table != null -> "$customer · Meja ${order.table.tableNumber}"
            order.orderType == "TAKE_AWAY" -> "$customer · Bawa pulang"
            else -> customer
        }
        return AppNotification(
            id = "payment:${order.id}",
            type = NotificationType.PAYMENT,
            title = "Pembayaran menunggu konfirmasi",
            message = "${order.orderNumber} · ${formatRp(order.total)} · $place",
            route = ROUTE_PAYMENTS,
            orderId = order.id,
            createdAt = order.createdAt
        )
    }

    private fun newOrderNotification(order: Order): AppNotification {
        val place = when {
            order.orderType == "DINE_IN" && order.table != null -> "Meja ${order.table.tableNumber}"
            order.orderType == "TAKE_AWAY" -> "Bawa pulang"
            else -> "Pesanan"
        }
        return AppNotification(
            id = "order:${order.id}",
            type = NotificationType.NEW_ORDER,
            title = "Pesanan baru ${order.orderNumber}",
            message = "$place · ${itemsSummary(order)} · ${formatRp(order.total)}",
            route = ROUTE_ORDERS,
            orderId = order.id,
            createdAt = order.createdAt
        )
    }

    private fun readyNotification(order: Order): AppNotification {
        val place = when {
            order.orderType == "DINE_IN" && order.table != null -> " untuk Meja ${order.table.tableNumber}"
            order.orderType == "TAKE_AWAY" -> " untuk dibawa pulang"
            else -> ""
        }
        return AppNotification(
            id = "ready:${order.id}",
            type = NotificationType.READY_TO_SERVE,
            title = "Pesanan ${order.orderNumber} siap disajikan",
            message = "Pesanan$place sudah selesai dimasak dan siap diantar ke pelanggan.",
            route = ROUTE_ORDERS,
            orderId = order.id,
            createdAt = order.createdAt
        )
    }

    private fun itemsSummary(order: Order): String {
        val items = order.orderDetails.take(3)
            .joinToString(", ") { "${it.quantity}x ${it.productName}" }
        val remaining = order.orderDetails.size - 3
        return if (remaining > 0) "$items, +$remaining lainnya" else items
    }

    private fun formatRp(amount: Int): String {
        val format = NumberFormat.getNumberInstance(Locale("id", "ID"))
        return "Rp${format.format(amount)}"
    }
}
