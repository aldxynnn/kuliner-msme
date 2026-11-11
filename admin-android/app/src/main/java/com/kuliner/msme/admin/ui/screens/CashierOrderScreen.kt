package com.kuliner.msme.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kuliner.msme.admin.data.api.RetrofitClient
import com.kuliner.msme.admin.data.model.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

data class SelectedCartItem(
    val product: Product,
    var quantity: Int,
    var notes: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashierOrderScreen(
    onOrderCreated: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var tables by remember { mutableStateOf<List<Table>>(emptyList()) }

    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var selectedOrderType by remember { mutableStateOf("DINE_IN") }
    var selectedTableNumber by remember { mutableStateOf<String?>(null) }
    var customerName by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("CASH") }
    var isCashConfirmedImmediately by remember { mutableStateOf(true) }

    val cartItems = remember { mutableStateListOf<SelectedCartItem>() }
    var isSubmitting by remember { mutableStateOf(false) }

    fun loadData() {
        scope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                val catRes = api.getCategories()
                val prodRes = api.getProducts()
                val tblRes = api.getTables()

                if (catRes.isSuccessful && catRes.body() != null) categories = catRes.body()!!
                if (prodRes.isSuccessful && prodRes.body() != null) products = prodRes.body()!!
                if (tblRes.isSuccessful && tblRes.body() != null) {
                    tables = tblRes.body()!!
                    if (tables.isNotEmpty()) selectedTableNumber = tables.first().tableNumber
                }
            } catch (e: Exception) {
                // Ignore error
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

    fun handleAddToCart(product: Product) {
        val existing = cartItems.find { it.product.id == product.id }
        if (existing != null) {
            existing.quantity += 1
            // Force recompose
            val idx = cartItems.indexOf(existing)
            cartItems[idx] = existing.copy(quantity = existing.quantity)
        } else {
            cartItems.add(SelectedCartItem(product, 1))
        }
    }

    fun handleUpdateQuantity(productId: String, delta: Int) {
        val existing = cartItems.find { it.product.id == productId } ?: return
        val newQty = existing.quantity + delta
        if (newQty <= 0) {
            cartItems.remove(existing)
        } else {
            val idx = cartItems.indexOf(existing)
            cartItems[idx] = existing.copy(quantity = newQty)
        }
    }

    fun handleCheckout() {
        if (cartItems.isEmpty()) return
        if (selectedOrderType == "DINE_IN" && selectedTableNumber.isNullOrBlank()) return

        isSubmitting = true
        scope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                val payload = CreateOrderPayload(
                    customerName = customerName.ifBlank { "Pelanggan Kasir" },
                    orderType = selectedOrderType,
                    tableNumber = if (selectedOrderType == "DINE_IN") selectedTableNumber else null,
                    paymentMethod = paymentMethod,
                    items = cartItems.map { OrderItemPayload(it.product.id, it.quantity, it.notes.ifBlank { null }) },
                    paymentStatus = if (isCashConfirmedImmediately) "PAID" else "PENDING"
                )

                val response = api.createCashierOrder(payload)
                if (response.isSuccessful) {
                    cartItems.clear()
                    onOrderCreated()
                }
            } catch (e: Exception) {
                // Ignore error
            } finally {
                isSubmitting = false
            }
        }
    }

    val totalAmount = cartItems.sumOf { it.product.price * it.quantity }
    val filteredProducts = if (selectedCategoryId == null) products else products.filter { it.categoryId == selectedCategoryId }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Pesan Baru (Kasir)", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Text("Buat pesanan langsung di kasir", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }

        // Options bar: Order type & table selection
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedOrderType == "DINE_IN",
                        onClick = { selectedOrderType = "DINE_IN" },
                        label = { Text("Dine In (Makan di Tempat)") }
                    )
                    FilterChip(
                        selected = selectedOrderType == "TAKE_AWAY",
                        onClick = { selectedOrderType = "TAKE_AWAY" },
                        label = { Text("Take Away") }
                    )
                }

                if (selectedOrderType == "DINE_IN" && tables.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Pilih Meja:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        items(tables) { table ->
                            FilterChip(
                                selected = selectedTableNumber == table.tableNumber,
                                onClick = { selectedTableNumber = table.tableNumber },
                                label = { Text(table.tableNumber, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Categories Row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategoryId == null,
                    onClick = { selectedCategoryId = null },
                    label = { Text("Semua") }
                )
            }
            items(categories) { cat ->
                FilterChip(
                    selected = selectedCategoryId == cat.id,
                    onClick = { selectedCategoryId = cat.id },
                    label = { Text(cat.name) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Products Catalog Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredProducts) { product ->
                val inCart = cartItems.find { it.product.id == product.id }
                Card(
                    onClick = { if (product.isAvailable) handleAddToCart(product) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(product.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                        Text(formatRp(product.price), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)

                        Spacer(modifier = Modifier.height(8.dp))

                        if (inCart != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { handleUpdateQuantity(product.id, -1) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = null)
                                }
                                Text("${inCart.quantity}", fontWeight = FontWeight.Bold)
                                IconButton(
                                    onClick = { handleUpdateQuantity(product.id, 1) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                }
                            }
                        } else {
                            Button(
                                onClick = { handleAddToCart(product) },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = product.isAvailable,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(if (product.isAvailable) "Tambah" else "Habis", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Cart Summary Bottom Bar
        if (cartItems.isNotEmpty()) {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total (${cartItems.sumOf { it.quantity }} item)", fontWeight = FontWeight.Bold)
                        Text(formatRp(totalAmount), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isCashConfirmedImmediately,
                            onCheckedChange = { isCashConfirmedImmediately = it }
                        )
                        Text("Konfirmasi Pembayaran Lunas Langsung", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { handleCheckout() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        enabled = !isSubmitting,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                        } else {
                            Text("Buat Pesanan Kasir", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
