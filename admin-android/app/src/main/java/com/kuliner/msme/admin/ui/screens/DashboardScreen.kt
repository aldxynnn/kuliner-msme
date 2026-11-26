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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
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
    val activeOrdersCount = summary?.activeOrdersCount ?: 0
    val txCount = summary?.todayTransactionCount ?: 0
    val completedCount = summary?.completedOrdersTodayCount ?: 0
    val displayName = friendlyUserName(user?.name)
    val initial = displayName.trim().firstOrNull()?.uppercase() ?: "A"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AromaCream)
    ) {
        // 1. Pure White Anchored Header Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = AromaSurface
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = AromaTerracottaSoft,
                            border = BorderStroke(1.dp, AromaTerracottaBorder)
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
                                text = "Halo, $displayName",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AromaCharcoal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(AromaSuccessLive, CircleShape)
                                )
                                Text(
                                    text = "AROMA POS · Shift Aktif",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AromaTextMuted
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NotificationBellButton(
                            unreadCount = unreadNotifications,
                            onClick = onOpenNotifications
                        )

                        Surface(
                            onClick = { onNavigateTo("profile") },
                            modifier = Modifier.size(42.dp),
                            shape = CircleShape,
                            color = AromaTerracotta,
                            border = BorderStroke(2.dp, AromaTerracottaSoft)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = initial,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AromaSurface
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(color = AromaBorder, thickness = 1.dp)
            }
        }

        // Scrollable Workspace
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 2. Clean Split Financial Summary Card (White Top + Soft Orange KPI Footer)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = AromaSurface,
                border = BorderStroke(1.dp, AromaBorder)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(14.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(AromaTerracotta)
                                )
                                Text(
                                    text = "Omzet Lunas Hari Ini",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AromaTextSecondary
                                )
                            }

                            Surface(
                                onClick = { if (!isLoading) loadData() },
                                shape = CircleShape,
                                color = AromaCreamSoft
                            ) {
                                Box(
                                    modifier = Modifier.size(30.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            color = AromaTerracotta,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(
                                            Icons.Default.Refresh,
                                            contentDescription = "Muat ulang ringkasan",
                                            tint = AromaTextSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = formatRupiah(summary?.todayRevenue ?: 0),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.8).sp,
                            color = AromaCharcoal
                        )
                    }

                    // Soft Orange 3-Column KPI Strip
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = AromaTerracottaSoft
                    ) {
                        Column {
                            HorizontalDivider(color = AromaTerracottaBorder, thickness = 1.dp)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min)
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                DashboardMiniMetric(
                                    label = "Transaksi",
                                    value = "$txCount",
                                    valueColor = AromaCharcoal,
                                    modifier = Modifier.weight(1f)
                                )
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .fillMaxHeight()
                                        .background(AromaTerracottaBorder)
                                )
                                DashboardMiniMetric(
                                    label = "Selesai",
                                    value = "$completedCount",
                                    valueColor = AromaSuccess,
                                    modifier = Modifier.weight(1f)
                                )
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .fillMaxHeight()
                                        .background(AromaTerracottaBorder)
                                )
                                DashboardMiniMetric(
                                    label = "Belum Bayar",
                                    value = "$pendingCount",
                                    valueColor = if (pendingCount > 0) AromaTerracotta else AromaTextMuted,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onNavigateTo("payments") }
                                )
                            }
                        }
                    }
                }
            }

            // 3. Primary Cashier Register Banner CTA
            Surface(
                onClick = { onNavigateTo("cashier_order") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = AromaTerracotta
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = AromaSurface.copy(alpha = 0.18f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.PointOfSale,
                                contentDescription = null,
                                tint = AromaSurface,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Buka Kasir POS",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AromaSurface
                        )
                        Text(
                            text = "Buat pesanan Dine In atau Take Away",
                            fontSize = 12.sp,
                            color = AromaSurface.copy(alpha = 0.88f)
                        )
                    }

                    Surface(
                        modifier = Modifier.size(34.dp),
                        shape = CircleShape,
                        color = AromaSurface
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Buka POS",
                                tint = AromaTerracotta,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }
            }

            // 4. 2-Column Live Operations Queue Tiles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LiveOperationTile(
                    icon = Icons.AutoMirrored.Filled.ReceiptLong,
                    count = "$activeOrdersCount",
                    title = "Antrean Pesanan",
                    subtitle = "Pantau dapur",
                    highlight = activeOrdersCount > 0,
                    onClick = { onNavigateTo("orders") },
                    modifier = Modifier.weight(1f)
                )
                LiveOperationTile(
                    icon = Icons.Default.Payments,
                    count = "$pendingCount",
                    title = "Konfirmasi Bayar",
                    subtitle = if (pendingCount > 0) "Perlu verifikasi" else "Semua lunas",
                    highlight = pendingCount > 0,
                    onClick = { onNavigateTo("payments") },
                    modifier = Modifier.weight(1f)
                )
            }

            // 5. Modular 2x2 Restaurant Management Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kelola Restoran",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AromaCharcoal
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onNavigateTo("products") }
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Katalog Menu",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AromaTerracotta
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = AromaTerracotta,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ManagementGridTile(
                        icon = Icons.Default.Category,
                        title = "Kategori Menu",
                        subtitle = "Susun kelompok hidangan",
                        onClick = { onNavigateTo("categories") },
                        modifier = Modifier.weight(1f)
                    )
                    ManagementGridTile(
                        icon = Icons.Default.QrCode,
                        title = "Meja & QR",
                        subtitle = "Atur meja & kode QR",
                        onClick = { onNavigateTo("tables") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ManagementGridTile(
                        icon = Icons.Default.MoneyOff,
                        title = "Biaya Harian",
                        subtitle = "Catat pengeluaran",
                        onClick = { onNavigateTo("expenses") },
                        modifier = Modifier.weight(1f)
                    )
                    ManagementGridTile(
                        icon = Icons.Default.Assessment,
                        title = "Laporan Surplus",
                        subtitle = "Omzet & arus kas",
                        onClick = { onNavigateTo("reports") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
private fun DashboardMiniMetric(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            color = valueColor
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = AromaTextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun LiveOperationTile(
    icon: ImageVector,
    count: String,
    title: String,
    subtitle: String,
    highlight: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = AromaSurface,
        border = BorderStroke(1.dp, if (highlight) AromaTerracottaBorder else AromaBorder)
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
                    color = if (highlight) AromaTerracottaSoft else AromaCreamSoft
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = if (highlight) AromaTerracotta else AromaTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = count,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (highlight) AromaTerracotta else AromaCharcoal
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
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
        }
    }
}

@Composable
private fun ManagementGridTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = AromaSurface,
        border = BorderStroke(1.dp, AromaBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.size(38.dp),
                shape = RoundedCornerShape(11.dp),
                color = AromaTerracottaSoft,
                border = BorderStroke(1.dp, AromaTerracottaBorder)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = AromaTerracotta,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AromaCharcoal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
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
