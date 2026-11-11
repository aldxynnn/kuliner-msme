package com.kuliner.msme.admin.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import com.kuliner.msme.admin.data.api.TokenManager
import com.kuliner.msme.admin.ui.screens.*

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Beranda", Icons.Default.Dashboard)
    object Orders : Screen("orders", "Pesanan", Icons.Default.ReceiptLong)
    object CashierOrder : Screen("cashier_order", "Kasir POS", Icons.Default.AddShoppingCart)
    object Payments : Screen("payments", "Bayar", Icons.Default.Payments)
    object Products : Screen("products", "Produk", Icons.Default.Fastfood)
    object Categories : Screen("categories", "Kategori", Icons.Default.Category)
    object Tables : Screen("tables", "Meja QR", Icons.Default.QrCode)
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
                    Text(
                        when (currentRoute) {
                            Screen.Dashboard.route -> "Kuliner POS Kasir"
                            Screen.Orders.route -> "Kelola Pesanan"
                            Screen.CashierOrder.route -> "Kasir Manual"
                            Screen.Payments.route -> "Konfirmasi Pembayaran"
                            Screen.Products.route -> "Daftar Produk Menu"
                            Screen.Categories.route -> "Kategori Menu"
                            Screen.Tables.route -> "Meja Pelanggan"
                            Screen.Expenses.route -> "Pengeluaran Operasional"
                            Screen.Reports.route -> "Laporan Keuangan"
                            else -> "Kuliner Kasir POS"
                        }
                    )
                },
                actions = {
                    IconButton(onClick = {
                        tokenManager.clear()
                        onLogout()
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Keluar")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = { currentRoute = screen.route }
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
