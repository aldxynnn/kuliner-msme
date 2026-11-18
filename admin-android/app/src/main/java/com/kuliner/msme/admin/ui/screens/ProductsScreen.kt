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
import androidx.compose.foundation.lazy.itemsIndexed
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
    var isSortingMode by remember { mutableStateOf(false) }

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

    fun moveItem(index: Int, direction: Int) {
        val targetIndex = index + direction
        if (targetIndex in products.indices) {
            val mutableList = products.toMutableList()
            val item = mutableList.removeAt(index)
            mutableList.add(targetIndex, item)
            products = mutableList
        }
    }

    fun handleSaveOrder() {
        scope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                val res = api.reorderProducts(ReorderProductsRequest(products.map { it.id }))
                if (res.isSuccessful) {
                    Toast.makeText(context, "Urutan menu berhasil disimpan", Toast.LENGTH_SHORT).show()
                    isSortingMode = false
                    loadData()
                } else {
                    val code = res.code()
                    val errorBody = res.errorBody()?.string() ?: res.message()
                    Toast.makeText(context, "Gagal simpan urutan ($code): $errorBody", Toast.LENGTH_LONG).show()
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
                .padding(bottom = 76.dp)
        ) {
            // Reorder Action Bar if active
            if (isSortingMode) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFFF0E8),
                    border = BorderStroke(1.dp, Color(0xFFE25822).copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Gunakan panah untuk mengurutkan menu",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE25822)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = { handleSaveOrder() },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE25822)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Simpan Urutan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = {
                                    isSortingMode = false
                                    loadData()
                                },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Batal", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Category Filter Pills & Atur Urutan Action Button Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    modifier = Modifier.weight(1f),
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
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
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
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Atur Urutan Toggle Action Button
                Surface(
                    onClick = { isSortingMode = !isSortingMode },
                    shape = RoundedCornerShape(18.dp),
                    color = if (isSortingMode) Color(0xFFE25822) else Color.White,
                    shadowElevation = 2.dp,
                    border = if (isSortingMode) null else BorderStroke(1.dp, Color(0xFFE9E1D9))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.SwapVert,
                            contentDescription = "Atur Urutan",
                            tint = if (isSortingMode) Color.White else Color(0xFFE25822),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isSortingMode) "Selesai" else "Atur Urutan",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSortingMode) Color.White else Color(0xFFE25822)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFE25822))
                }
            } else if (products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tidak ada produk menu", color = Color(0xFF81766F), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(products) { index, product ->
                        val imageUrl = getProductImageUrl(product.image)

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            color = Color.White,
                            shadowElevation = 2.dp,
                            border = BorderStroke(1.dp, Color(0xFFE9E1D9))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Dominant Food Photography Thumbnail
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(RoundedCornerShape(18.dp))
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
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Surface(
                                        color = Color(0xFFFFF0E8),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = product.category?.name ?: "Menu",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFE25822),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = product.name,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = Color(0xFF2B211D)
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = formatRp(product.price),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFE25822)
                                    )
                                }

                                if (isSortingMode) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = { moveItem(index, -1) },
                                            enabled = index > 0,
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.ArrowUpward, contentDescription = "Naik", tint = Color(0xFFE25822), modifier = Modifier.size(18.dp))
                                        }
                                        IconButton(
                                            onClick = { moveItem(index, 1) },
                                            enabled = index < products.size - 1,
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.ArrowDownward, contentDescription = "Turun", tint = Color(0xFFE25822), modifier = Modifier.size(18.dp))
                                        }
                                    }
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Switch(
                                            checked = product.isAvailable,
                                            onCheckedChange = { handleToggleAvailability(product.id, it) }
                                        )
                                        IconButton(
                                            onClick = {
                                                editingProduct = product
                                                isDialogVisible = true
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF2B211D), modifier = Modifier.size(16.dp))
                                        }
                                        IconButton(
                                            onClick = { handleDeleteProduct(product.id) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFFC94136), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Prominent Full-Width Floating Action Button at Bottom
        if (!isSortingMode) {
            Button(
                onClick = {
                    editingProduct = null
                    isDialogVisible = true
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE25822)),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tambah Menu Baru", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }

    if (isDialogVisible) {
        var name by remember(editingProduct) { mutableStateOf(editingProduct?.name ?: "") }
        var description by remember(editingProduct) { mutableStateOf(editingProduct?.description ?: "") }
        var priceStr by remember(editingProduct) { mutableStateOf(editingProduct?.price?.toString() ?: "") }
        var categoryId by remember(editingProduct) { mutableStateOf(editingProduct?.categoryId ?: categories.firstOrNull()?.id ?: "") }
        var isAvailable by remember(editingProduct) { mutableStateOf(editingProduct?.isAvailable ?: true) }
        var imageUrl by remember(editingProduct) { mutableStateOf(editingProduct?.image ?: "") }

        var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
        var isUploadingPhoto by remember { mutableStateOf(false) }

        // Photo Picker launcher
        val photoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                selectedImageUri = uri
                isUploadingPhoto = true
                scope.launch {
                    try {
                        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
                        val extension = when {
                            mimeType.contains("png") -> ".png"
                            mimeType.contains("webp") -> ".webp"
                            else -> ".jpg"
                        }
                        val file = File(context.cacheDir, "upload_product_${System.currentTimeMillis()}$extension")
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            FileOutputStream(file).use { output ->
                                input.copyTo(output)
                            }
                        }

                        val reqFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                        val body = MultipartBody.Part.createFormData("file", file.name, reqFile)

                        val api = RetrofitClient.getApiService(context)
                        val uploadRes = api.uploadProductImage(body)
                        if (uploadRes.isSuccessful && uploadRes.body() != null) {
                            imageUrl = uploadRes.body()!!.imageUrl
                            Toast.makeText(context, "Foto berhasil diunggah", Toast.LENGTH_SHORT).show()
                        } else {
                            val errBody = uploadRes.errorBody()?.string() ?: uploadRes.message()
                            Toast.makeText(context, "Gagal mengunggah foto (${uploadRes.code()}): $errBody", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error upload: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    } finally {
                        isUploadingPhoto = false
                    }
                }
            }
        }

        AlertDialog(
            onDismissRequest = { isDialogVisible = false },
            title = { Text(if (editingProduct != null) "Edit Produk Menu" else "Tambah Menu Baru", fontWeight = FontWeight.Black, fontSize = 17.sp, color = Color(0xFF2B211D)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Image Picker Area
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        color = Color(0xFFFFF0E8),
                        border = BorderStroke(1.dp, Color(0xFFE25822).copy(alpha = 0.3f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            val displayUrl = getProductImageUrl(imageUrl)

                            if (displayUrl != null) {
                                AsyncImage(
                                    model = displayUrl,
                                    contentDescription = "Preview Menu",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            if (isUploadingPhoto) {
                                CircularProgressIndicator(color = Color(0xFFE25822))
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    IconButton(onClick = { photoPickerLauncher.launch("image/*") }) {
                                        Icon(Icons.Default.AddAPhoto, contentDescription = "Pilih Foto", tint = Color(0xFFE25822))
                                    }
                                    Text(
                                        if (imageUrl.isBlank()) "Pilih Foto Menu (Galeri)" else "Ganti Foto Menu",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE25822)
                                    )
                                }
                            }
                        }
                    }

                    // Category Selection Filter Chips
                    Text(
                        text = "Kategori Menu",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2B211D)
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(categories) { cat ->
                            val isSelected = categoryId == cat.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { categoryId = cat.id },
                                label = { Text(cat.name, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                shape = RoundedCornerShape(8.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFFFF0E8),
                                    selectedLabelColor = Color(0xFFE25822),
                                    containerColor = Color.White,
                                    labelColor = Color(0xFF2B211D)
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    selectedBorderColor = Color(0xFFE25822),
                                    borderColor = Color(0xFFE9E1D9)
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Menu") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Harga (Rp)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Deskripsi Menu") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
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
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE25822))
                ) {
                    Text("Simpan", fontWeight = FontWeight.Bold)
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
