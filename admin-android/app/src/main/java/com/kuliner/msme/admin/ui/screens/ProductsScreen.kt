package com.kuliner.msme.admin.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kuliner.msme.admin.data.api.RetrofitClient
import com.kuliner.msme.admin.data.model.*
import com.kuliner.msme.admin.ui.components.*
import com.kuliner.msme.admin.ui.theme.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AromaCream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (!isSortingMode) 76.dp else 0.dp)
        ) {
            // Category Filter Pills + Reorder Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    modifier = Modifier.weight(1f),
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

                Surface(
                    onClick = { isSortingMode = !isSortingMode },
                    modifier = Modifier.defaultMinSize(minHeight = 36.dp),
                    shape = RoundedCornerShape(50),
                    color = if (isSortingMode) AromaTerracotta else AromaSurface,
                    border = if (isSortingMode) null else BorderStroke(1.dp, AromaBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.SwapVert,
                            contentDescription = "Atur Urutan",
                            tint = if (isSortingMode) AromaSurface else AromaTerracotta,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isSortingMode) "Selesai" else "Urutkan",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSortingMode) AromaSurface else AromaCharcoal
                        )
                    }
                }
            }

            // Reorder Action Bar
            if (isSortingMode) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 10.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = AromaTerracottaSoft,
                    border = BorderStroke(1.dp, AromaTerracottaBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Gunakan tombol panah naik/turun untuk menata urutan menu di Customer Web.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = AromaCharcoal
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    isSortingMode = false
                                    loadData()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, AromaBorder),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = AromaSurface,
                                    contentColor = AromaCharcoal
                                )
                            ) {
                                Text("Batal", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { handleSaveOrder() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AromaTerracotta)
                            ) {
                                Text("Simpan Urutan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            if (isLoading) {
                AromaLoadingState(message = "Memuat daftar menu...")
            } else if (products.isEmpty()) {
                AromaEmptyState(
                    icon = Icons.Default.Fastfood,
                    title = "Belum ada produk menu",
                    subtitle = "Tambahkan menu baru agar muncul di POS dan Customer Web."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(products) { index, product ->
                        val imageUrl = getProductImageUrl(product.image)

                        AromaCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 12.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(74.dp)
                                        .clip(RoundedCornerShape(10.dp))
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
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = (product.category?.name ?: "Menu").uppercase(),
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AromaTerracotta,
                                            letterSpacing = 0.4.sp
                                        )
                                        Text(
                                            text = "· ${if (product.isAvailable) "Tersedia" else "Habis"}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (product.isAvailable) AromaSuccess else AromaError
                                        )
                                    }

                                    Text(
                                        text = product.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = AromaCharcoal,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Text(
                                        text = formatRupiah(product.price),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AromaCharcoal
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                if (isSortingMode) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Surface(
                                            onClick = { if (index > 0) moveItem(index, -1) },
                                            enabled = index > 0,
                                            modifier = Modifier.size(34.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (index > 0) AromaTerracotta else AromaCreamSoft
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Default.ArrowUpward,
                                                    contentDescription = "Naik",
                                                    tint = if (index > 0) AromaSurface else AromaTextMuted,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                        Surface(
                                            onClick = { if (index < products.size - 1) moveItem(index, 1) },
                                            enabled = index < products.size - 1,
                                            modifier = Modifier.size(34.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (index < products.size - 1) AromaTerracotta else AromaCreamSoft
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Default.ArrowDownward,
                                                    contentDescription = "Turun",
                                                    tint = if (index < products.size - 1) AromaSurface else AromaTextMuted,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Column(
                                        horizontalAlignment = Alignment.End,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Switch(
                                            checked = product.isAvailable,
                                            onCheckedChange = { handleToggleAvailability(product.id, it) },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = AromaSurface,
                                                checkedTrackColor = AromaSuccess,
                                                uncheckedThumbColor = AromaSurface,
                                                uncheckedTrackColor = AromaBorder,
                                                uncheckedBorderColor = AromaBorder
                                            )
                                        )
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Surface(
                                                onClick = {
                                                    editingProduct = product
                                                    isDialogVisible = true
                                                },
                                                modifier = Modifier.size(34.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                color = AromaCreamSoft,
                                                border = BorderStroke(1.dp, AromaBorder)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        Icons.Default.Edit,
                                                        contentDescription = "Edit",
                                                        tint = AromaCharcoal,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                            Surface(
                                                onClick = { handleDeleteProduct(product.id) },
                                                modifier = Modifier.size(34.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                color = AromaErrorSoft,
                                                border = BorderStroke(1.dp, AromaErrorBorder)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        Icons.Default.Delete,
                                                        contentDescription = "Hapus",
                                                        tint = AromaError,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(10.dp)) }
                }
            }
        }

        // Primary Add Product CTA
        if (!isSortingMode) {
            Button(
                onClick = {
                    editingProduct = null
                    isDialogVisible = true
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AromaTerracotta,
                    contentColor = AromaSurface
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Tambah Menu Baru", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }

    if (isDialogVisible) {
        var name by remember(editingProduct) { mutableStateOf(editingProduct?.name ?: "") }
        var description by remember(editingProduct) { mutableStateOf(editingProduct?.description ?: "") }
        var priceStr by remember(editingProduct) { mutableStateOf(editingProduct?.price?.toString() ?: "") }
        var categoryId by remember(editingProduct) {
            mutableStateOf(editingProduct?.categoryId ?: categories.firstOrNull()?.id ?: "")
        }
        var isAvailable by remember(editingProduct) { mutableStateOf(editingProduct?.isAvailable ?: true) }
        var imageUrl by remember(editingProduct) { mutableStateOf(editingProduct?.image ?: "") }

        var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
        var isUploadingPhoto by remember { mutableStateOf(false) }

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
                            Toast.makeText(
                                context,
                                "Gagal mengunggah foto (${uploadRes.code()}): $errBody",
                                Toast.LENGTH_LONG
                            ).show()
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
            containerColor = AromaSurface,
            shape = RoundedCornerShape(18.dp),
            title = {
                Text(
                    text = if (editingProduct != null) "Edit Produk Menu" else "Tambah Menu Baru",
                    style = MaterialTheme.typography.titleLarge,
                    color = AromaCharcoal
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(132.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(enabled = !isUploadingPhoto) {
                                photoPickerLauncher.launch("image/*")
                            },
                        color = AromaCreamSoft,
                        border = BorderStroke(1.dp, AromaBorder)
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
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = AromaSurface.copy(alpha = 0.9f)
                                ) {
                                    CircularProgressIndicator(
                                        color = AromaTerracotta,
                                        strokeWidth = 2.5.dp,
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .size(24.dp)
                                    )
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = AromaSurface.copy(alpha = 0.94f),
                                    border = BorderStroke(1.dp, AromaTerracottaBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.AddAPhoto,
                                            contentDescription = "Pilih Foto",
                                            tint = AromaTerracotta,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = if (imageUrl.isBlank()) "Pilih Foto Menu" else "Ganti Foto",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AromaTerracotta
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        text = "Kategori Menu",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AromaCharcoal
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { cat ->
                            val isSelected = categoryId == cat.id
                            AromaFilterPill(
                                label = cat.name,
                                selected = isSelected,
                                onClick = { categoryId = cat.id }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Menu") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = aromaTextFieldColors()
                    )
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Harga (Rp)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = aromaTextFieldColors()
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Deskripsi Menu") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = aromaTextFieldColors()
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
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AromaTerracotta)
                ) {
                    Text("Simpan", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isDialogVisible = false }) {
                    Text("Batal", fontWeight = FontWeight.Bold, color = AromaTextSecondary)
                }
            }
        )
    }
}
