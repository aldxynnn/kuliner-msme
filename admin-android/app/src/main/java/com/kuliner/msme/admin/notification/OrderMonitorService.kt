package com.kuliner.msme.admin.notification

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import com.kuliner.msme.admin.data.api.RetrofitClient
import com.kuliner.msme.admin.data.api.TokenManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class OrderMonitorService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var pollJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = OrderNotifier.monitorNotification(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                OrderNotifier.MONITOR_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(OrderNotifier.MONITOR_NOTIFICATION_ID, notification)
        }

        if (TokenManager(this).getToken().isNullOrBlank()) {
            stopSelf()
            return START_NOT_STICKY
        }

        if (pollJob == null) {
            pollJob = scope.launch { pollLoop() }
        }
        return START_STICKY
    }

    private suspend fun pollLoop() {
        val store = SeenOrderStore(this)
        val api = RetrofitClient.getApiService(this)
        while (scope.isActive) {
            if (TokenManager(this).getToken().isNullOrBlank()) {
                stopSelf()
                return
            }
            try {
                val response = api.getOrders()
                val orders = response.body().orEmpty()
                if (response.isSuccessful) {
                    val ids = orders.map { it.id }.toSet()
                    if (!store.isSeeded()) {
                        store.seed(ids)
                    } else {
                        val known = store.knownIds()
                        val fresh = orders.filter { it.id !in known }
                        fresh.forEach { order ->
                            OrderNotifier.showNewOrder(this, order)
                            OrderAlerts.emit(order.id)
                        }
                        if (fresh.isNotEmpty() || ids != known) {
                            store.remember(ids)
                        }
                    }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                // Jaringan gagal: coba lagi pada siklus berikutnya.
            }
            delay(POLL_INTERVAL_MS)
        }
    }

    override fun onDestroy() {
        pollJob?.cancel()
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val POLL_INTERVAL_MS = 15_000L

        fun start(context: Context) {
            val appContext = context.applicationContext
            if (TokenManager(appContext).getToken().isNullOrBlank()) return
            val intent = Intent(appContext, OrderMonitorService::class.java)
            androidx.core.content.ContextCompat.startForegroundService(appContext, intent)
        }

        fun stop(context: Context) {
            context.applicationContext.stopService(
                Intent(context.applicationContext, OrderMonitorService::class.java)
            )
        }
    }
}
