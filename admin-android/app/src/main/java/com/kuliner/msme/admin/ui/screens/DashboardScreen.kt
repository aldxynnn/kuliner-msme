package com.kuliner.msme.admin.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kuliner.msme.admin.R
import com.kuliner.msme.admin.data.api.RetrofitClient
import com.kuliner.msme.admin.data.model.DashboardSummary
import com.kuliner.msme.admin.data.model.User
import com.kuliner.msme.admin.ui.components.AromaCard
import com.kuliner.msme.admin.ui.components.AromaSectionHeader
import com.kuliner.msme.admin.ui.components.NotificationBellButton
import com.kuliner.msme.admin.ui.components.formatRupiah
import com.kuliner.msme.admin.ui.theme.*
import com.kuliner.msme.admin.ui.util.friendlyUserName
import kotlinx.coroutines.launch

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

    val pendingCount = summary?.pendingPaymentsCount ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AromaCream)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Top Header: Brand on left, Notification Bell on right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = AromaSurface,
                    border = BorderStroke(1.dp, AromaBorder)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_aroma),
                        contentDescription = "Logo AROMA",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "AROMA POS",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = AromaCharcoal,
                        letterSpacing = (-0.3).sp
                    )
                    Text(
                        text = "Ayam Goreng Lengkuas",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AromaTextMuted
                    )
                }
            }

            NotificationBellButton(
                unreadCount = unreadNotifications,
                onClick = onOpenNotifications
            )
        }

        // Greeting
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Selamat bertugas,",
                style = MaterialTheme.typography.bodyMedium,
                color = AromaTextSecondary
            )
            Text(
                text = friendlyUserName(user?.name),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = AromaCharcoal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Primary Revenue Summary Panel (Solid Aroma Terracotta, clean commercial POS aesthetic)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = AromaTerracotta
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "OMZET LUNAS HARI INI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = AromaSurface.copy(alpha = 0.85f)
                    )
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = AromaSurface,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = AromaSurface.copy(alpha = 0.85f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = formatRupiah(summary?.todayRevenue ?: 0),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp,
                    color = AromaSurface
                )

                HorizontalDivider(color = AromaSurface.copy(alpha = 0.18f), thickness = 1.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Transaksi Lunas",
                            fontSize = 11.sp,
                            color = AromaSurface.copy(alpha = 0.78f)
                        )
                        Text(
                            text = "${summary?.todayTransactionCount ?: 0} transaksi",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AromaSurface
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Pesanan Selesai",
                            fontSize = 11.sp,
                            color = AromaSurface.copy(alpha = 0.78f)
                        )
                        Text(
                            text = "${summary?.completedOrdersTodayCount ?: 0} selesai",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AromaSurface
                        )
                    }
                }
            }
        }

        // Key Live Operations KPI Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardKpiCard(
                label = "Pesanan Aktif",
                value = "${summary?.activeOrdersCount ?: 0}",
                helper = "Antrean & dapur",
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                iconBg = AromaTerracottaSoft,
                iconTint = AromaTerracotta,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateTo("orders") }
            )

            DashboardKpiCard(
                label = "Belum Bayar",
                value = "$pendingCount",
                helper = if (pendingCount > 0) "Perlu konfirmasi" else "Semua lunas",
                icon = Icons.Default.AccessTime,
                iconBg = if (pendingCount > 0) AromaWarningSoft else AromaSuccessSoft,
                iconTint = if (pendingCount > 0) AromaWarning else AromaSuccess,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateTo("payments") }
            )
        }

        // Secondary Operational Management Hub
        AromaSectionHeader(
            title = "Pengelolaan Resto",
            subtitle = "Kategori, meja QR, biaya operasional, dan laporan"
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardActionCard(
                    title = "Kelola Kategori",
                    subtitle = "Susun kelompok menu",
                    icon = Icons.Default.Category,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("categories") }
                )

                DashboardActionCard(
                    title = "Meja & QR Code",
                    subtitle = "Tautan meja pelanggan",
                    icon = Icons.Default.QrCode,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("tables") }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardActionCard(
                    title = "Biaya Operasional",
                    subtitle = "Catat pengeluaran harian",
                    icon = Icons.Default.MoneyOff,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("expenses") }
                )

                DashboardActionCard(
                    title = "Laporan Surplus",
                    subtitle = "Rekap harian & bulanan",
                    icon = Icons.Default.Assessment,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("reports") }
                )
            }
        }

        // Account Access Hub
        AromaSectionHeader(title = "Akun & Sesi")

        DashboardAccountCard(
            user = user,
            onClick = { onNavigateTo("profile") }
        )

        Spacer(modifier = Modifier.height(6.dp))
    }
}

@Composable
private fun DashboardKpiCard(
    label: String,
    value: String,
    helper: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    AromaCard(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = iconBg
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = AromaTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(
                    text = value,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AromaCharcoal
                )
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AromaCharcoal
                )
                Text(
                    text = helper,
                    fontSize = 11.sp,
                    color = AromaTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun DashboardAccountCard(
    user: User?,
    onClick: () -> Unit
) {
    val displayName = friendlyUserName(user?.name, fallback = "Akun Saya")
    val displayEmail = user?.email?.takeIf { it.isNotBlank() }
        ?: "Kelola profil dan keamanan akun"
    val initial = displayName.trim().firstOrNull()?.uppercase() ?: "A"

    AromaCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = AromaTerracottaSoft,
                border = BorderStroke(1.dp, AromaTerracottaBorder)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = initial,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AromaTerracotta
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AromaCharcoal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = displayEmail,
                    fontSize = 12.sp,
                    color = AromaTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Buka profil akun",
                tint = AromaTextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun DashboardActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    AromaCard(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(10.dp),
                color = AromaTerracottaSoft
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = title,
                        tint = AromaTerracotta,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = AromaCharcoal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = AromaTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
