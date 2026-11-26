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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
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
        // Unified Period Switcher + Refresh
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AromaSegmentedControl(
                options = listOf(
                    "Harian" to null,
                    "Bulanan" to null
                ),
                selectedIndex = if (period == "daily") 0 else 1,
                onSelect = { index ->
                    period = if (index == 0) "daily" else "monthly"
                },
                modifier = Modifier.weight(1f)
            )

            Surface(
                onClick = { loadData() },
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(10.dp),
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
            val surplusColor = if (isNegative) AromaTerracottaLight else AromaSuccessLive

            // Deep Roast Executive Surplus Sheet
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = AromaCharcoal
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SURPLUS OPERASIONAL",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AromaSurface.copy(alpha = 0.7f),
                            letterSpacing = 1.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AromaInkSoft,
                            border = BorderStroke(1.dp, AromaInkDivider)
                        ) {
                            Text(
                                text = if (r.period == "daily") "Hari Ini" else "Bulan Ini",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AromaSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = formatRupiah(r.operatingSurplus),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 31.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.8).sp,
                        color = surplusColor
                    )

                    Text(
                        text = r.note,
                        fontSize = 12.sp,
                        color = AromaSurface.copy(alpha = 0.7f),
                        lineHeight = 18.sp
                    )
                }
            }

            // Omzet vs Pengeluaran Comparison Sheet with Real Ratio Bar
            val totalFlow = (r.totalRevenue + r.totalExpenses).coerceAtLeast(1)
            val revenueWeight = (r.totalRevenue.toFloat() / totalFlow.toFloat()).coerceIn(0f, 1f)
            val expenseWeight = (r.totalExpenses.toFloat() / totalFlow.toFloat()).coerceIn(0f, 1f)

            AromaCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 14.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "RINGKASAN ARUS KAS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AromaTextMuted,
                        letterSpacing = 0.9.sp
                    )

                    // Real Data-Driven Visual Proportion Strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(AromaCreamTrack)
                    ) {
                        if (r.totalRevenue > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(revenueWeight.coerceAtLeast(0.02f))
                                    .fillMaxHeight()
                                    .background(AromaSuccess)
                            )
                        }
                        if (r.totalExpenses > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(expenseWeight.coerceAtLeast(0.02f))
                                    .fillMaxHeight()
                                    .background(AromaError)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = AromaSuccess,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "Omzet Lunas",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AromaTextMuted
                                )
                            }
                            Text(
                                text = formatRupiah(r.totalRevenue),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = AromaCharcoal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${r.transactionCount} transaksi",
                                fontSize = 11.sp,
                                color = AromaTextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(AromaDivider)
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.TrendingDown,
                                    contentDescription = null,
                                    tint = AromaError,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "Pengeluaran",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AromaTextMuted
                                )
                            }
                            Text(
                                text = formatRupiah(r.totalExpenses),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
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
            }

            // Period Expenses Breakdown List
            if (r.expenses.isNotEmpty()) {
                AromaSectionHeader(
                    overline = "Rincian Biaya",
                    title = "Pengeluaran Periode Ini",
                    subtitle = "${r.expenses.size} transaksi tercatat"
                )

                AromaCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 12.dp
                ) {
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
                                        text = "${exp.category} · ${exp.date.take(10)}",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = AromaTextMuted
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = formatRupiah(exp.amount),
                                    fontFamily = FontFamily.Monospace,
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
