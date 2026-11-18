package com.kuliner.msme.admin.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    var orderType by remember { mutableStateOf("DINE_IN") } // "DINE_IN" or "TAKE_AWAY"
    var selectedTable by remember { mutableStateOf<Table?>(null) }
    var customerName by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("CASH") } // "CASH" or "QRIS"
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

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

    val filteredProducts = products.filter { product ->
        val matchesCategory = selectedCategoryId == null || product.categoryId == selectedCategoryId
        val matchesSearch = searchQuery.isBlank() || product.name.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF8F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (totalCartCount > 0) 84.dp else 0.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Segmented Order Type Selector (Matching Reference Screen 2)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFFF3EFEA)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val isDineIn = orderType == "DINE_IN"
                    Surface(
                        onClick = { orderType = "DINE_IN" },
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = if (isDineIn) Color(0xFFE25822) else Color.Transparent,
                        shadowElevation = if (isDineIn) 2.dp else 0.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.TableBar,
                                contentDescription = null,
                                tint = if (isDineIn) Color.White else Color(0xFF2B211D),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Dine In",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isDineIn) Color.White else Color(0xFF2B211D)
                            )
                        }
                    }

                    val isTakeAway = orderType == "TAKE_AWAY"
                    Surface(
                        onClick = { orderType = "TAKE_AWAY"; selectedTable = null },
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = if (isTakeAway) Color(0xFFE25822) else Color.Transparent,
                        shadowElevation = if (isTakeAway) 2.dp else 0.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.ShoppingBag,
                                contentDescription = null,
                                tint = if (isTakeAway) Color.White else Color(0xFF2B211D),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Take Away",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isTakeAway) Color.White else Color(0xFF2B211D)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Table Selection Row (If Dine In)
            if (orderType == "DINE_IN") {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "Pilih Meja",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2B211D)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tables) { table ->
                            val isSelected = selectedTable?.id == table.id
                            Surface(
                                onClick = { selectedTable = table },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFFFFF0E8) else Color.White,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) Color(0xFFE25822) else Color(0xFFE9E1D9)
                                )
                            ) {
                                Text(
                                    text = table.tableNumber,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFFE25822) else Color(0xFF2B211D),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Category Horizontal Selector Pills
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    val isSelected = selectedCategoryId == null
                    Surface(
                        onClick = { selectedCategoryId = null },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) Color(0xFFE25822) else Color.White,
                        shadowElevation = if (isSelected) 2.dp else 0.dp,
                        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE9E1D9))
                    ) {
                        Text(
                            text = "Semua",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color(0xFF2B211D),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
                items(categories) { cat ->
                    val isSelected = selectedCategoryId == cat.id
                    Surface(
                        onClick = { selectedCategoryId = cat.id },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) Color(0xFFE25822) else Color.White,
                        shadowElevation = if (isSelected) 2.dp else 0.dp,
                        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE9E1D9))
                    ) {
                        Text(
                            text = cat.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color(0xFF2B211D),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2-Column Product Cards Grid
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFE25822))
                }
            } else if (filteredProducts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Tidak ada produk menu",
                        color = Color(0xFF81766F),
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredProducts) { product ->
                        val inCart = cartItems.find { it.product.id == product.id }
                        val imageUrl = getProductImageUrl(product.image)

                        Surface(
                            onClick = { if (product.isAvailable) handleAddToCart(product) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            color = Color.White,
                            shadowElevation = 2.dp,
                            border = BorderStroke(1.dp, Color(0xFFE9E1D9))
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Dominant Photo Top Section
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                        .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
                                        .background(Color(0xFFFFF0E8)),
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
                                            tint = Color(0xFFE25822),
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }

                                    if (!product.isAvailable) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Black.copy(alpha = 0.5f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "HABIS",
                                                color = Color.White,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    if (inCart != null) {
                                        Surface(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(8.dp),
                                            shape = CircleShape,
                                            color = Color(0xFFE25822)
                                        ) {
                                            Text(
                                                text = "${inCart.quantity}",
                                                color = Color.White,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                // Info & Plus Action Button Section
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = product.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        color = Color(0xFF2B211D)
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = formatRp(product.price),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF2B211D)
                                        )

                                        // Orange Round Plus Button
                                        Surface(
                                            onClick = { if (product.isAvailable) handleAddToCart(product) },
                                            modifier = Modifier.size(32.dp),
                                            shape = CircleShape,
                                            color = Color(0xFFE25822)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Default.Add,
                                                    contentDescription = "Tambah",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Bottom Cart Bar
        if (totalCartCount > 0) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(20.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, Color(0xFFE9E1D9))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(38.dp),
                            shape = CircleShape,
                            color = Color(0xFFFFF0E8)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.ShoppingBag,
                                    contentDescription = null,
                                    tint = Color(0xFFE25822),
                                    modifier = Modifier.size(20.dp)
                                )
                                Surface(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(16.dp),
                                    shape = CircleShape,
                                    color = Color(0xFFE25822)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "$totalCartCount",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = formatRp(totalCartPrice),
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = Color(0xFF2B211D)
                        )
                    }

                    // Orange Action Pill Button "Lanjut >"
                    Button(
                        onClick = { isCheckoutDialogVisible = true },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE25822)),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Lanjut", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }

    if (isCheckoutDialogVisible) {
        AlertDialog(
            onDismissRequest = { isCheckoutDialogVisible = false },
            title = { Text("Konfirmasi Pesanan Kasir", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFF0E8),
                        border = BorderStroke(1.dp, Color(0xFFE25822).copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = if (orderType == "DINE_IN") "Tipe: Dine In (${selectedTable?.tableNumber ?: "Pilih Meja"})" else "Tipe: Take Away",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE25822),
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Nama Pelanggan (Opsional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
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
                    Text("Total: ${formatRp(totalCartPrice)}", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Color(0xFFE25822))
                }
            },
            confirmButton = {
                Button(
                    onClick = { handleCreateOrder() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE25822))
                ) {
                    Text("Buat Pesanan Sekarang", fontWeight = FontWeight.Bold)
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
