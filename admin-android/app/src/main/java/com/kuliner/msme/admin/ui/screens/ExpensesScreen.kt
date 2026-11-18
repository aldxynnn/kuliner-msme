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
import com.kuliner.msme.admin.data.model.CreateExpenseRequest
import com.kuliner.msme.admin.data.model.Expense
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

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

    fun formatRp(amount: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        format.maximumFractionDigits = 0
        return format.format(amount)
    }

    val totalExpense = expenses.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF8F5))
    ) {
        // Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Biaya Operasional", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF2B211D))
                Text("Catat & kelola pengeluaran harian resto", fontSize = 11.sp, color = Color(0xFF81766F))
            }

            Surface(
                onClick = { isDialogVisible = true },
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = Color(0xFFE25822),
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }

        // Summary Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFFFEE2E2),
            shadowElevation = 2.dp,
            border = BorderStroke(1.dp, Color(0xFFFCA5A5))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    "TOTAL PENGELUARAN DITAMBAHKAN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF991B1B),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    formatRp(totalExpense),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF991B1B)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFE25822))
            }
        } else if (expenses.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Belum ada pengeluaran dicatat", color = Color(0xFF81766F), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(expenses) { exp ->
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
                                Text(exp.description, fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color(0xFF2B211D))
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Kategori: ${exp.category} • ${exp.date.take(10)}", fontSize = 11.sp, color = Color(0xFF81766F))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(formatRp(exp.amount), fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color(0xFFC94136))
                            }

                            IconButton(
                                onClick = { handleDelete(exp.id) },
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

    if (isDialogVisible) {
        var description by remember { mutableStateOf("") }
        var amountStr by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Bahan Baku") }

        AlertDialog(
            onDismissRequest = { isDialogVisible = false },
            title = { Text("Catat Pengeluaran Baru", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFF2B211D)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Keterangan Pengeluaran") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = { Text("Nominal (Rp)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Kategori (e.g. Bahan Baku / Operasional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
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
                                val res = api.createExpense(CreateExpenseRequest(description.trim(), amt, category.trim()))
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
