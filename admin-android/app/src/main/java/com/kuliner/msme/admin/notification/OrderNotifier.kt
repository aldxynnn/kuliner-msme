package com.kuliner.msme.admin.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.kuliner.msme.admin.MainActivity
import com.kuliner.msme.admin.R
import com.kuliner.msme.admin.data.model.Order
import java.text.NumberFormat
import java.util.Locale

object OrderNotifier {
    const val CHANNEL_ORDERS = "pesanan_masuk"
    const val CHANNEL_MONITOR = "pantau_pesanan"
    const val MONITOR_NOTIFICATION_ID = 1001

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java)

        val orders = NotificationChannel(
            CHANNEL_ORDERS,
            "Pesanan masuk",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Memberitahu kasir saat ada pesanan baru."
            enableVibration(true)
        }

        val monitor = NotificationChannel(
            CHANNEL_MONITOR,
            "Pemantauan pesanan",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Status pemantauan pesanan saat aplikasi berjalan di latar."
            setShowBadge(false)
        }

        manager.createNotificationChannel(orders)
        manager.createNotificationChannel(monitor)
    }

    fun monitorNotification(context: Context): android.app.Notification {
        ensureChannels(context)
        return NotificationCompat.Builder(context, CHANNEL_MONITOR)
            .setSmallIcon(R.drawable.ic_stat_order)
            .setContentTitle("AROMA POS")
            .setContentText("Memantau pesanan masuk")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(openOrdersIntent(context, requestCode = 0))
            .build()
    }

    fun showNewOrder(context: Context, order: Order) {
        ensureChannels(context)
        val manager = context.getSystemService(NotificationManager::class.java)
        val notification = NotificationCompat.Builder(context, CHANNEL_ORDERS)
            .setSmallIcon(R.drawable.ic_stat_order)
            .setContentTitle("Pesanan baru ${order.orderNumber}")
            .setContentText(summary(order))
            .setStyle(NotificationCompat.BigTextStyle().bigText(summary(order)))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openOrdersIntent(context, requestCode = order.id.hashCode()))
            .build()

        manager.notify(order.id.hashCode(), notification)
    }

    private fun summary(order: Order): String {
        val type = when (order.orderType) {
            "DINE_IN" -> "Makan di tempat"
            "TAKE_AWAY" -> "Bawa pulang"
            else -> order.orderType
        }
        val table = order.table?.tableNumber?.let { " · Meja $it" }.orEmpty()
        val customer = order.customerName?.takeIf { it.isNotBlank() }?.let { " · $it" }.orEmpty()
        val payment = when (order.paymentStatus) {
            "PAID" -> "Lunas"
            "PENDING" -> "Menunggu pembayaran"
            else -> order.paymentStatus
        }
        return "$type$table$customer · $payment · ${formatRp(order.total)}"
    }

    private fun formatRp(amount: Int): String {
        val formatted = NumberFormat.getNumberInstance(Locale("id", "ID")).format(amount)
        return "Rp$formatted"
    }

    private fun openOrdersIntent(context: Context, requestCode: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_OPEN_ORDERS, true)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getActivity(context, requestCode, intent, flags)
    }
}
