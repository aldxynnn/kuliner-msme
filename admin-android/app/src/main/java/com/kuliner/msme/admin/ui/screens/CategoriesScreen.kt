package com.kuliner.msme.admin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                api.deleteCategory(id)
                loadData()
            } catch (e: Exception) {
                // Ignore
            }
        }
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
                Text("Kelola Kategori Menu", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Text("Kategori hidangan cafe/resto", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }

            FloatingActionButton(
                onClick = {
                    editingCategory = null
                    isDialogVisible = true
                },
                modifier = Modifier.size(44.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah")
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categories) { category ->
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
                                Text(category.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                if (!category.description.isNullOrBlank()) {
                                    Text(category.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                }
                            }

                            Row {
                                IconButton(onClick = {
                                    editingCategory = category
                                    isDialogVisible = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                                }
                                IconButton(onClick = { handleDelete(category.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (isDialogVisible) {
        var name by remember { mutableStateOf(editingCategory?.name ?: "") }
        var description by remember { mutableStateOf(editingCategory?.description ?: "") }

        AlertDialog(
            onDismissRequest = { isDialogVisible = false },
            title = { Text(if (editingCategory != null) "Edit Kategori" else "Tambah Kategori") },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Kategori") },
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
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            scope.launch {
                                val api = RetrofitClient.getApiService(context)
                                val req = CreateCategoryRequest(name.trim(), description.ifBlank { null })
                                if (editingCategory != null) {
                                    api.updateCategory(editingCategory!!.id, req)
                                } else {
                                    api.createCategory(req)
                                }
                                isDialogVisible = false
                                loadData()
                            }
                        }
                    }
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
