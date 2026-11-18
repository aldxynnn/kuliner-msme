package com.kuliner.msme.admin.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.QrCode
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
import com.kuliner.msme.admin.data.model.CreateTableRequest
import com.kuliner.msme.admin.data.model.Table
import com.kuliner.msme.admin.data.model.formatTableQrUrl
import kotlinx.coroutines.launch

@Composable
fun TablesScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var tables by remember { mutableStateOf<List<Table>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isDialogVisible by remember { mutableStateOf(false) }
    var selectedQrTable by remember { mutableStateOf<Table?>(null) }

    fun loadData() {
        isLoading = true
        scope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                val res = api.getTables()
                if (res.isSuccessful && res.body() != null) {
                    tables = res.body()!!
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
                val res = api.deleteTable(id)
                if (res.isSuccessful) {
                    Toast.makeText(context, "Meja berhasil dihapus", Toast.LENGTH_SHORT).show()
                    loadData()
                } else {
                    Toast.makeText(context, "Gagal menghapus meja", Toast.LENGTH_SHORT).show()
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
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(tables) { table ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White,
                            shadowElevation = 2.dp,
                            border = BorderStroke(1.dp, Color(0xFFE9E1D9))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    modifier = Modifier.size(48.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFFFFF0E8)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.QrCode,
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp),
                                            tint = Color(0xFFE25822)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(table.tableNumber, fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color(0xFF2B211D))

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = { selectedQrTable = table },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE25822)),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                    ) {
                                        Text("Lihat QR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = { handleDelete(table.id) },
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
            onClick = { isDialogVisible = true },
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
                Text("Tambah Meja Baru", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }

    if (isDialogVisible) {
        var tableNum by remember { mutableStateOf("Meja ${tables.size + 1}") }

        AlertDialog(
            onDismissRequest = { isDialogVisible = false },
            title = { Text("Tambah Meja Baru", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFF2B211D)) },
            text = {
                OutlinedTextField(
                    value = tableNum,
                    onValueChange = { tableNum = it },
                    label = { Text("Nomor / Nama Meja") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tableNum.isNotBlank()) {
                            scope.launch {
                                val api = RetrofitClient.getApiService(context)
                                val res = api.createTable(CreateTableRequest(tableNum.trim()))
                                if (res.isSuccessful) {
                                    Toast.makeText(context, "Meja berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                                    isDialogVisible = false
                                    loadData()
                                } else {
                                    Toast.makeText(context, "Gagal menambah meja", Toast.LENGTH_SHORT).show()
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

    if (selectedQrTable != null) {
        val formattedQrUrl = formatTableQrUrl(selectedQrTable!!.qrCode, selectedQrTable!!.tableNumber)

        AlertDialog(
            onDismissRequest = { selectedQrTable = null },
            title = { Text("QR Code - ${selectedQrTable!!.tableNumber}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFF2B211D)) },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("URL QR Code Pemesanan Meja (Vercel):", fontSize = 11.sp, color = Color(0xFF81766F))
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFF0E8),
                        border = BorderStroke(1.dp, Color(0xFFE25822).copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = formattedQrUrl,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE25822),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedQrTable = null },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE25822))
                ) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
