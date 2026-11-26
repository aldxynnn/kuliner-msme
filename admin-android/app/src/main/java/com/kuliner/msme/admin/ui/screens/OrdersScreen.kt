package com.kuliner.msme.admin.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kuliner.msme.admin.data.api.RetrofitClient
import com.kuliner.msme.admin.data.model.Order
import com.kuliner.msme.admin.data.model.ReceiptInfo
import com.kuliner.msme.admin.data.model.UpdateOrderStatusRequest
import com.kuliner.msme.admin.notification.OrderAlerts
import com.kuliner.msme.admin.ui.components.*
import com.kuliner.msme.admin.ui.theme.*
import kotlinx.coroutines.launch

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

    LaunchedEffect(Unit) {
        OrderAlerts.newOrders.collect {
            loadOrders()
        }
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
            .background(AromaCream)
    ) {
        // Horizontal Status Filter Rail
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(statusFilterList) { (statusVal, statusName) ->
                AromaFilterPill(
                    label = statusName,
                    selected = selectedStatusFilter == statusVal,
                    onClick = { selectedStatusFilter = statusVal }
                )
            }
        }

        if (isLoading) {
            AromaLoadingState(message = "Memuat daftar pesanan...")
        } else if (orders.isEmpty()) {
            AromaEmptyState(
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                title = "Tidak ada pesanan",
                subtitle = if (selectedStatusFilter == null) {
                    "Pesanan pelanggan maupun kasir POS akan muncul di sini."
                } else {
                    "Belum ada pesanan pada kategori status ini."
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 2.dp, bottom = 20.dp),
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

    // Kitchen Order Detail Dialog with Dark Roast Header & 5-Step Progress
    if (selectedOrderForDetails != null) {
        val ord = selectedOrderForDetails!!
        val steps = listOf(
            "MENUNGGU_PEMBAYARAN" to "Bayar",
            "MENUNGGU_DIPROSES" to "Antre",
            "DIPROSES" to "Masak",
            "SIAP_DISAJIKAN" to "Siap",
            "SELESAI" to "Selesai"
        )
        val currentIdx = steps.indexOfFirst { it.first == ord.orderStatus }

        AlertDialog(
            onDismissRequest = { selectedOrderForDetails = null },
            containerColor = AromaSurface,
            shape = RoundedCornerShape(18.dp),
            title = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = AromaTerracottaSoft,
                        border = BorderStroke(1.dp, AromaTerracottaBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "DETAIL TIKET DAPUR",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                color = AromaTerracotta
                            )
                            Text(
                                text = "#${ord.orderNumber}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AromaCharcoal
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                AromaStatusBadge(
                                    visual = orderStatusVisual(ord.orderStatus),
                                    showDot = true
                                )
                                AromaStatusBadge(visual = paymentStatusVisual(ord.paymentStatus))
                            }
                        }
                    }

                    if (ord.orderStatus != "DIBATALKAN" && currentIdx >= 0) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            steps.forEachIndexed { idx, (_, stepLabel) ->
                                val completed = idx < currentIdx || ord.orderStatus == "SELESAI"
                                val active = idx == currentIdx && ord.orderStatus != "SELESAI"
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .background(
                                                when {
                                                    completed -> AromaSuccess
                                                    active -> AromaTerracotta
                                                    else -> AromaCreamTrack
                                                },
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (completed) "✓" else "${idx + 1}",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (completed || active) AromaSurface else AromaTextMuted
                                        )
                                    }
                                    Spacer(Modifier.height(3.dp))
                                    Text(
                                        text = stepLabel,
                                        fontSize = 10.sp,
                                        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                        color = if (active) AromaTerracotta else AromaTextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = AromaCreamSoft,
                        border = BorderStroke(1.dp, AromaBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OrderMetaRow("Pelanggan", ord.customerName ?: "Pelanggan")
                            OrderMetaRow(
                                "Tipe Pesanan",
                                if (ord.orderType == "DINE_IN") {
                                    "Dine In (${ord.table?.tableNumber ?: "-"})"
                                } else {
                                    "Take Away"
                                }
                            )
                            OrderMetaRow(
                                "Pembayaran",
                                "${ord.paymentMethod} (${paymentStatusVisual(ord.paymentStatus).label})"
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Rincian Menu",
                            style = MaterialTheme.typography.labelLarge,
                            color = AromaCharcoal
                        )

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = AromaSurface,
                            border = BorderStroke(1.dp, AromaBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ord.orderDetails.forEachIndexed { idx, item ->
                                    if (idx > 0) {
                                        HorizontalDivider(color = AromaDivider, thickness = 1.dp)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "${item.quantity}× ${item.productName}",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = AromaCharcoal
                                            )
                                            if (!item.notes.isNullOrBlank()) {
                                                Text(
                                                    text = "Catatan: ${item.notes}",
                                                    fontSize = 11.sp,
                                                    color = AromaTextMuted
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = formatRupiah(item.subtotal),
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AromaCharcoal
                                        )
                                    }
                                }

                                AromaDashedDivider()

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Total",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AromaCharcoal
                                    )
                                    Text(
                                        text = formatRupiah(ord.total),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AromaTerracotta
                                    )
                                }
                            }
                        }
                    }

                    // Kitchen Workflow Actions
                    if (ord.paymentStatus == "PAID") {
                        if (ord.orderStatus in listOf("MENUNGGU_DIPROSES", "DIPROSES", "SIAP_DISAJIKAN")) {
                            Text(
                                text = "Tindakan Dapur",
                                style = MaterialTheme.typography.labelLarge,
                                color = AromaCharcoal
                            )
                        }

                        if (ord.orderStatus == "MENUNGGU_DIPROSES") {
                            Button(
                                onClick = { handleUpdateStatus(ord.id, "DIPROSES") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AromaTerracotta)
                            ) {
                                Text("Proses di Dapur", fontWeight = FontWeight.Bold)
                            }
                        }
                        if (ord.orderStatus == "DIPROSES") {
                            Button(
                                onClick = { handleUpdateStatus(ord.id, "SIAP_DISAJIKAN") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AromaTerracotta)
                            ) {
                                Text("Siap Disajikan", fontWeight = FontWeight.Bold)
                            }
                        }
                        if (ord.orderStatus == "SIAP_DISAJIKAN") {
                            Button(
                                onClick = { handleUpdateStatus(ord.id, "SELESAI") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AromaSuccess)
                            ) {
                                Text("Selesaikan Pesanan", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        AromaFeedbackBanner(
                            message = "Pesanan belum lunas. Konfirmasi pembayaran kasir terlebih dahulu sebelum diproses dapur.",
                            isError = true
                        )
                        OutlinedButton(
                            onClick = { handleCancelOrder(ord.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, AromaErrorBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AromaError)
                        ) {
                            Text("Batalkan Pesanan", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedOrderForDetails = null }) {
                    Text("Tutup", fontWeight = FontWeight.Bold, color = AromaCharcoal)
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
private fun OrderMetaRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = AromaTextMuted)
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = AromaCharcoal
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OrderCardItem(
    order: Order,
    onSelect: () -> Unit,
    onPrintReceipt: () -> Unit
) {
    val orderStatus = orderStatusVisual(order.orderStatus)
    val payStatus = paymentStatusVisual(order.paymentStatus)

    AromaCard(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Left Vertical Kitchen Status Rail
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(orderStatus.text)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = order.orderNumber,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AromaCharcoal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AromaStatusBadge(visual = orderStatus, showDot = true)
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AromaTag(
                        label = if (order.orderType == "DINE_IN") {
                            "Dine In · ${order.table?.tableNumber ?: "-"}"
                        } else {
                            "Take Away"
                        },
                        backgroundColor = AromaCreamSoft,
                        textColor = AromaCharcoal,
                        borderColor = AromaBorder
                    )
                    AromaTag(
                        label = "${order.paymentMethod} · ${payStatus.label}",
                        backgroundColor = payStatus.bg,
                        textColor = payStatus.text,
                        borderColor = payStatus.border
                    )
                }

                Text(
                    text = order.orderDetails.joinToString(", ") { "${it.quantity}× ${it.productName}" },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = AromaTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                AromaDashedDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = order.customerName?.takeIf { it.isNotBlank() } ?: "Pelanggan",
                            fontSize = 11.sp,
                            color = AromaTextMuted
                        )
                        Text(
                            text = formatRupiah(order.total),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = AromaCharcoal
                        )
                    }

                    OutlinedButton(
                        onClick = onPrintReceipt,
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, AromaTerracottaBorder),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = AromaTerracottaSoft,
                            contentColor = AromaTerracotta
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    ) {
                        Icon(
                            Icons.Default.Receipt,
                            contentDescription = "Struk",
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Struk", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
