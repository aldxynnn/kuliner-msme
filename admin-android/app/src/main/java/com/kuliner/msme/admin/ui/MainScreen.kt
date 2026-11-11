package com.kuliner.msme.admin.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kuliner.msme.admin.data.api.TokenManager
import com.kuliner.msme.admin.ui.screens.*

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Beranda", Icons.Default.Dashboard)
    object Orders : Screen("orders", "Pesanan", Icons.Default.ReceiptLong)
    object CashierOrder : Screen("cashier_order", "POS", Icons.Default.AddShoppingCart)
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

    val bottomNavItems = listOf(
        Screen.Dashboard,
        Screen.Orders,
        Screen.CashierOrder,
        Screen.Payments,
        Screen.Products
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "AROMA POS",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = when (currentRoute) {
                                Screen.Dashboard.route -> "Beranda & Ringkasan"
                                Screen.Orders.route -> "Kelola Pesanan Masuk"
                                Screen.CashierOrder.route -> "Kasir & Pemesanan Manual"
                                Screen.Payments.route -> "Konfirmasi Pembayaran"
                                Screen.Products.route -> "Manajemen Produk Menu"
                                Screen.Categories.route -> "Manajemen Kategori"
                                Screen.Tables.route -> "Manajemen Meja & QR"
                                Screen.Expenses.route -> "Pencatatan Biaya Operasional"
                                Screen.Reports.route -> "Laporan & Surplus Keuangan"
                                else -> "Manajemen Resto"
                            },
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                actions = {
                    IconButton(onClick = {
                        tokenManager.clear()
                        onLogout()
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Keluar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        selected = currentRoute == screen.route,
                        onClick = { currentRoute = screen.route },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding),
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
