package com.kuliner.msme.admin.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kuliner.msme.admin.data.api.RetrofitClient
import com.kuliner.msme.admin.data.model.FinancialReport
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

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

    fun formatRp(amount: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        format.maximumFractionDigits = 0
        return format.format(amount)
    }

    val primaryGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFE25822), Color(0xFFF27B3D))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF8F5))
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Laporan Keuangan", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF2B211D))
                Text("Surplus operasional resto AROMA POS", fontSize = 11.sp, color = Color(0xFF81766F))
            }

            Surface(
                onClick = { loadData() },
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color(0xFF2B211D), modifier = Modifier.size(20.dp))
                }
            }
        }

        // Period Toggle Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val isDaily = period == "daily"
            Surface(
                onClick = { period = "daily" },
                modifier = Modifier.weight(1f).height(42.dp),
                shape = RoundedCornerShape(14.dp),
                color = if (isDaily) Color(0xFFE25822) else Color.White,
                shadowElevation = if (isDaily) 2.dp else 0.dp,
                border = if (isDaily) null else BorderStroke(1.dp, Color(0xFFE9E1D9))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "Laporan Harian",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isDaily) Color.White else Color(0xFF2B211D)
                    )
                }
            }

            val isMonthly = period == "monthly"
            Surface(
                onClick = { period = "monthly" },
                modifier = Modifier.weight(1f).height(42.dp),
                shape = RoundedCornerShape(14.dp),
                color = if (isMonthly) Color(0xFFE25822) else Color.White,
                shadowElevation = if (isMonthly) 2.dp else 0.dp,
                border = if (isMonthly) null else BorderStroke(1.dp, Color(0xFFE9E1D9))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "Laporan Bulanan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isMonthly) Color.White else Color(0xFF2B211D)
                    )
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFE25822))
            }
        } else if (report != null) {
            val r = report!!

            // Hero Operating Surplus Banner Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 6.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(primaryGradient)
                        .padding(22.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "SURPLUS OPERASIONAL (${if (r.period == "daily") "HARI INI" else "BULAN INI"})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.9f),
                            letterSpacing = 0.5.sp
                        )

                        Text(
                            text = formatRp(r.operatingSurplus),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = r.note,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.95f)
                        )
                    }
                }
            }

            // Breakdown Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Total Omzet Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, Color(0xFFE9E1D9))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total Omzet Lunas", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF81766F))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(formatRp(r.totalRevenue), fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFFE25822))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("${r.transactionCount} Transaksi", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF81766F))
                    }
                }

                // Total Pengeluaran Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, Color(0xFFE9E1D9))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total Pengeluaran", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF81766F))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(formatRp(r.totalExpenses), fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFFC94136))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("${r.expenses.size} Catatan Biaya", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF81766F))
                    }
                }
            }
        }
    }
}
