package com.kuliner.msme.admin.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF8F5))
    ) {
        // Status filter pills (Directly under top header, no duplicated title banner)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(statusFilterList) { (statusVal, statusName) ->
                val isSelected = selectedStatusFilter == statusVal
                Surface(
                    onClick = { selectedStatusFilter = statusVal },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) Color(0xFFE25822) else Color.White,
                    shadowElevation = if (isSelected) 2.dp else 0.dp,
                    border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE9E1D9))
                ) {
                    Text(
                        text = statusName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Color(0xFF2B211D),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFE25822))
            }
        } else if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tidak ada pesanan", color = Color(0xFF81766F), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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

    // Order Details BottomSheet / Dialog
    if (selectedOrderForDetails != null) {
        val ord = selectedOrderForDetails!!
        AlertDialog(
            onDismissRequest = { selectedOrderForDetails = null },
            title = {
                Text("Detail Pesanan #${ord.orderNumber}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF2B211D))
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFFF0E8),
                        border = BorderStroke(1.dp, Color(0xFFE25822).copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text("Pelanggan: ${ord.customerName ?: "Pelanggan"}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2B211D))
                            Text(
                                "Tipe: ${if (ord.orderType == "DINE_IN") "Dine In (${ord.table?.tableNumber ?: "-"})" else "Take Away"}",
                                fontSize = 12.sp,
                                color = Color(0xFF2B211D)
                            )
                            Text("Metode Pembayaran: ${ord.paymentMethod} (${ord.paymentStatus})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE25822))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Rincian Item Menu:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF2B211D))
                    ord.orderDetails.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("• ${item.quantity}x ${item.productName}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2B211D))
                                if (!item.notes.isNullOrBlank()) {
                                    Text("  Note: ${item.notes}", fontSize = 11.sp, color = Color(0xFF81766F))
                                }
                            }
                            Text(formatRp(item.subtotal), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2B211D))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (ord.paymentStatus == "PAID") {
                        Text("Update Status Dapur:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF2B211D))
                        Spacer(modifier = Modifier.height(4.dp))

                        if (ord.orderStatus == "MENUNGGU_DIPROSES") {
                            Button(
                                onClick = { handleUpdateStatus(ord.id, "DIPROSES") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE25822))
                            ) {
                                Text("Proses di Dapur", fontWeight = FontWeight.Bold)
                            }
                        }
                        if (ord.orderStatus == "DIPROSES") {
                            Button(
                                onClick = { handleUpdateStatus(ord.id, "SIAP_DISAJIKAN") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE25822))
                            ) {
                                Text("Siap Disajikan", fontWeight = FontWeight.Bold)
                            }
                        }
                        if (ord.orderStatus == "SIAP_DISAJIKAN") {
                            Button(
                                onClick = { handleUpdateStatus(ord.id, "SELESAI") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE25822))
                            ) {
                                Text("Selesaikan Pesanan", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFEE2E2)
                        ) {
                            Text(
                                "Pesanan belum lunas. Konfirmasi pembayaran kasir terlebih dahulu.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF991B1B),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedButton(
                            onClick = { handleCancelOrder(ord.id) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC94136))
                        ) {
                            Text("Batalkan Pesanan", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedOrderForDetails = null }) {
                    Text("Tutup", fontWeight = FontWeight.Bold, color = Color(0xFFE25822))
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

    val (badgeText, badgeBg, badgeTextColor) = when (order.orderStatus) {
        "MENUNGGU_PEMBAYARAN" -> Triple("Belum Bayar", Color(0xFFFEF3C7), Color(0xFFD97706))
        "MENUNGGU_DIPROSES" -> Triple("Masuk Antrean", Color(0xFFFFF0E8), Color(0xFFE25822))
        "DIPROSES" -> Triple("Sedang Dimasak", Color(0xFFFFEDD5), Color(0xFFEA580C))
        "SIAP_DISAJIKAN" -> Triple("Siap Disajikan", Color(0xFFE0F2FE), Color(0xFF0284C7))
        "SELESAI" -> Triple("Selesai", Color(0xFFE6F4EA), Color(0xFF166534))
        else -> Triple(order.orderStatus, Color(0xFFF3EFEA), Color(0xFF2B211D))
    }

    Surface(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFE9E1D9))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(order.orderNumber, fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color(0xFF2B211D))
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Color(0xFFFFF0E8),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (order.orderType == "DINE_IN") "Dine In (${order.table?.tableNumber ?: "-"})" else "Take Away",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE25822),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeTextColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = order.orderDetails.joinToString(", ") { "${it.quantity}x ${it.productName}" },
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2B211D),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatRp(order.total),
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = Color(0xFFE25822)
                )

                Surface(
                    onClick = onPrintReceipt,
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = Color(0xFFFFF0E8)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Receipt, contentDescription = "Struk", tint = Color(0xFFE25822), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
