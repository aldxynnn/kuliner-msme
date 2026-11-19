package com.kuliner.msme.admin.notification

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** Sinyal dalam proses aplikasi saat ada pesanan baru, supaya layar pesanan ikut dimuat ulang. */
object OrderAlerts {
    private val _newOrders = MutableSharedFlow<String>(extraBufferCapacity = 16)
    val newOrders = _newOrders.asSharedFlow()

    fun emit(orderId: String) {
        _newOrders.tryEmit(orderId)
    }
}
