package com.kuliner.msme.admin.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kuliner.msme.admin.data.api.RetrofitClient
import com.kuliner.msme.admin.data.model.CreateTableRequest
import com.kuliner.msme.admin.data.model.Table
import com.kuliner.msme.admin.data.model.formatTableQrUrl
import com.kuliner.msme.admin.ui.components.*
import com.kuliner.msme.admin.ui.theme.*
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
            .background(AromaCream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 76.dp)
        ) {
            if (isLoading) {
                AromaLoadingState(message = "Memuat daftar meja...")
            } else if (tables.isEmpty()) {
                AromaEmptyState(
                    icon = Icons.Default.QrCode,
                    title = "Belum ada meja terdaftar",
                    subtitle = "Tambahkan meja untuk membuat tautan QR pemesanan mandiri pelanggan."
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tables) { table ->
                        AromaCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    modifier = Modifier.size(46.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = AromaTerracottaSoft
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.QrCode,
                                            contentDescription = null,
                                            modifier = Modifier.size(22.dp),
                                            tint = AromaTerracotta
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = table.tableNumber,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = AromaCharcoal,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                AromaTag(
                                    label = if (table.isAvailable) "Tersedia" else "Tidak Aktif",
                                    backgroundColor = if (table.isAvailable) AromaSuccessSoft else AromaCreamTrack,
                                    textColor = if (table.isAvailable) AromaSuccess else AromaTextMuted,
                                    borderColor = if (table.isAvailable) AromaSuccessBorder else AromaBorder
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = { selectedQrTable = table },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(38.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, AromaTerracottaBorder),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = AromaTerracottaSoft,
                                            contentColor = AromaTerracotta
                                        ),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                    ) {
                                        Text("Lihat QR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Surface(
                                        onClick = { handleDelete(table.id) },
                                        modifier = Modifier.size(38.dp),
                                        shape = RoundedCornerShape(10.dp),
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
        }

        // Bottom Primary Button
        Button(
            onClick = { isDialogVisible = true },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AromaTerracotta,
                contentColor = AromaSurface
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Tambah Meja Baru", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }

    if (isDialogVisible) {
        var tableNum by remember { mutableStateOf("Meja ${tables.size + 1}") }

        AlertDialog(
            onDismissRequest = { isDialogVisible = false },
            containerColor = AromaSurface,
            shape = RoundedCornerShape(22.dp),
            title = {
                Text(
                    text = "Tambah Meja Baru",
                    style = MaterialTheme.typography.titleLarge,
                    color = AromaCharcoal
                )
            },
            text = {
                OutlinedTextField(
                    value = tableNum,
                    onValueChange = { tableNum = it },
                    label = { Text("Nomor / Nama Meja") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = aromaTextFieldColors()
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

    if (selectedQrTable != null) {
        val formattedQrUrl = formatTableQrUrl(selectedQrTable!!.qrCode, selectedQrTable!!.tableNumber)

        AlertDialog(
            onDismissRequest = { selectedQrTable = null },
            containerColor = AromaSurface,
            shape = RoundedCornerShape(22.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = AromaTerracottaSoft
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.QrCode,
                                contentDescription = null,
                                tint = AromaTerracotta,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "QR Pemesanan",
                            style = MaterialTheme.typography.labelMedium,
                            color = AromaTextMuted
                        )
                        Text(
                            text = selectedQrTable!!.tableNumber,
                            style = MaterialTheme.typography.titleLarge,
                            color = AromaCharcoal
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Tautan QR Code pemesanan pelanggan untuk meja ini:",
                        style = MaterialTheme.typography.bodySmall,
                        color = AromaTextSecondary
                    )
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = AromaCreamSoft,
                        border = BorderStroke(1.dp, AromaBorder)
                    ) {
                        Text(
                            text = formattedQrUrl,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AromaTerracotta,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedQrTable = null },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AromaTerracotta)
                ) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
