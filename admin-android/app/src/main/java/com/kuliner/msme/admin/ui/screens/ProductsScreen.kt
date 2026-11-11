package com.kuliner.msme.admin.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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

@Composable
fun ProductsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    var isAddEditDialogVisible by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }

    fun loadData() {
        isLoading = true
        scope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                val catRes = api.getCategories()
                val prodRes = api.getProducts(categoryId = selectedCategoryId)

                if (catRes.isSuccessful && catRes.body() != null) {
                    categories = catRes.body()!!
                } else {
                    Toast.makeText(context, "Gagal memuat kategori: ${catRes.code()}", Toast.LENGTH_SHORT).show()
                }

                if (prodRes.isSuccessful && prodRes.body() != null) {
                    products = prodRes.body()!!
                } else {
                    Toast.makeText(context, "Gagal memuat produk: ${prodRes.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error koneksi: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(selectedCategoryId) {
        loadData()
    }

    fun handleToggleAvailability(productId: String, isAvailable: Boolean) {
        scope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                val res = api.toggleAvailability(productId, ToggleAvailabilityRequest(isAvailable))
                if (res.isSuccessful) {
                    Toast.makeText(context, "Status ketersediaan berhasil diubah", Toast.LENGTH_SHORT).show()
                    loadData()
                } else {
                    val errorBody = res.errorBody()?.string() ?: res.message()
                    Toast.makeText(context, "Gagal ubah status (${res.code()}): $errorBody", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun handleDeleteProduct(productId: String) {
        scope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                val res = api.deleteProduct(productId)
                if (res.isSuccessful) {
                    Toast.makeText(context, "Menu berhasil dihapus", Toast.LENGTH_SHORT).show()
                    loadData()
                } else {
                    val errorBody = res.errorBody()?.string() ?: res.message()
                    Toast.makeText(context, "Gagal menghapus (${res.code()}): $errorBody", Toast.LENGTH_LONG).show()
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Kelola Produk Menu", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Text("Atur harga dan ketersediaan menu AROMA", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }

            FloatingActionButton(
                onClick = {
                    editingProduct = null
                    isAddEditDialogVisible = true
                },
                modifier = Modifier.size(44.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah")
            }
        }

        // Category Filter
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

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Belum ada produk menu", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products) { product ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    text = "${product.category?.name ?: "Kategori"} • ${product.description ?: ""}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(formatRp(product.price), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = product.isAvailable,
                                    onCheckedChange = { handleToggleAvailability(product.id, it) }
                                )
                                IconButton(
                                    onClick = {
                                        editingProduct = product
                                        isAddEditDialogVisible = true
                                    }
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                                }
                                IconButton(
                                    onClick = { handleDeleteProduct(product.id) }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (isAddEditDialogVisible) {
        ProductFormDialog(
            categories = categories,
            product = editingProduct,
            onDismiss = { isAddEditDialogVisible = false },
            onSave = {
                isAddEditDialogVisible = false
                loadData()
            }
        )
    }
}

@Composable
fun ProductFormDialog(
    categories: List<Category>,
    product: Product?,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf(product?.name ?: "") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var priceStr by remember { mutableStateOf(product?.price?.toString() ?: "15000") }
    var categoryId by remember { mutableStateOf(product?.categoryId ?: categories.firstOrNull()?.id ?: "") }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun handleSave() {
        val price = priceStr.toIntOrNull() ?: 0
        if (name.isBlank() || categoryId.isBlank() || price <= 0) {
            errorMessage = "Mohon lengkapi nama, kategori, dan harga yang valid"
            return
        }

        isSubmitting = true
        errorMessage = null

        scope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                val req = CreateProductRequest(
                    categoryId = categoryId,
                    name = name.trim(),
                    description = description.ifBlank { null },
                    price = price,
                    isAvailable = product?.isAvailable ?: true
                )

                val response = if (product != null) {
                    api.updateProduct(product.id, req)
                } else {
                    api.createProduct(req)
                }

                if (response.isSuccessful) {
                    Toast.makeText(context, if (product != null) "Menu berhasil diperbarui" else "Menu berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    onSave()
                } else {
                    val errBody = response.errorBody()?.string() ?: response.message()
                    errorMessage = "Gagal (${response.code()}): $errBody"
                }
            } catch (e: Exception) {
                errorMessage = "Terjadi kesalahan: ${e.localizedMessage}"
            } finally {
                isSubmitting = false
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (product != null) "Edit Produk Menu" else "Tambah Produk Baru") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (errorMessage != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text("Pilih Kategori:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = categoryId == cat.id,
                            onClick = { categoryId = cat.id },
                            label = { Text(cat.name, fontSize = 11.sp) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Produk") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Harga (Rp)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { handleSave() }, enabled = !isSubmitting) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Simpan")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
