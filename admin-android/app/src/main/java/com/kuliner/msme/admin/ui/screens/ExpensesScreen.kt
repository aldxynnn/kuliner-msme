package com.kuliner.msme.admin.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kuliner.msme.admin.data.api.RetrofitClient
import com.kuliner.msme.admin.data.model.CreateExpenseRequest
import com.kuliner.msme.admin.data.model.Expense
import com.kuliner.msme.admin.ui.components.*
import com.kuliner.msme.admin.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ExpensesScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var expenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isDialogVisible by remember { mutableStateOf(false) }

    fun loadData() {
        isLoading = true
        scope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                val res = api.getExpenses()
                if (res.isSuccessful && res.body() != null) {
                    expenses = res.body()!!
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
                val res = api.deleteExpense(id)
                if (res.isSuccessful) {
                    Toast.makeText(context, "Pengeluaran berhasil dihapus", Toast.LENGTH_SHORT).show()
                    loadData()
                } else {
                    Toast.makeText(context, "Gagal menghapus pengeluaran", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val totalExpense = expenses.sumOf { it.amount }

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
            Spacer(modifier = Modifier.height(14.dp))

            // Executive Dark Outflow Summary Sheet
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(14.dp),
                color = AromaCharcoal
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "TOTAL PENGELUARAN TERCATAT",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AromaSurface.copy(alpha = 0.68f),
                            letterSpacing = 0.9.sp
                        )
                        Text(
                            text = formatRupiah(totalExpense),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = AromaTerracottaLight
                        )
                        Text(
                            text = "${expenses.size} catatan pengeluaran operasional",
                            fontSize = 12.sp,
                            color = AromaSurface.copy(alpha = 0.72f)
                        )
                    }

                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = AromaInkSoft,
                        border = BorderStroke(1.dp, AromaInkDivider)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.MoneyOff,
                                contentDescription = null,
                                tint = AromaTerracottaLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (isLoading) {
                AromaLoadingState(message = "Memuat catatan pengeluaran...")
            } else if (expenses.isEmpty()) {
                AromaEmptyState(
                    icon = Icons.Default.MoneyOff,
                    title = "Belum ada pengeluaran dicatat",
                    subtitle = "Catat pembelian bahan baku dan biaya operasional harian agar laporan surplus akurat."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(expenses) { exp ->
                        AromaCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 12.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .fillMaxHeight()
                                        .background(AromaError)
                                )

                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            AromaTag(
                                                label = exp.category,
                                                backgroundColor = AromaCreamSoft,
                                                textColor = AromaCharcoal,
                                                borderColor = AromaBorder
                                            )
                                            Text(
                                                text = exp.date.take(10),
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 11.sp,
                                                color = AromaTextMuted
                                            )
                                        }

                                        Text(
                                            text = exp.description,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = AromaCharcoal
                                        )

                                        Text(
                                            text = formatRupiah(exp.amount),
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = AromaError
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Surface(
                                        onClick = { handleDelete(exp.id) },
                                        modifier = Modifier.size(36.dp),
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
                    item { Spacer(modifier = Modifier.height(10.dp)) }
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
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AromaTerracotta,
                contentColor = AromaSurface
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Catat Pengeluaran Baru", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }

    if (isDialogVisible) {
        var description by remember { mutableStateOf("") }
        var amountStr by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Bahan Baku") }

        AlertDialog(
            onDismissRequest = { isDialogVisible = false },
            containerColor = AromaSurface,
            shape = RoundedCornerShape(18.dp),
            title = {
                Text(
                    text = "Catat Pengeluaran Baru",
                    style = MaterialTheme.typography.titleLarge,
                    color = AromaCharcoal
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Keterangan Pengeluaran") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = aromaTextFieldColors()
                    )
                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = { Text("Nominal (Rp)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = aromaTextFieldColors()
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Kategori (mis. Bahan Baku / Operasional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = aromaTextFieldColors()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = amountStr.toIntOrNull() ?: 0
                        if (description.isNotBlank() && amt > 0) {
                            scope.launch {
                                val api = RetrofitClient.getApiService(context)
                                val res = api.createExpense(
                                    CreateExpenseRequest(description.trim(), amt, category.trim())
                                )
                                if (res.isSuccessful) {
                                    Toast.makeText(context, "Pengeluaran dicatat", Toast.LENGTH_SHORT).show()
                                    isDialogVisible = false
                                    loadData()
                                } else {
                                    Toast.makeText(context, "Gagal mencatat pengeluaran", Toast.LENGTH_SHORT).show()
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
