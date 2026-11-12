package com.kuliner.msme.admin.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    var isDialogVisible by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }

    fun loadData() {
        isLoading = true
        scope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                val catRes = api.getCategories()
                val prodRes = api.getProducts(categoryId = selectedCategoryId)

                if (catRes.isSuccessful && catRes.body() != null) categories = catRes.body()!!
                if (prodRes.isSuccessful && prodRes.body() != null) products = prodRes.body()!!
            } catch (e: Exception) {
                // Ignore
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
                    Toast.makeText(context, "Status ketersediaan diperbarui", Toast.LENGTH_SHORT).show()
                    loadData()
                } else {
                    val errorBody = res.errorBody()?.string() ?: res.message()
                    Toast.makeText(context, "Gagal ubah status ($errorBody)", Toast.LENGTH_LONG).show()
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
                    Toast.makeText(context, "Gagal hapus menu ($errorBody)", Toast.LENGTH_LONG).show()
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Katalog Produk Menu", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Kelola harga, ketersediaan, dan foto menu", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }

            IconButton(onClick = {
                editingProduct = null
                isDialogVisible = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah")
            }
        }

        // Category Filter Chips with Brand Terracotta Selection Colors
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

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tidak ada produk menu", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products) { product ->
                    val imageUrl = if (!product.image.isNullOrBlank()) {
                        if (product.image.startsWith("/")) "http://10.0.2.2:3000${product.image}" else product.image
                    } else null

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Thumbnail
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
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

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    text = "${product.category?.name ?: "Menu"} • ${formatRp(product.price)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Switch(
                                    checked = product.isAvailable,
                                    onCheckedChange = { handleToggleAvailability(product.id, it) }
                                )
                                IconButton(onClick = {
                                    editingProduct = product
                                    isDialogVisible = true
                                }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                                }
                                IconButton(onClick = { handleDeleteProduct(product.id) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (isDialogVisible) {
        var name by remember { mutableStateOf(editingProduct?.name ?: "") }
        var description by remember { mutableStateOf(editingProduct?.description ?: "") }
        var priceStr by remember { mutableStateOf(editingProduct?.price?.toString() ?: "") }
        var categoryId by remember { mutableStateOf(editingProduct?.categoryId ?: categories.firstOrNull()?.id ?: "") }
        var isAvailable by remember { mutableStateOf(editingProduct?.isAvailable ?: true) }
        var imageUrl by remember { mutableStateOf(editingProduct?.image ?: "") }

        var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
        var isUploadingPhoto by remember { mutableStateOf(false) }

        // Photo Picker launcher
        val photoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                selectedImageUri = uri
                // Automatically upload photo
                isUploadingPhoto = true
                scope.launch {
                    try {
                        val file = File(context.cacheDir, "upload_product.jpg")
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            FileOutputStream(file).use { output ->
                                input.copyTo(output)
                            }
                        }

                        val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        val body = MultipartBody.Part.createFormData("file", file.name, reqFile)

                        val api = RetrofitClient.getApiService(context)
                        val uploadRes = api.uploadProductImage(body)
                        if (uploadRes.isSuccessful && uploadRes.body() != null) {
                            imageUrl = uploadRes.body()!!.imageUrl
                            Toast.makeText(context, "Foto berhasil diunggah", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Gagal mengunggah foto", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error upload: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    } finally {
                        isUploadingPhoto = false
                    }
                }
            }
        }

        AlertDialog(
            onDismissRequest = { isDialogVisible = false },
            title = { Text(if (editingProduct != null) "Edit Produk Menu" else "Tambah Menu Baru", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Image Picker Area
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            val displayUrl = if (imageUrl.isNotBlank()) {
                                if (imageUrl.startsWith("/")) "http://10.0.2.2:3000$imageUrl" else imageUrl
                            } else null

                            if (displayUrl != null) {
                                AsyncImage(
                                    model = displayUrl,
                                    contentDescription = "Preview Menu",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            if (isUploadingPhoto) {
                                CircularProgressIndicator()
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    IconButton(onClick = { photoPickerLauncher.launch("image/*") }) {
                                        Icon(Icons.Default.AddAPhoto, contentDescription = "Pilih Foto", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    Text(
                                        if (imageUrl.isBlank()) "Pilih Foto Menu (Galeri)" else "Ganti Foto Menu",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Menu") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Harga (Rp)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Deskripsi Menu") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = priceStr.toIntOrNull() ?: 0
                        if (name.isNotBlank() && p > 0 && categoryId.isNotBlank()) {
                            scope.launch {
                                val api = RetrofitClient.getApiService(context)
                                val req = CreateProductRequest(
                                    categoryId = categoryId,
                                    name = name.trim(),
                                    description = description.ifBlank { null },
                                    price = p,
                                    image = imageUrl.ifBlank { null },
                                    isAvailable = isAvailable
                                )
                                val res = if (editingProduct != null) {
                                    api.updateProduct(editingProduct!!.id, req)
                                } else {
                                    api.createProduct(req)
                                }
                                if (res.isSuccessful) {
                                    Toast.makeText(context, "Menu berhasil disimpan", Toast.LENGTH_SHORT).show()
                                    isDialogVisible = false
                                    loadData()
                                } else {
                                    val err = res.errorBody()?.string() ?: res.message()
                                    Toast.makeText(context, "Gagal simpan: $err", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { isDialogVisible = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
