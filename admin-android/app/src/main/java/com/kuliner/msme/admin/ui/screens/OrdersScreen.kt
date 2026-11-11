package com.kuliner.msme.admin.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kuliner.msme.admin.data.api.RetrofitClient
import com.kuliner.msme.admin.data.model.Order
import com.kuliner.msme.admin.data.model.ReceiptInfo
import com.kuliner.msme.admin.data.model.UpdateOrderStatusRequest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun OrdersScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedStatusFilter by remember { mutableStateOf<String?>(null) }

    var selectedOrderForDetails by remember { mutableStateOf<Order?>(null) }
    var receiptInfoToPrint by remember { mutableStateOf<ReceiptInfo?>(null) }

    fun loadOrders() {
        isLoading = true
        scope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                val response = api.getOrders(status = selectedStatusFilter)
                if (response.isSuccessful && response.body() != null) {
                    orders = response.body()!!
                }
            } catch (e: Exception) {
                // Ignore
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(selectedStatusFilter) {
        loadOrders()
    }

    fun handleUpdateStatus(orderId: String, newStatus: String) {
        scope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                val response = api.updateOrderStatus(orderId, UpdateOrderStatusRequest(newStatus))
                if (response.isSuccessful) {
                    selectedOrderForDetails = null
                    loadOrders()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun handleCancelOrder(orderId: String) {
        scope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                val response = api.cancelOrder(orderId)
                if (response.isSuccessful) {
                    selectedOrderForDetails = null
                    loadOrders()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun handleLoadReceipt(orderId: String) {
        scope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                val response = api.getReceipt(orderId)
                if (response.isSuccessful && response.body() != null) {
                    receiptInfoToPrint = response.body()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun formatRp(amount: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        format.maximumFractionDigits = 0
        return format.format(amount)
    }

    val statusFilterList = listOf(
        null to "Semua",
        "MENUNGGU_PEMBAYARAN" to "Belum Bayar",
        "MENUNGGU_DIPROSES" to "Masuk Antrean",
        "DIPROSES" to "Sedang Dimasak",
        "SIAP_DISAJIKAN" to "Siap Disajikan",
        "SELESAI" to "Selesai",
        "DIBATALKAN" to "Dibatalkan"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Antrean Pesanan", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Kelola alur status pesanan AROMA POS", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            IconButton(onClick = { loadOrders() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }

        // Status filter pills
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(statusFilterList) { (statusVal, statusName) ->
                FilterChip(
                    selected = selectedStatusFilter == statusVal,
                    onClick = { selectedStatusFilter = statusVal },
                    label = { Text(statusName, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tidak ada pesanan", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(orders) { order ->
                    OrderCardItem(
                        order = order,
                        onSelect = { selectedOrderForDetails = order },
                        onPrintReceipt = { handleLoadReceipt(order.id) }
                    )
                }
            }
        }
    }

    // Order Details BottomSheet Dialog
    if (selectedOrderForDetails != null) {
        val ord = selectedOrderForDetails!!
        AlertDialog(
            onDismissRequest = { selectedOrderForDetails = null },
            title = {
                Text("Detail Pesanan #${ord.orderNumber}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Pelanggan: ${ord.customerName ?: "Pelanggan"}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text(
                        "Tipe: ${if (ord.orderType == "DINE_IN") "Dine In (${ord.table?.tableNumber ?: "-"})" else "Take Away"}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text("Metode Pembayaran: ${ord.paymentMethod} (${ord.paymentStatus})", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text("Status Pesanan: ${ord.orderStatus}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    Text("Rincian Item:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    ord.orderDetails.forEach { item ->
                        Text("• ${item.quantity}x ${item.productName} - ${formatRp(item.subtotal)}", fontSize = 12.sp)
                        if (!item.notes.isNullOrBlank()) {
                            Text("  Note: ${item.notes}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Aksi Status:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))

                    if (ord.paymentStatus == "PAID") {
                        if (ord.orderStatus == "MENUNGGU_DIPROSES") {
                            Button(
                                onClick = { handleUpdateStatus(ord.id, "DIPROSES") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Proses di Dapur")
                            }
                        }
                        if (ord.orderStatus == "DIPROSES") {
                            Button(
                                onClick = { handleUpdateStatus(ord.id, "SIAP_DISAJIKAN") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Siap Disajikan")
                            }
                        }
                        if (ord.orderStatus == "SIAP_DISAJIKAN") {
                            Button(
                                onClick = { handleUpdateStatus(ord.id, "SELESAI") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Selesaikan Pesanan")
                            }
                        }
                    } else {
                        Text(
                            "Pesanan belum lunas. Konfirmasi pembayaran terlebih dahulu.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedButton(
                            onClick = { handleCancelOrder(ord.id) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Batalkan Pesanan")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedOrderForDetails = null }) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (receiptInfoToPrint != null) {
        ReceiptDialog(
            receipt = receiptInfoToPrint,
            onDismiss = { receiptInfoToPrint = null }
        )
    }
}

@Composable
fun OrderCardItem(
    order: Order,
    onSelect: () -> Unit,
    onPrintReceipt: () -> Unit
) {
    fun formatRp(amount: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        format.maximumFractionDigits = 0
        return format.format(amount)
    }

    val (badgeText, badgeBg) = when (order.orderStatus) {
        "MENUNGGU_PEMBAYARAN" -> "Belum Bayar" to MaterialTheme.colorScheme.errorContainer
        "MENUNGGU_DIPROSES" -> "Masuk Antrean" to MaterialTheme.colorScheme.primaryContainer
        "DIPROSES" -> "Sedang Dimasak" to MaterialTheme.colorScheme.secondaryContainer
        "SIAP_DISAJIKAN" -> "Siap Disajikan" to MaterialTheme.colorScheme.primaryContainer
        "SELESAI" -> "Selesai" to MaterialTheme.colorScheme.surfaceVariant
        else -> order.orderStatus to MaterialTheme.colorScheme.surfaceVariant
    }

    Surface(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(order.orderNumber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${order.customerName ?: "Pelanggan"} • ${if (order.orderType == "DINE_IN") "Dine In (${order.table?.tableNumber ?: "-"})" else "Take Away"}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = order.orderDetails.joinToString(", ") { "${it.quantity}x ${it.productName}" },
                fontSize = 12.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatRp(order.total),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                IconButton(
                    onClick = onPrintReceipt,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.Receipt, contentDescription = "Struk", modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
