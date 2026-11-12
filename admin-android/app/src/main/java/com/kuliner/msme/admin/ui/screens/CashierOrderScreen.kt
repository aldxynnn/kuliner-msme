package com.kuliner.msme.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
                // Ignore
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
                // Ignore
            } finally {
                isSubmitting = false
            }
        }
    }

    val totalAmount = cartItems.sumOf { it.product.price * it.quantity }
    val filteredProducts = if (selectedCategoryId == null) products else products.filter { it.categoryId == selectedCategoryId }

    Column(modifier = Modifier.fillMaxSize()) {
        // Options bar: Order type & table selection
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val isDineIn = selectedOrderType == "DINE_IN"
                    FilterChip(
                        selected = isDineIn,
                        onClick = { selectedOrderType = "DINE_IN" },
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
                        )
                    )

                    val isTakeAway = selectedOrderType == "TAKE_AWAY"
                    FilterChip(
                        selected = isTakeAway,
                        onClick = { selectedOrderType = "TAKE_AWAY" },
                        label = { Text("Take Away", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
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
                        )
                    )
                }

                if (selectedOrderType == "DINE_IN" && tables.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Pilih Meja Pelanggan:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        items(tables) { table ->
                            val isTableSelected = selectedTableNumber == table.tableNumber
                            FilterChip(
                                selected = isTableSelected,
                                onClick = { selectedTableNumber = table.tableNumber },
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
                                    selected = isTableSelected,
                                    selectedBorderColor = MaterialTheme.colorScheme.primary,
                                    borderColor = MaterialTheme.colorScheme.outlineVariant
                                )
                            )
                        }
                    }
                }
            }
        }

        // Categories Row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                val isSelected = selectedCategoryId == null
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategoryId = null },
                    label = { Text("Semua", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
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
            items(categories) { cat ->
                val isSelected = selectedCategoryId == cat.id
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategoryId = cat.id },
                    label = { Text(cat.name, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
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
                val imageUrl = if (!product.image.isNullOrBlank()) {
                    if (product.image.startsWith("/")) "http://10.0.2.2:3000${product.image}" else product.image
                } else null

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
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(product.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                        Text(formatRp(product.price), fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)

                        Spacer(modifier = Modifier.height(6.dp))

                        if (inCart != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { handleUpdateQuantity(product.id, -1) },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                                Text("${inCart.quantity}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                IconButton(
                                    onClick = { handleUpdateQuantity(product.id, 1) },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            }
                        } else {
                            Button(
                                onClick = { handleAddToCart(product) },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = product.isAvailable,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(if (product.isAvailable) "Tambah" else "Habis", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total (${cartItems.sumOf { it.quantity }} item)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(formatRp(totalAmount), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isCashConfirmedImmediately,
                            onCheckedChange = { isCashConfirmedImmediately = it }
                        )
                        Text("Konfirmasi Pembayaran Lunas Langsung", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = { handleCheckout() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        enabled = !isSubmitting,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp))
                        } else {
                            Text("Buat Pesanan Kasir", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
