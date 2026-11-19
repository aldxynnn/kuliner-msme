package com.kuliner.msme.admin.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
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
import com.kuliner.msme.admin.data.model.ConfirmPaymentRequest
import com.kuliner.msme.admin.data.model.Order
import com.kuliner.msme.admin.data.model.Payment
import com.kuliner.msme.admin.ui.util.friendlyUserName
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PaymentsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf(0) } // 0 = Unpaid, 1 = History
    var unpaidOrders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var paymentHistory by remember { mutableStateOf<List<Payment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf<String?>(null) }

    fun loadData() {
        isLoading = true
        message = null
        scope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                if (selectedTab == 0) {
                    val res = api.getUnpaidOrders()
                    if (res.isSuccessful && res.body() != null) unpaidOrders = res.body()!!
                } else {
                    val res = api.getPaymentHistory()
                    if (res.isSuccessful && res.body() != null) paymentHistory = res.body()!!
                }
            } catch (e: Exception) {
                // Ignore
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(selectedTab) {
        loadData()
    }

    fun handleConfirmPayment(orderId: String) {
        scope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                val res = api.confirmPayment(ConfirmPaymentRequest(orderId))
                if (res.isSuccessful) {
                    message = "Pembayaran berhasil dikonfirmasi LUNAS!"
                    loadData()
                } else {
                    message = "Gagal mengonfirmasi. Pembayaran mungkin sudah terverifikasi."
                }
            } catch (e: Exception) {
                message = "Terjadi kesalahan koneksi"
            }
        }
    }

    fun formatRp(amount: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        format.maximumFractionDigits = 0
        return format.format(amount)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF8F5))
    ) {
        // Tab Row with Refresh Action Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color(0xFFE25822),
                modifier = Modifier.weight(1f)
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text(
                        "Menunggu Pembayaran",
                        modifier = Modifier.padding(vertical = 12.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (selectedTab == 0) Color(0xFFE25822) else Color(0xFF81766F)
                    )
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text(
                        "Riwayat Pembayaran",
                        modifier = Modifier.padding(vertical = 12.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (selectedTab == 1) Color(0xFFE25822) else Color(0xFF81766F)
                    )
                }
            }

            IconButton(
                onClick = { loadData() },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color(0xFF81766F), modifier = Modifier.size(20.dp))
            }
        }

        if (message != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                color = Color(0xFFFFF0E8),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFE25822).copy(alpha = 0.3f))
            ) {
                Text(
                    text = message!!,
                    modifier = Modifier.padding(14.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE25822)
                )
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFE25822))
            }
        } else if (selectedTab == 0) {
            if (unpaidOrders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Semua pesanan telah lunas!", color = Color(0xFF81766F), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(unpaidOrders) { order ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White,
                            shadowElevation = 2.dp,
                            border = BorderStroke(1.dp, Color(0xFFE9E1D9))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(order.orderNumber, fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color(0xFF2B211D))
                                    Surface(
                                        color = Color(0xFFFFF0E8),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = order.paymentMethod,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = Color(0xFFE25822),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "${order.customerName ?: "Pelanggan"} • ${if (order.orderType == "DINE_IN") "Dine In (${order.table?.tableNumber ?: "-"})" else "Take Away"}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF81766F)
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = order.orderDetails.joinToString(", ") { "${it.quantity}x ${it.productName}" },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2B211D)
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        formatRp(order.total),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = Color(0xFFE25822)
                                    )

                                    Button(
                                        onClick = { handleConfirmPayment(order.id) },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE25822))
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Konfirmasi Lunas", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (paymentHistory.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada riwayat pembayaran", color = Color(0xFF81766F), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(paymentHistory) { payment ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White,
                            shadowElevation = 2.dp,
                            border = BorderStroke(1.dp, Color(0xFFE9E1D9))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(payment.order?.orderNumber ?: "ORDER", fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color(0xFF2B211D))
                                    Surface(
                                        color = Color(0xFFE6F4EA),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            "LUNAS",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = Color(0xFF166534),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Metode: ${payment.paymentMethod} • Diverifikasi: ${friendlyUserName(payment.confirmedByUser?.name, fallback = "Kasir")}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF81766F)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    formatRp(payment.amount),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = Color(0xFFE25822)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
