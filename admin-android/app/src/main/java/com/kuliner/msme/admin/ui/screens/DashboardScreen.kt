package com.kuliner.msme.admin.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
        // Clean Top Bar: Brand Identity + Notification Bell
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

        // Bright Refined White Revenue Sheet with subtle Terracotta top accent
        AromaCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 16.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(AromaTerracotta)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
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
                            color = AromaTerracotta
                        )
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = AromaTerracotta,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AromaSuccessSoft
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.TrendingUp,
                                        contentDescription = null,
                                        tint = AromaSuccess,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Live",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AromaSuccess
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = formatRupiah(summary?.todayRevenue ?: 0),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp,
                        color = AromaCharcoal
                    )

                    HorizontalDivider(color = AromaDivider, thickness = 1.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Transaksi Lunas",
                                fontSize = 11.sp,
                                color = AromaTextMuted
                            )
                            Text(
                                text = "${summary?.todayTransactionCount ?: 0} transaksi",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AromaCharcoal
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Pesanan Selesai",
                                fontSize = 11.sp,
                                color = AromaTextMuted
                            )
                            Text(
                                text = "${summary?.completedOrdersTodayCount ?: 0} selesai",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AromaCharcoal
                            )
                        }
                    }
                }
            }
        }

        // Key Live Operations KPI Row
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

        // Grouped Operational Management Hub (Single crisp panel instead of 4 redundant cards)
        AromaSectionHeader(
            title = "Pengelolaan Resto",
            subtitle = "Kategori, meja QR, biaya operasional, dan laporan"
        )

        AromaCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 16.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                DashboardMenuRow(
                    title = "Kelola Kategori",
                    subtitle = "Susun kelompok menu makanan & minuman",
                    icon = Icons.Default.Category,
                    onClick = { onNavigateTo("categories") }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = AromaDivider
                )
                DashboardMenuRow(
                    title = "Meja & QR Code",
                    subtitle = "Atur meja dan tautan QR pemesanan",
                    icon = Icons.Default.QrCode,
                    onClick = { onNavigateTo("tables") }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = AromaDivider
                )
                DashboardMenuRow(
                    title = "Biaya Operasional",
                    subtitle = "Catat pengeluaran bahan baku & harian",
                    icon = Icons.Default.MoneyOff,
                    onClick = { onNavigateTo("expenses") }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = AromaDivider
                )
                DashboardMenuRow(
                    title = "Laporan Surplus",
                    subtitle = "Rekap omzet lunas & pengeluaran periode",
                    icon = Icons.Default.Assessment,
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
        modifier = modifier,
        cornerRadius = 14.dp
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
                    modifier = Modifier.size(34.dp),
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
private fun DashboardMenuRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(38.dp),
            shape = RoundedCornerShape(10.dp),
            color = AromaTerracottaSoft
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = AromaTerracotta,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
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

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = title,
            tint = AromaTextMuted,
            modifier = Modifier.size(20.dp)
        )
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
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 14.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = AromaTerracottaSoft,
                border = BorderStroke(1.dp, AromaTerracottaBorder)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = initial,
                        fontSize = 15.sp,
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
                Spacer(modifier = Modifier.height(1.dp))
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
