package com.kuliner.msme.admin.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kuliner.msme.admin.data.api.RetrofitClient
import com.kuliner.msme.admin.data.model.ConfirmPaymentRequest
import com.kuliner.msme.admin.data.model.Order
import com.kuliner.msme.admin.data.model.Payment
import com.kuliner.msme.admin.ui.components.*
import com.kuliner.msme.admin.ui.theme.*
import com.kuliner.msme.admin.ui.util.friendlyUserName
import kotlinx.coroutines.launch

@Composable
fun PaymentsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf(0) } // 0 = Unpaid, 1 = History
    var unpaidOrders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var paymentHistory by remember { mutableStateOf<List<Payment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf<String?>(null) }
    var isErrorMessage by remember { mutableStateOf(false) }

    fun loadData() {
        isLoading = true
        message = null
        isErrorMessage = false
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
                    isErrorMessage = false
                    message = "Pembayaran berhasil dikonfirmasi LUNAS!"
                    loadData()
                } else {
                    isErrorMessage = true
                    message = "Gagal mengonfirmasi. Pembayaran mungkin sudah terverifikasi."
                }
            } catch (e: Exception) {
                isErrorMessage = true
                message = "Terjadi kesalahan koneksi"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AromaCream)
    ) {
        // Segmented Tabs + Refresh Action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                color = AromaCreamTrack,
                border = BorderStroke(1.dp, AromaBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val isUnpaid = selectedTab == 0
                    Surface(
                        onClick = { selectedTab = 0 },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isUnpaid) AromaTerracotta else Color.Transparent
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "Menunggu Bayar",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUnpaid) AromaSurface else AromaCharcoal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    val isHistory = selectedTab == 1
                    Surface(
                        onClick = { selectedTab = 1 },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isHistory) AromaTerracotta else Color.Transparent
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "Riwayat Lunas",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isHistory) AromaSurface else AromaCharcoal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Surface(
                onClick = { loadData() },
                modifier = Modifier.size(46.dp),
                shape = RoundedCornerShape(12.dp),
                color = AromaSurface,
                border = BorderStroke(1.dp, AromaBorder)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = AromaCharcoal,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (message != null) {
            AromaFeedbackBanner(
                message = message!!,
                isError = isErrorMessage,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        if (isLoading) {
            AromaLoadingState(message = "Memuat data pembayaran...")
        } else if (selectedTab == 0) {
            if (unpaidOrders.isEmpty()) {
                AromaEmptyState(
                    icon = Icons.Default.CheckCircle,
                    title = "Semua pesanan telah lunas",
                    subtitle = "Tidak ada pembayaran yang menunggu konfirmasi saat ini.",
                    iconBackground = AromaSuccessSoft,
                    iconTint = AromaSuccess
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(unpaidOrders) { order ->
                        AromaCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = order.orderNumber,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = AromaCharcoal,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        AromaTag(
                                            label = order.paymentMethod,
                                            backgroundColor = AromaTerracottaSoft,
                                            textColor = AromaTerracotta,
                                            borderColor = AromaTerracottaBorder
                                        )
                                        AromaStatusBadge(visual = paymentStatusVisual("PENDING"))
                                    }
                                }

                                Text(
                                    text = "${order.customerName ?: "Pelanggan"} • ${
                                        if (order.orderType == "DINE_IN") {
                                            "Dine In (${order.table?.tableNumber ?: "-"})"
                                        } else {
                                            "Take Away"
                                        }
                                    }",
                                    fontSize = 12.sp,
                                    color = AromaTextMuted
                                )

                                Text(
                                    text = order.orderDetails.joinToString(", ") { "${it.quantity}× ${it.productName}" },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AromaCharcoal,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                HorizontalDivider(color = AromaDivider, thickness = 1.dp)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Total Tagihan",
                                            fontSize = 11.sp,
                                            color = AromaTextMuted
                                        )
                                        Text(
                                            text = formatRupiah(order.total),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 16.sp,
                                            color = AromaCharcoal,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Button(
                                        onClick = { handleConfirmPayment(order.id) },
                                        modifier = Modifier.height(44.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = AromaTerracotta,
                                            contentColor = AromaSurface
                                        ),
                                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Konfirmasi Lunas",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (paymentHistory.isEmpty()) {
                AromaEmptyState(
                    icon = Icons.Default.Payments,
                    title = "Belum ada riwayat pembayaran",
                    subtitle = "Pembayaran yang telah diverifikasi akan tercatat di sini."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(paymentHistory) { payment ->
                        AromaCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = payment.order?.orderNumber ?: "ORDER",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = AromaCharcoal
                                    )
                                    AromaStatusBadge(visual = paymentStatusVisual("PAID"))
                                }

                                Text(
                                    text = "Metode ${payment.paymentMethod} • Diverifikasi oleh ${
                                        friendlyUserName(payment.confirmedByUser?.name, fallback = "Kasir")
                                    }",
                                    fontSize = 12.sp,
                                    color = AromaTextMuted
                                )

                                HorizontalDivider(color = AromaDivider, thickness = 1.dp)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Nominal Diterima",
                                        fontSize = 11.sp,
                                        color = AromaTextMuted
                                    )
                                    Text(
                                        text = formatRupiah(payment.amount),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp,
                                        color = AromaSuccess
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
