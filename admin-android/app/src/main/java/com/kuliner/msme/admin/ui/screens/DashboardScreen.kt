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
import androidx.compose.ui.text.font.FontFamily
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
    val activeOrdersCount = summary?.activeOrdersCount ?: 0
    val displayName = friendlyUserName(user?.name)
    val initial = displayName.trim().firstOrNull()?.uppercase() ?: "A"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AromaCream)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Clean Top Bar: Logo + Shift Operator + Bell + Profile Avatar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    modifier = Modifier.size(42.dp),
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
                        text = "AROMA POS",
                        style = AromaBrandSerifStyle,
                        fontSize = 19.sp,
                        color = AromaTerracotta
                    )
                    Text(
                        text = "Shift · $displayName",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = AromaTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
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
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    color = AromaTerracottaSoft,
                    border = BorderStroke(1.dp, AromaTerracottaBorder)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = initial,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AromaTerracotta
                        )
                    }
                }
            }
        }

        // Clean Brand Orange Revenue Summary Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
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
                        color = AromaSurface.copy(alpha = 0.88f)
                    )

                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = AromaSurface,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = AromaSurface.copy(alpha = 0.18f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(AromaSurface, CircleShape)
                                )
                                Text(
                                    text = "LIVE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AromaSurface,
                                    letterSpacing = 0.6.sp
                                )
                            }
                        }
                    }
                }

                Text(
                    text = formatRupiah(summary?.todayRevenue ?: 0),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.8).sp,
                    color = AromaSurface
                )

                HorizontalDivider(color = AromaSurface.copy(alpha = 0.22f), thickness = 1.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Transaksi Lunas",
                            fontSize = 11.sp,
                            color = AromaSurface.copy(alpha = 0.82f)
                        )
                        Text(
                            text = "${summary?.todayTransactionCount ?: 0} transaksi",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AromaSurface
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Pesanan Selesai",
                            fontSize = 11.sp,
                            color = AromaSurface.copy(alpha = 0.82f)
                        )
                        Text(
                            text = "${summary?.completedOrdersTodayCount ?: 0} selesai",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AromaSurface
                        )
                    }
                }
            }
        }

        // Unified Split Live Operations Strip (One architectural container, zero redundant boxes)
        AromaCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 14.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                // Left half: Active Kitchen Queue
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateTo("orders") }
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(AromaTerracotta, CircleShape)
                            )
                            Text(
                                text = "PESANAN AKTIF",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AromaTextMuted,
                                letterSpacing = 0.6.sp
                            )
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = AromaTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "$activeOrdersCount",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = AromaCharcoal
                    )
                    Text(
                        text = "Antrean & dapur",
                        fontSize = 11.sp,
                        color = AromaTextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .padding(vertical = 12.dp)
                        .background(AromaBorder)
                )

                // Right half: Pending Payments
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateTo("payments") }
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(
                                        if (pendingCount > 0) AromaWarning else AromaSuccess,
                                        CircleShape
                                    )
                            )
                            Text(
                                text = "BELUM BAYAR",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (pendingCount > 0) AromaWarning else AromaTextMuted,
                                letterSpacing = 0.6.sp
                            )
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = AromaTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "$pendingCount",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (pendingCount > 0) AromaWarning else AromaSuccess
                    )
                    Text(
                        text = if (pendingCount > 0) "Perlu konfirmasi" else "Semua lunas",
                        fontSize = 11.sp,
                        color = AromaTextSecondary
                    )
                }
            }
        }

        // Quick Cashier Register Launch Strip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { onNavigateTo("cashier_order") },
                modifier = Modifier
                    .weight(1.15f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AromaTerracotta,
                    contentColor = AromaSurface
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    Icons.Default.PointOfSale,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text("Buka Kasir POS", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = { onNavigateTo("products") },
                modifier = Modifier
                    .weight(0.85f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AromaTerracottaBorder),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = AromaSurface,
                    contentColor = AromaTerracotta
                )
            ) {
                Icon(
                    Icons.Default.Fastfood,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Kelola Menu", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Unified Back-Office Directory with Monospace Index Numbers
        AromaSectionHeader(
            overline = "Manajemen Usaha",
            title = "Operasional & Laporan",
            subtitle = "Kategori menu, meja QR, pengeluaran, dan surplus"
        )

        AromaCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 14.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                DashboardMenuRow(
                    indexCode = "01",
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
                    indexCode = "02",
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
                    indexCode = "03",
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
                    indexCode = "04",
                    title = "Laporan Surplus",
                    subtitle = "Rekap omzet lunas & pengeluaran periode",
                    icon = Icons.Default.Assessment,
                    onClick = { onNavigateTo("reports") }
                )
            }
        }

        // Operator Account Strip
        AromaCard(
            onClick = { onNavigateTo("profile") },
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 14.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(38.dp),
                    shape = CircleShape,
                    color = AromaCreamSoft,
                    border = BorderStroke(1.dp, AromaBorder)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = initial,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AromaCharcoal
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
                    Text(
                        text = user?.email?.takeIf { it.isNotBlank() } ?: "Profil kasir & sesi",
                        fontSize = 12.sp,
                        color = AromaTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "Akun",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AromaTerracotta
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Buka profil akun",
                    tint = AromaTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
    }
}

@Composable
private fun DashboardMenuRow(
    indexCode: String,
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
            modifier = Modifier.size(40.dp),
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

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
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
                fontSize = 12.sp,
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
