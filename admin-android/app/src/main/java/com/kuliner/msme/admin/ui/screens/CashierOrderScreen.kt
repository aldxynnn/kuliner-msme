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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kuliner.msme.admin.data.api.RetrofitClient
import com.kuliner.msme.admin.data.model.*
import com.kuliner.msme.admin.ui.components.*
import com.kuliner.msme.admin.ui.theme.*
import kotlinx.coroutines.launch

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

    val cartItems = remember { mutableStateListOf<CartItem>() }
    var isCartSheetVisible by remember { mutableStateOf(false) }
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
        val index = cartItems.indexOfFirst { it.product.id == product.id }
        if (index > -1) {
            val existing = cartItems[index]
            cartItems[index] = existing.copy(quantity = existing.quantity + 1)
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

    fun handleRemoveItem(productId: String) {
        cartItems.removeAll { it.product.id == productId }
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
                    isCartSheetVisible = false
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AromaCream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (totalCartCount > 0) 84.dp else 0.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Unified Commercial Segmented Order Mode Switcher
            AromaSegmentedControl(
                options = listOf(
                    "Dine In" to Icons.Default.TableBar,
                    "Take Away" to Icons.Default.ShoppingBag
                ),
                selectedIndex = if (orderType == "DINE_IN") 0 else 1,
                onSelect = { index ->
                    if (index == 0) {
                        orderType = "DINE_IN"
                    } else {
                        orderType = "TAKE_AWAY"
                        selectedTable = null
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Table Selection Row (Dine In)
            if (orderType == "DINE_IN") {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PILIH MEJA",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = AromaTextMuted
                        )
                        Text(
                            text = selectedTable?.tableNumber ?: "Belum dipilih",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTable != null) AromaTerracotta else AromaTextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(tables) { table ->
                            val isSelected = selectedTable?.id == table.id
                            Surface(
                                onClick = { selectedTable = table },
                                modifier = Modifier.defaultMinSize(minHeight = 36.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) AromaTerracottaSoft else AromaSurface,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) AromaTerracotta else AromaBorder
                                )
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = table.tableNumber,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) AromaTerracotta else AromaCharcoal
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Category Filter Rail
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    AromaFilterPill(
                        label = "Semua",
                        selected = selectedCategoryId == null,
                        onClick = { selectedCategoryId = null }
                    )
                }
                items(categories) { cat ->
                    AromaFilterPill(
                        label = cat.name,
                        selected = selectedCategoryId == cat.id,
                        onClick = { selectedCategoryId = cat.id }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Photo-Forward 2-Column Register Grid
            if (isLoading) {
                AromaLoadingState(message = "Memuat daftar menu POS...")
            } else if (filteredProducts.isEmpty()) {
                AromaEmptyState(
                    icon = Icons.Default.Fastfood,
                    title = "Tidak ada produk menu",
                    subtitle = "Pilih kategori lain atau tambahkan menu terlebih dahulu."
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredProducts) { product ->
                        val inCart = cartItems.find { it.product.id == product.id }
                        val imageUrl = getProductImageUrl(product.image)

                        AromaCard(
                            onClick = { if (product.isAvailable) handleAddToCart(product) },
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = if (inCart != null) AromaTerracotta else AromaBorder,
                            cornerRadius = 12.dp
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                                        .background(AromaCreamSoft),
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
                                            tint = AromaTextMuted,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }

                                    if (!product.isAvailable) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(AromaCharcoal.copy(alpha = 0.65f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = AromaSurface
                                            ) {
                                                Text(
                                                    text = "HABIS",
                                                    fontFamily = FontFamily.Monospace,
                                                    color = AromaCharcoal,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }
                                        }
                                    }

                                    if (inCart != null) {
                                        Surface(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(8.dp),
                                            shape = RoundedCornerShape(6.dp),
                                            color = AromaSuccess
                                        ) {
                                            Text(
                                                text = "${inCart.quantity}×",
                                                fontFamily = FontFamily.Monospace,
                                                color = AromaSurface,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = product.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        lineHeight = 17.sp,
                                        maxLines = 2,
                                        minLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        color = AromaCharcoal
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = formatRupiah(product.price),
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AromaCharcoal,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Spacer(modifier = Modifier.width(4.dp))

                                        Surface(
                                            onClick = { if (product.isAvailable) handleAddToCart(product) },
                                            modifier = Modifier.size(34.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            color = when {
                                                !product.isAvailable -> AromaCreamTrack
                                                inCart != null -> AromaTerracotta
                                                else -> AromaCharcoal
                                            }
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Default.Add,
                                                    contentDescription = "Tambah",
                                                    tint = if (product.isAvailable) AromaSurface else AromaTextMuted,
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

        // Deep Roast Ink Floating Register Checkout Bar
        if (totalCartCount > 0) {
            Surface(
                onClick = { isCartSheetVisible = true },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(14.dp),
                color = AromaCharcoal,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(38.dp),
                            shape = RoundedCornerShape(9.dp),
                            color = AromaTerracotta
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "$totalCartCount",
                                    fontFamily = FontFamily.Monospace,
                                    color = AromaSurface,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "TOTAL PESANAN",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = AromaSurface.copy(alpha = 0.65f),
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.6.sp
                            )
                            Text(
                                text = formatRupiah(totalCartPrice),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = AromaSurface
                            )
                        }
                    }

                    Button(
                        onClick = { isCartSheetVisible = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AromaSurface,
                            contentColor = AromaCharcoal
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text("Checkout", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    // Checkout Bottom Sheet
    if (isCartSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { isCartSheetVisible = false },
            containerColor = AromaSurface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TIKET KASIR",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = AromaTerracotta
                        )
                        Text(
                            text = "Keranjang Pesanan",
                            style = MaterialTheme.typography.titleLarge,
                            color = AromaCharcoal
                        )
                        Text(
                            text = if (orderType == "DINE_IN") {
                                "Dine In · ${selectedTable?.tableNumber ?: "Meja belum dipilih"}"
                            } else {
                                "Take Away · $totalCartCount item"
                            },
                            fontSize = 12.sp,
                            color = AromaTextMuted
                        )
                    }
                    IconButton(
                        onClick = { isCartSheetVisible = false },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = AromaCharcoal)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (cartItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Keranjang masih kosong",
                            color = AromaTextMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(cartItems) { item ->
                            val itemImageUrl = getProductImageUrl(item.product.image)

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = AromaCreamSoft,
                                border = BorderStroke(1.dp, AromaBorder)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(AromaSurface),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (itemImageUrl != null) {
                                            AsyncImage(
                                                model = itemImageUrl,
                                                contentDescription = item.product.name,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Icon(
                                                Icons.Default.Fastfood,
                                                contentDescription = null,
                                                tint = AromaTextMuted,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.product.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = AromaCharcoal,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = formatRupiah(item.product.price * item.quantity),
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AromaTerracotta
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            onClick = { handleUpdateQty(item.product.id, -1) },
                                            modifier = Modifier.size(32.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            color = AromaSurface,
                                            border = BorderStroke(1.dp, AromaBorder)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Default.Remove,
                                                    contentDescription = "Kurang",
                                                    tint = AromaCharcoal,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = "${item.quantity}",
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = AromaCharcoal,
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        )

                                        Surface(
                                            onClick = { handleUpdateQty(item.product.id, 1) },
                                            modifier = Modifier.size(32.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            color = AromaCharcoal
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Default.Add,
                                                    contentDescription = "Tambah",
                                                    tint = AromaSurface,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        IconButton(
                                            onClick = { handleRemoveItem(item.product.id) },
                                            modifier = Modifier.size(34.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Hapus",
                                                tint = AromaError,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                AromaDashedDivider()
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Nama Pelanggan (Opsional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = aromaTextFieldColors()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Metode Pembayaran",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AromaCharcoal
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val isCash = paymentMethod == "CASH"
                    Surface(
                        onClick = { paymentMethod = "CASH" },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isCash) AromaCharcoal else AromaSurface,
                        border = BorderStroke(
                            1.dp,
                            if (isCash) AromaCharcoal else AromaBorder
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Payments,
                                contentDescription = null,
                                tint = if (isCash) AromaSurface else AromaTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Tunai (Cash)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCash) AromaSurface else AromaCharcoal
                            )
                        }
                    }

                    val isQris = paymentMethod == "QRIS"
                    Surface(
                        onClick = { paymentMethod = "QRIS" },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isQris) AromaCharcoal else AromaSurface,
                        border = BorderStroke(
                            1.dp,
                            if (isQris) AromaCharcoal else AromaBorder
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.QrCode,
                                contentDescription = null,
                                tint = if (isQris) AromaSurface else AromaTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "QRIS",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isQris) AromaSurface else AromaCharcoal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Pembayaran",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = AromaTextSecondary
                    )
                    Text(
                        text = formatRupiah(totalCartPrice),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = AromaCharcoal
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { handleCreateOrder() },
                    enabled = cartItems.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AromaTerracotta,
                        contentColor = AromaSurface
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text("Proses Pesanan", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}
