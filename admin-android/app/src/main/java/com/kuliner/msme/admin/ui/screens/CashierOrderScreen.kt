package com.kuliner.msme.admin.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kuliner.msme.admin.data.api.RetrofitClient
import com.kuliner.msme.admin.data.model.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

data class CartItem(
    val product: Product,
    var quantity: Int,
    var notes: String
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
    var orderType by remember { mutableStateOf("DINE_IN") } // "DINE_IN" or "Take Away"
    var selectedTable by remember { mutableStateOf<Table?>(null) }
    var customerName by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("CASH") } // "CASH" or "QRIS"

    val cartItems = remember { mutableStateListOf<CartItem>() }
    var isCheckoutDialogVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    fun loadData() {
        isLoading = true
        scope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                val catRes = api.getCategories()
                val prodRes = api.getProducts()
                val tableRes = api.getTables()

                if (catRes.isSuccessful && catRes.body() != null) categories = catRes.body()!!
                if (prodRes.isSuccessful && prodRes.body() != null) products = prodRes.body()!!
                if (tableRes.isSuccessful && tableRes.body() != null) tables = tableRes.body()!!
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

    val filteredProducts = if (selectedCategoryId == null) {
        products
    } else {
        products.filter { it.categoryId == selectedCategoryId }
    }

    val totalCartCount = cartItems.sumOf { it.quantity }
    val totalCartPrice = cartItems.sumOf { it.product.price * it.quantity }

    fun handleAddToCart(product: Product) {
        val existing = cartItems.find { it.product.id == product.id }
        if (existing != null) {
            existing.quantity += 1
        } else {
            cartItems.add(CartItem(product, 1, ""))
        }
    }

    fun handleUpdateQty(productId: String, delta: Int) {
        val index = cartItems.indexOfFirst { it.product.id == productId }
        if (index > -1) {
            val item = cartItems[index]
            val newQty = item.quantity + delta
            if (newQty <= 0) {
                cartItems.removeAt(index)
            } else {
                cartItems[index] = item.copy(quantity = newQty)
            }
        }
    }

    fun handleCreateOrder() {
        if (cartItems.isEmpty()) {
            Toast.makeText(context, "Keranjang masih kosong", Toast.LENGTH_SHORT).show()
            return
        }
        if (orderType == "DINE_IN" && selectedTable == null) {
            Toast.makeText(context, "Pilih meja terlebih dahulu untuk Dine In", Toast.LENGTH_SHORT).show()
            return
        }

        scope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                val payload = CreateOrderPayload(
                    customerName = customerName.ifBlank { null },
                    orderType = orderType,
                    tableNumber = if (orderType == "DINE_IN") selectedTable?.tableNumber else null,
                    tableId = if (orderType == "DINE_IN") selectedTable?.id else null,
                    paymentMethod = paymentMethod,
                    items = cartItems.map {
                        OrderItemPayload(
                            productId = it.product.id,
                            quantity = it.quantity,
                            notes = it.notes.ifBlank { null }
                        )
                    }
                )

                val res = api.createCashierOrder(payload)
                if (res.isSuccessful && res.body() != null) {
                    Toast.makeText(context, "Pesanan kasir berhasil dibuat!", Toast.LENGTH_SHORT).show()
                    cartItems.clear()
                    isCheckoutDialogVisible = false
                    onOrderCreated()
                } else {
                    val err = res.errorBody()?.string() ?: res.message()
                    Toast.makeText(context, "Gagal buat pesanan: $err", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun formatRp(amount: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        format.maximumFractionDigits = 0
        return format.format(amount)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Kasir POS", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Buat pesanan langsung dari meja kasir", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }

        // Order Type & Table Selection Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Tipe Pesanan Toggle with Terracotta Colors
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isDineIn = orderType == "DINE_IN"
                    FilterChip(
                        selected = isDineIn,
                        onClick = { orderType = "DINE_IN" },
                        label = { Text("Dine In (Makan di Tempat)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isDineIn,
                            selectedBorderColor = MaterialTheme.colorScheme.primary,
                            borderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    val isTakeAway = orderType == "TAKE_AWAY"
                    FilterChip(
                        selected = isTakeAway,
                        onClick = { orderType = "TAKE_AWAY"; selectedTable = null },
                        label = { Text("Take Away (Bawa Pulang)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isTakeAway,
                            selectedBorderColor = MaterialTheme.colorScheme.primary,
                            borderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Table selection if Dine In
                if (orderType == "DINE_IN") {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(tables) { table ->
                            val isSelected = selectedTable?.id == table.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedTable = table },
                                label = { Text(table.tableNumber, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                shape = RoundedCornerShape(8.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.primary,
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    labelColor = MaterialTheme.colorScheme.onSurface
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    selectedBorderColor = MaterialTheme.colorScheme.primary,
                                    borderColor = MaterialTheme.colorScheme.outlineVariant
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                val isSelected = selectedCategoryId == null
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategoryId = null },
                    label = { Text("Semua", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                        borderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
            }
            items(categories) { cat ->
                val isSelected = selectedCategoryId == cat.id
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategoryId = cat.id },
                    label = { Text(cat.name, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                        borderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Products Catalog Grid with Product Photo Preview
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredProducts) { product ->
                val inCart = cartItems.find { it.product.id == product.id }
                val imageUrl = getProductImageUrl(product.image)

                Card(
                    onClick = { if (product.isAvailable) handleAddToCart(product) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            if (imageUrl != null) {
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = product.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    Icons.Default.Fastfood,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            product.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            formatRp(product.price),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        if (inCart != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    "Di Keranjang: ${inCart.quantity}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Cart Summary Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("$totalCartCount item dipilih", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Text(formatRp(totalCartPrice), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                }

                Button(
                    onClick = { isCheckoutDialogVisible = true },
                    enabled = totalCartCount > 0,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Proses Checkout", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }

    if (isCheckoutDialogVisible) {
        AlertDialog(
            onDismissRequest = { isCheckoutDialogVisible = false },
            title = { Text("Konfirmasi Pesanan Kasir", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (orderType == "DINE_IN") "Tipe: Dine In (${selectedTable?.tableNumber ?: "Pilih Meja"})" else "Tipe: Take Away",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Nama Pelanggan (Opsional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Text("Metode Pembayaran:", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = paymentMethod == "CASH",
                            onClick = { paymentMethod = "CASH" },
                            label = { Text("Tunai (Cash)", fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                        FilterChip(
                            selected = paymentMethod == "QRIS",
                            onClick = { paymentMethod = "QRIS" },
                            label = { Text("QRIS", fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Total: ${formatRp(totalCartPrice)}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                }
            },
            confirmButton = {
                Button(
                    onClick = { handleCreateOrder() },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Buat Pesanan Sekarang")
                }
            },
            dismissButton = {
                TextButton(onClick = { isCheckoutDialogVisible = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
