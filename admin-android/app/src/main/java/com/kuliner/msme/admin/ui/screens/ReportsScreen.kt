package com.kuliner.msme.admin.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Assessment
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
import com.kuliner.msme.admin.data.model.FinancialReport
import com.kuliner.msme.admin.ui.components.*
import com.kuliner.msme.admin.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ReportsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var period by remember { mutableStateOf("daily") } // "daily" or "monthly"
    var report by remember { mutableStateOf<FinancialReport?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    fun loadData() {
        isLoading = true
        scope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                val res = api.getFinancialReport(period = period)
                if (res.isSuccessful && res.body() != null) {
                    report = res.body()!!
                }
            } catch (e: Exception) {
                // Ignore
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(period) {
        loadData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AromaCream)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Segmented Period Selector + Refresh
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                    val isDaily = period == "daily"
                    Surface(
                        onClick = { period = "daily" },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isDaily) AromaTerracotta else Color.Transparent
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "Harian",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isDaily) AromaSurface else AromaCharcoal
                            )
                        }
                    }

                    val isMonthly = period == "monthly"
                    Surface(
                        onClick = { period = "monthly" },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isMonthly) AromaTerracotta else Color.Transparent
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "Bulanan",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isMonthly) AromaSurface else AromaCharcoal
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

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentAlignment = Alignment.Center
            ) {
                AromaLoadingState(message = "Menghitung laporan keuangan...")
            }
        } else if (report == null) {
            AromaEmptyState(
                icon = Icons.Default.Assessment,
                title = "Laporan belum tersedia",
                subtitle = "Tekan tombol muat ulang untuk mengambil data laporan terbaru."
            )
        } else {
            val r = report!!
            val isNegative = r.operatingSurplus < 0

            // Operating Surplus Hero Panel (Solid Brand / Semantic Color)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = if (isNegative) AromaError else AromaTerracotta
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "SURPLUS OPERASIONAL • ${if (r.period == "daily") "HARI INI" else "BULAN INI"}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AromaSurface.copy(alpha = 0.85f),
                        letterSpacing = 0.7.sp
                    )

                    Text(
                        text = formatRupiah(r.operatingSurplus),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp,
                        color = AromaSurface
                    )

                    HorizontalDivider(color = AromaSurface.copy(alpha = 0.18f), thickness = 1.dp)

                    Text(
                        text = r.note,
                        fontSize = 12.sp,
                        color = AromaSurface.copy(alpha = 0.92f),
                        lineHeight = 18.sp
                    )
                }
            }

            // Breakdown Cards (Revenue vs Expenses)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AromaCard(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(34.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = AromaSuccessSoft
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = AromaSuccess,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text(
                            text = "Omzet Lunas",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AromaTextMuted
                        )
                        Text(
                            text = formatRupiah(r.totalRevenue),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = AromaSuccess,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${r.transactionCount} transaksi",
                            fontSize = 11.sp,
                            color = AromaTextSecondary
                        )
                    }
                }

                AromaCard(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(34.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = AromaErrorSoft
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.AutoMirrored.Filled.TrendingDown,
                                    contentDescription = null,
                                    tint = AromaError,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text(
                            text = "Pengeluaran",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AromaTextMuted
                        )
                        Text(
                            text = formatRupiah(r.totalExpenses),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = AromaError,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${r.expenses.size} catatan biaya",
                            fontSize = 11.sp,
                            color = AromaTextSecondary
                        )
                    }
                }
            }

            // Period Expenses Breakdown List (if present in report payload)
            if (r.expenses.isNotEmpty()) {
                AromaSectionHeader(
                    title = "Rincian Pengeluaran Periode Ini",
                    subtitle = "${r.expenses.size} transaksi tercatat"
                )

                AromaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        r.expenses.forEachIndexed { idx, exp ->
                            if (idx > 0) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = AromaDivider
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = exp.description,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AromaCharcoal
                                    )
                                    Text(
                                        text = "${exp.category} • ${exp.date.take(10)}",
                                        fontSize = 11.sp,
                                        color = AromaTextMuted
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = formatRupiah(exp.amount),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AromaError
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
