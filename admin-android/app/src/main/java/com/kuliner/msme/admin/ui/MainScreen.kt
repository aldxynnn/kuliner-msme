package com.kuliner.msme.admin.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kuliner.msme.admin.R
import com.kuliner.msme.admin.data.api.TokenManager
import com.kuliner.msme.admin.notification.NotificationCenter
import com.kuliner.msme.admin.notification.OrderAlerts
import com.kuliner.msme.admin.ui.components.CountBadge
import com.kuliner.msme.admin.ui.components.NotificationBellButton
import com.kuliner.msme.admin.ui.screens.*
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
    val tokenManager = remember { TokenManager(context) }

    val notifications by NotificationCenter.notifications.collectAsState()
    val unreadNotifications = notifications.count { !it.isRead }

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
        topBar = {
            if (!isDashboard) {
                Surface(
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { currentRoute = Screen.Dashboard.route }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Kembali",
                                    tint = Color(0xFF2B211D),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (currentRoute) {
                                    Screen.Orders.route -> "Pesanan"
                                    Screen.CashierOrder.route -> "POS"
                                    Screen.Payments.route -> "Konfirmasi Pembayaran"
                                    Screen.Products.route -> "Kelola Menu"
                                    Screen.Categories.route -> "Kelola Kategori"
                                    Screen.Tables.route -> "Meja & QR Code"
                                    Screen.Expenses.route -> "Pengeluaran Resto"
                                    Screen.Reports.route -> "Laporan Surplus"
                                    Screen.Notifications.route -> "Notifikasi"
                                    else -> "AROMA POS"
                                },
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                color = Color(0xFF2B211D)
                            )
                        }

                        // Notification bell + Logout actions
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (currentRoute != Screen.Notifications.route) {
                                NotificationBellButton(
                                    unreadCount = unreadNotifications,
                                    onClick = { currentRoute = Screen.Notifications.route }
                                )
                            }

                            Surface(
                                onClick = {
                                    tokenManager.clear()
                                    onLogout()
                                },
                                modifier = Modifier.size(36.dp),
                                shape = CircleShape,
                                color = Color(0xFFFEE2E2)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Logout,
                                        contentDescription = "Keluar",
                                        tint = Color(0xFF991B1B),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Sleek Floating Bottom Navigation Bar (Height 54.dp, Rounded 27.dp)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 10.dp),
                shape = RoundedCornerShape(27.dp),
                color = Color.White,
                shadowElevation = 6.dp,
                border = BorderStroke(1.dp, Color(0xFFE9E1D9))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    mainNavItems.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        val badgeCount = when (screen.route) {
                            Screen.Orders.route -> NotificationCenter.unreadForRoute(notifications, Screen.Orders.route)
                            Screen.Payments.route -> NotificationCenter.unreadForRoute(notifications, Screen.Payments.route)
                            else -> 0
                        }
                        Surface(
                            onClick = { currentRoute = screen.route },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) Color(0xFFFFF0E8) else Color.Transparent,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                NavIconWithBadge(
                                    icon = screen.icon,
                                    label = screen.title,
                                    tint = if (isSelected) Color(0xFFE25822) else Color(0xFF81766F),
                                    count = badgeCount
                                )
                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = screen.title,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFE25822)
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
                .padding(innerPadding)
                .background(Color(0xFFFAF8F5)),
            color = Color(0xFFFAF8F5)
        ) {
            when (currentRoute) {
                Screen.Dashboard.route -> DashboardScreen(
                    onNavigateTo = { route -> currentRoute = route },
                    onLogout = onLogout,
                    onOpenNotifications = { currentRoute = Screen.Notifications.route },
                    unreadNotifications = unreadNotifications
                )
                Screen.Orders.route -> OrdersScreen()
                Screen.CashierOrder.route -> CashierOrderScreen(onOrderCreated = { currentRoute = Screen.Orders.route })
                Screen.Payments.route -> PaymentsScreen()
                Screen.Products.route -> ProductsScreen()
                Screen.Categories.route -> CategoriesScreen()
                Screen.Tables.route -> TablesScreen()
                Screen.Expenses.route -> ExpensesScreen()
                Screen.Reports.route -> ReportsScreen()
                Screen.Notifications.route -> NotificationsScreen(
                    onNavigateTo = { route -> currentRoute = route }
                )
                else -> DashboardScreen(
                    onNavigateTo = { route -> currentRoute = route },
                    onLogout = onLogout,
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
        modifier = Modifier.size(26.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
        CountBadge(
            count = count,
            modifier = Modifier.align(Alignment.TopEnd),
            borderColor = Color.White
        )
    }
}
