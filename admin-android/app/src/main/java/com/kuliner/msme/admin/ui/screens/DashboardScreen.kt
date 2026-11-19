package com.kuliner.msme.admin.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kuliner.msme.admin.R
import com.kuliner.msme.admin.data.api.RetrofitClient
import com.kuliner.msme.admin.data.model.DashboardSummary
import com.kuliner.msme.admin.data.model.User
import com.kuliner.msme.admin.ui.components.NotificationBellButton
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    onNavigateTo: (String) -> Unit,
    user: User? = null,
    onOpenNotifications: () -> Unit = {},
    unreadNotifications: Int = 0
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var summary by remember { mutableStateOf<DashboardSummary?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    fun loadData() {
        isLoading = true
        scope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                val response = api.getDashboardSummary()
                if (response.isSuccessful) {
                    summary = response.body()
                }
            } catch (e: Exception) {
                // Ignore
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
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
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Header bersih: brand di kiri, notifikasi di kanan.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_aroma),
                        contentDescription = "Logo AROMA",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp))
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "AROMA POS",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Color(0xFFE25822),
                    letterSpacing = (-0.5).sp
                )
            }

            NotificationBellButton(
                unreadCount = unreadNotifications,
                onClick = onOpenNotifications
            )
        }

        // Greeting Section
        Column {
            Text(
                text = "Selamat Datang,",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF81766F)
            )
            Text(
                text = "Halo, ${user?.name?.takeIf { it.isNotBlank() } ?: "Kasir AROMA"}!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF2B211D),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }

        // Hero Gradient Banner Card (Omzet Summary)
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
                        text = "Total Omzet Hari Ini",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = formatRp(summary?.todayRevenue ?: 0),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${summary?.todayTransactionCount ?: 0} Transaksi Lunas",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.95f)
                                )
                            }
                        }

                        Icon(
                            Icons.Default.BarChart,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.35f),
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }
            }
        }

        // Operational Metric Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Card 1: Pesanan Dapur
            Surface(
                onClick = { onNavigateTo("orders") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE9E1D9))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(42.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFF0E8)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.AutoMirrored.Filled.ReceiptLong,
                                contentDescription = null,
                                tint = Color(0xFFE25822),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column {
                        Text("Pesanan Dapur", fontSize = 11.sp, color = Color(0xFF81766F), fontWeight = FontWeight.Medium)
                        Text(
                            "${summary?.activeOrdersCount ?: 0}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF2B211D)
                        )
                    }
                }
            }

            // Card 2: Belum Bayar
            Surface(
                onClick = { onNavigateTo("payments") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE9E1D9))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(42.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFF0E8)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = Color(0xFFE25822),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column {
                        Text("Belum Bayar", fontSize = 11.sp, color = Color(0xFF81766F), fontWeight = FontWeight.Medium)
                        Text(
                            "${summary?.pendingPaymentsCount ?: 0}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF2B211D)
                        )
                    }
                }
            }
        }

        // Secondary Operational Management Hub (NON-REDUNDANT)
        Text(
            text = "Pengelolaan Resto & Operasional",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color(0xFF2B211D),
            modifier = Modifier.padding(top = 4.dp)
        )

        // 2x2 Grid for Secondary Features NOT in bottom navbar
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                DashboardActionCard(
                    title = "Kelola Kategori",
                    icon = Icons.Default.Category,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("categories") }
                )

                DashboardActionCard(
                    title = "Meja & QR Code",
                    icon = Icons.Default.QrCode,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("tables") }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                DashboardActionCard(
                    title = "Biaya Operasional",
                    icon = Icons.Default.MoneyOff,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("expenses") }
                )

                DashboardActionCard(
                    title = "Laporan Surplus",
                    icon = Icons.Default.Assessment,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("reports") }
                )
            }
        }

        // Account Access Hub — pintu masuk ke profil & logout
        Text(
            text = "Akun & Keamanan",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color(0xFF2B211D),
            modifier = Modifier.padding(top = 4.dp)
        )

        DashboardAccountCard(
            user = user,
            onClick = { onNavigateTo("profile") }
        )

        Spacer(modifier = Modifier.height(2.dp))
    }
}

@Composable
private fun DashboardAccountCard(
    user: User?,
    onClick: () -> Unit
) {
    val displayName = user?.name?.takeIf { it.isNotBlank() } ?: "Akun Saya"
    val displayEmail = user?.email?.takeIf { it.isNotBlank() }
        ?: "Kelola profil dan keamanan akun"
    val initial = displayName.trim().firstOrNull()?.uppercase() ?: "A"

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE9E1D9))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = Color(0xFFFFF0E8)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = initial,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFE25822)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF2B211D),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = displayEmail,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF81766F),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Buka profil akun",
                tint = Color(0xFFB9AEA6),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun DashboardActionCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(96.dp),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE9E1D9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Surface(
                modifier = Modifier.size(34.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFFFF0E8)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = title,
                        tint = Color(0xFFE25822),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFF2B211D)
            )
        }
    }
}
