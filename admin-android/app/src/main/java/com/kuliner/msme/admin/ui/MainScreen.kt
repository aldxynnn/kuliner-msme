package com.kuliner.msme.admin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kuliner.msme.admin.data.api.RetrofitClient
import com.kuliner.msme.admin.data.model.User
import com.kuliner.msme.admin.notification.NotificationCenter
import com.kuliner.msme.admin.notification.OrderAlerts
import com.kuliner.msme.admin.ui.components.CountBadge
import com.kuliner.msme.admin.ui.screens.*
import com.kuliner.msme.admin.ui.theme.*
import kotlinx.coroutines.delay

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Beranda", Icons.Default.Dashboard)
    object Orders : Screen("orders", "Pesanan", Icons.AutoMirrored.Filled.ReceiptLong)
    object CashierOrder : Screen("cashier_order", "POS", Icons.Default.PointOfSale)
    object Payments : Screen("payments", "Bayar", Icons.Default.Payments)
    object Products : Screen("products", "Menu", Icons.Default.Fastfood)
    object Categories : Screen("categories", "Kategori", Icons.Default.Category)
    object Tables : Screen("tables", "Meja", Icons.Default.QrCode)
    object Expenses : Screen("expenses", "Biaya", Icons.Default.MoneyOff)
    object Reports : Screen("reports", "Laporan", Icons.Default.Assessment)
    object Notifications : Screen("notifications", "Notifikasi", Icons.Default.Notifications)
    object Profile : Screen("profile", "Profil & Akun", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLogout: () -> Unit,
    openOrdersSignal: Int = 0
) {
    var currentRoute by remember { mutableStateOf<String>(Screen.Dashboard.route) }

    LaunchedEffect(openOrdersSignal) {
        if (openOrdersSignal > 0) {
            currentRoute = Screen.Orders.route
        }
    }
    val context = LocalContext.current

    val notifications by NotificationCenter.notifications.collectAsState()
    val unreadNotifications = notifications.count { !it.isRead }

    var currentUser by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.getApiService(context).getMe()
            if (response.isSuccessful) {
                currentUser = response.body()
            }
        } catch (_: Exception) {
            // Profil tetap memakai fallback bila jaringan bermasalah.
        }
    }

    LaunchedEffect(Unit) {
        NotificationCenter.init(context)
        while (true) {
            NotificationCenter.refresh(context)
            delay(15_000)
        }
    }

    LaunchedEffect(Unit) {
        OrderAlerts.newOrders.collect {
            NotificationCenter.refresh(context)
        }
    }

    LaunchedEffect(currentRoute) {
        NotificationCenter.refresh(context)
    }

    val mainNavItems = listOf(
        Screen.Dashboard,
        Screen.Orders,
        Screen.CashierOrder,
        Screen.Payments,
        Screen.Products
    )

    val isDashboard = currentRoute == Screen.Dashboard.route

    Scaffold(
        containerColor = AromaCream,
        topBar = {
            if (!isDashboard) {
                Surface(
                    color = AromaSurface,
                    shadowElevation = 0.dp
                ) {
                    Column(modifier = Modifier.statusBarsPadding()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { currentRoute = Screen.Dashboard.route },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Kembali",
                                    tint = AromaCharcoal,
                                    modifier = Modifier.size(21.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = when (currentRoute) {
                                    Screen.Orders.route -> "Pesanan"
                                    Screen.CashierOrder.route -> "Kasir POS"
                                    Screen.Payments.route -> "Konfirmasi Pembayaran"
                                    Screen.Products.route -> "Kelola Menu"
                                    Screen.Categories.route -> "Kelola Kategori"
                                    Screen.Tables.route -> "Meja & QR Code"
                                    Screen.Expenses.route -> "Biaya Operasional"
                                    Screen.Reports.route -> "Laporan Surplus"
                                    Screen.Notifications.route -> "Notifikasi"
                                    Screen.Profile.route -> "Profil & Akun"
                                    else -> "AROMA POS"
                                },
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AromaCharcoal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        HorizontalDivider(color = AromaDivider, thickness = 1.dp)
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = AromaSurface,
                shadowElevation = 0.dp
            ) {
                Column(modifier = Modifier.navigationBarsPadding()) {
                    HorizontalDivider(color = AromaBorder, thickness = 1.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        mainNavItems.forEach { screen ->
                            val isSelected = currentRoute == screen.route
                            val badgeCount = when (screen.route) {
                                Screen.Orders.route -> NotificationCenter.unreadForRoute(
                                    notifications,
                                    Screen.Orders.route
                                )
                                Screen.Payments.route -> NotificationCenter.unreadForRoute(
                                    notifications,
                                    Screen.Payments.route
                                )
                                else -> 0
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable { currentRoute = screen.route },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(28.dp)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp))
                                        .background(
                                            if (isSelected) AromaTerracotta else Color.Transparent
                                        )
                                )
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    NavIconWithBadge(
                                        icon = screen.icon,
                                        label = screen.title,
                                        tint = if (isSelected) AromaTerracotta else AromaTextMuted,
                                        count = badgeCount
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = screen.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) AromaTerracotta else AromaTextMuted,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .then(if (isDashboard) Modifier.statusBarsPadding() else Modifier),
            color = AromaCream
        ) {
            when (currentRoute) {
                Screen.Dashboard.route -> DashboardScreen(
                    onNavigateTo = { route -> currentRoute = route },
                    user = currentUser,
                    onOpenNotifications = { currentRoute = Screen.Notifications.route },
                    unreadNotifications = unreadNotifications
                )
                Screen.Orders.route -> OrdersScreen()
                Screen.CashierOrder.route -> CashierOrderScreen(
                    onOrderCreated = { currentRoute = Screen.Orders.route }
                )
                Screen.Payments.route -> PaymentsScreen()
                Screen.Products.route -> ProductsScreen()
                Screen.Categories.route -> CategoriesScreen()
                Screen.Tables.route -> TablesScreen()
                Screen.Expenses.route -> ExpensesScreen()
                Screen.Reports.route -> ReportsScreen()
                Screen.Notifications.route -> NotificationsScreen(
                    onNavigateTo = { route -> currentRoute = route }
                )
                Screen.Profile.route -> ProfileScreen(
                    user = currentUser,
                    onLogout = onLogout
                )
                else -> DashboardScreen(
                    onNavigateTo = { route -> currentRoute = route },
                    user = currentUser,
                    onOpenNotifications = { currentRoute = Screen.Notifications.route },
                    unreadNotifications = unreadNotifications
                )
            }
        }
    }
}

@Composable
private fun NavIconWithBadge(
    icon: ImageVector,
    label: String,
    tint: Color,
    count: Int
) {
    Box(
        modifier = Modifier.size(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        CountBadge(
            count = count,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 6.dp, y = (-2).dp),
            borderColor = AromaSurface
        )
    }
}
