package com.kuliner.msme.admin.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kuliner.msme.admin.data.api.RetrofitClient
import com.kuliner.msme.admin.data.model.Category
import com.kuliner.msme.admin.data.model.CreateCategoryRequest
import kotlinx.coroutines.launch

@Composable
fun CategoriesScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isDialogVisible by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<Category?>(null) }

    fun loadData() {
        isLoading = true
        scope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                val res = api.getCategories()
                if (res.isSuccessful && res.body() != null) {
                    categories = res.body()!!
                }
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

    fun handleDelete(id: String) {
        scope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                val res = api.deleteCategory(id)
                if (res.isSuccessful) {
                    Toast.makeText(context, "Kategori berhasil dihapus", Toast.LENGTH_SHORT).show()
                    loadData()
                } else {
                    val err = res.errorBody()?.string() ?: res.message()
                    Toast.makeText(context, "Gagal menghapus: $err", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
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
            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFE25822))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(categories) { category ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White,
                            shadowElevation = 2.dp,
                            border = BorderStroke(1.dp, Color(0xFFE9E1D9))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(category.name, fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color(0xFF2B211D))
                                    if (!category.description.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(category.description, fontSize = 12.sp, color = Color(0xFF81766F))
                                    }
                                    if (category.count != null) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Surface(
                                            color = Color(0xFFFFF0E8),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                "${category.count.products} Produk Menu",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFE25822),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = {
                                            editingCategory = category
                                            isDialogVisible = true
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF2B211D), modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(
                                        onClick = { handleDelete(category.id) },
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

        // Prominent Floating Action Button at Bottom
        Button(
            onClick = {
                editingCategory = null
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
                Text("Tambah Kategori Baru", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }

    if (isDialogVisible) {
        var name by remember(editingCategory) { mutableStateOf(editingCategory?.name ?: "") }
        var description by remember(editingCategory) { mutableStateOf(editingCategory?.description ?: "") }

        AlertDialog(
            onDismissRequest = { isDialogVisible = false },
            title = { Text(if (editingCategory != null) "Edit Kategori" else "Tambah Kategori Baru", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFF2B211D)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Kategori") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Deskripsi") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            scope.launch {
                                val api = RetrofitClient.getApiService(context)
                                val req = CreateCategoryRequest(name.trim(), description.ifBlank { null })
                                val res = if (editingCategory != null) {
                                    api.updateCategory(editingCategory!!.id, req)
                                } else {
                                    api.createCategory(req)
                                }
                                if (res.isSuccessful) {
                                    Toast.makeText(context, "Kategori berhasil disimpan", Toast.LENGTH_SHORT).show()
                                    isDialogVisible = false
                                    loadData()
                                } else {
                                    Toast.makeText(context, "Gagal menyimpan kategori", Toast.LENGTH_SHORT).show()
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
