package com.kuliner.msme.admin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kuliner.msme.admin.data.api.TokenManager
import com.kuliner.msme.admin.ui.screens.*

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Beranda", Icons.Default.Dashboard)
    object Orders : Screen("orders", "Pesanan", Icons.Default.ReceiptLong)
    object CashierOrder : Screen("cashier_order", "POS", Icons.Default.PointOfSale)
    object Payments : Screen("payments", "Bayar", Icons.Default.Payments)
    object Products : Screen("products", "Menu", Icons.Default.Fastfood)
    object Categories : Screen("categories", "Kategori", Icons.Default.Category)
    object Tables : Screen("tables", "Meja", Icons.Default.QrCode)
    object Expenses : Screen("expenses", "Biaya", Icons.Default.MoneyOff)
    object Reports : Screen("reports", "Laporan", Icons.Default.Assessment)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLogout: () -> Unit
) {
    var currentRoute by remember { mutableStateOf<String>(Screen.Dashboard.route) }
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    val mainNavItems = listOf(
        Screen.Dashboard,
        Screen.Orders,
        Screen.CashierOrder,
        Screen.Payments,
        Screen.Products
    )

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "A",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "AROMA POS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = when (currentRoute) {
                                    Screen.Dashboard.route -> "Ringkasan Resto"
                                    Screen.Orders.route -> "Antrean Pesanan"
                                    Screen.CashierOrder.route -> "Input Pesanan POS"
                                    Screen.Payments.route -> "Konfirmasi Pembayaran"
                                    Screen.Products.route -> "Katalog Menu"
                                    Screen.Categories.route -> "Kategori"
                                    Screen.Tables.route -> "Meja & QR"
                                    Screen.Expenses.route -> "Pengeluaran"
                                    Screen.Reports.route -> "Laporan Surplus"
                                    else -> "Resto"
                                },
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    // Top Action Navigation Shortcuts
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(onClick = { currentRoute = Screen.Categories.route }) {
                            Icon(
                                Icons.Default.Category,
                                contentDescription = "Kategori",
                                tint = if (currentRoute == Screen.Categories.route) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(onClick = { currentRoute = Screen.Tables.route }) {
                            Icon(
                                Icons.Default.QrCode,
                                contentDescription = "Meja",
                                tint = if (currentRoute == Screen.Tables.route) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(onClick = { currentRoute = Screen.Expenses.route }) {
                            Icon(
                                Icons.Default.MoneyOff,
                                contentDescription = "Biaya",
                                tint = if (currentRoute == Screen.Expenses.route) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(onClick = { currentRoute = Screen.Reports.route }) {
                            Icon(
                                Icons.Default.Assessment,
                                contentDescription = "Laporan",
                                tint = if (currentRoute == Screen.Reports.route) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(onClick = {
                            tokenManager.clear()
                            onLogout()
                        }) {
                            Icon(
                                Icons.Default.Logout,
                                contentDescription = "Keluar",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                mainNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        selected = currentRoute == screen.route,
                        onClick = { currentRoute = screen.route },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentRoute) {
                Screen.Dashboard.route -> DashboardScreen(onNavigateTo = { route -> currentRoute = route })
                Screen.Orders.route -> OrdersScreen()
                Screen.CashierOrder.route -> CashierOrderScreen(onOrderCreated = { currentRoute = Screen.Orders.route })
                Screen.Payments.route -> PaymentsScreen()
                Screen.Products.route -> ProductsScreen()
                Screen.Categories.route -> CategoriesScreen()
                Screen.Tables.route -> TablesScreen()
                Screen.Expenses.route -> ExpensesScreen()
                Screen.Reports.route -> ReportsScreen()
                else -> DashboardScreen(onNavigateTo = { route -> currentRoute = route })
            }
        }
    }
}
