package com.kuliner.msme.admin.ui.screens

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kuliner.msme.admin.R
import com.kuliner.msme.admin.data.model.ReceiptInfo
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ReceiptDialog(
    receipt: ReceiptInfo?,
    onDismiss: () -> Unit
) {
    if (receipt == null) return

    val context = LocalContext.current

    // Official Business Identity requested by user
    val storeName = "Ayam Goreng Lengkuas Aroma"
    val storeAddress = "Ruko Puri Cempaka Blok R1 No. 10, Panancangan, Kec. Cipocok Jaya, Kota Serang, Banten 42124"
    val storePhone = "0821-1215-2744"

    fun formatRp(amount: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        format.maximumFractionDigits = 0
        return format.format(amount)
    }

    fun handlePrint() {
        try {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
            if (printManager == null) {
                Toast.makeText(context, "Layanan cetak tidak tersedia pada perangkat ini", Toast.LENGTH_SHORT).show()
                return
            }

            val htmlContent = StringBuilder().apply {
                append("<html><body style='font-family: monospace; padding: 20px; font-size: 14px;'>")
                append("<h2 style='text-align: center; margin-bottom: 2px;'>$storeName</h2>")
                append("<p style='text-align: center; font-size: 11px; margin-top: 0;'>$storeAddress<br>Telp: $storePhone</p>")
                append("<hr>")
                append("<p><b>No. Pesanan:</b> ${receipt.orderNumber}<br>")
                append("<b>Tanggal:</b> ${receipt.dateTime.take(19).replace("T", " ")}<br>")
                append("<b>Pelanggan:</b> ${receipt.customerName ?: "Pelanggan"}<br>")
                append("<b>Tipe:</b> ${if (receipt.orderType == "DINE_IN") "Dine In (${receipt.tableName ?: "-"})" else "Take Away"}<br>")
                append("<b>Metode / Status:</b> ${receipt.paymentMethod} (${receipt.paymentStatus})</p>")
                append("<hr>")
                append("<table style='width: 100%; border-collapse: collapse;'>")
                receipt.items.forEach { item ->
                    append("<tr><td style='padding: 4px 0;'>${item.quantity}x ${item.productName}</td><td style='text-align: right; padding: 4px 0;'>${formatRp(item.subtotal)}</td></tr>")
                    if (!item.notes.isNullOrBlank()) {
                        append("<tr><td colspan='2' style='font-size: 11px; color: #555; padding-bottom: 4px;'>Note: ${item.notes}</td></tr>")
                    }
                }
                append("</table>")
                append("<hr>")
                append("<h3 style='text-align: right;'>Total: ${formatRp(receipt.total)}</h3>")
                append("<p style='text-align: center; font-size: 11px; margin-top: 20px;'>Terima kasih telah berkunjung ke AROMA!</p>")
                append("</body></html>")
            }.toString()

            val webView = WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {
                        val printAdapter = view.createPrintDocumentAdapter("Struk-${receipt.orderNumber}")
                        printManager.print("Struk-${receipt.orderNumber}", printAdapter, PrintAttributes.Builder().build())
                    }
                }
                loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal mencetak: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun handleSavePdf() {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(400, 700, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas
            val paint = Paint()

            // Draw content
            paint.textSize = 14f
            paint.isFakeBoldText = true
            canvas.drawText(storeName, 20f, 30f, paint)

            paint.textSize = 10f
            paint.isFakeBoldText = false
            paint.color = android.graphics.Color.DKGRAY
            canvas.drawText("Alamat: $storeAddress", 20f, 50f, paint)
            canvas.drawText("Telp: $storePhone", 20f, 65f, paint)

            // Divider line
            paint.color = android.graphics.Color.BLACK
            canvas.drawLine(20f, 75f, 380f, 75f, paint)

            var y = 95f
            paint.textSize = 11f
            canvas.drawText("No. Pesanan: ${receipt.orderNumber}", 20f, y, paint); y += 18f
            canvas.drawText("Tanggal: ${receipt.dateTime.take(19).replace("T", " ")}", 20f, y, paint); y += 18f
            canvas.drawText("Pelanggan: ${receipt.customerName ?: "Pelanggan"}", 20f, y, paint); y += 18f
            canvas.drawText("Tipe: ${if (receipt.orderType == "DINE_IN") "Dine In (${receipt.tableName ?: "-"})" else "Take Away"}", 20f, y, paint); y += 18f
            canvas.drawText("Pembayaran: ${receipt.paymentMethod} (${receipt.paymentStatus})", 20f, y, paint); y += 10f

            canvas.drawLine(20f, y, 380f, y, paint); y += 20f

            paint.isFakeBoldText = true
            canvas.drawText("Rincian Item:", 20f, y, paint); y += 18f
            paint.isFakeBoldText = false

            receipt.items.forEach { item ->
                canvas.drawText("${item.quantity}x ${item.productName}", 20f, y, paint)
                canvas.drawText(formatRp(item.subtotal), 280f, y, paint)
                y += 18f
                if (!item.notes.isNullOrBlank()) {
                    paint.color = android.graphics.Color.GRAY
                    canvas.drawText("  Note: ${item.notes}", 20f, y, paint)
                    paint.color = android.graphics.Color.BLACK
                    y += 16f
                }
            }

            canvas.drawLine(20f, y, 380f, y, paint); y += 25f

            paint.textSize = 14f
            paint.isFakeBoldText = true
            paint.color = android.graphics.Color.rgb(201, 79, 29)
            canvas.drawText("TOTAL: ${formatRp(receipt.total)}", 20f, y, paint)

            pdfDocument.finishPage(page)

            val fileName = "Struk-AROMA-${receipt.orderNumber}.pdf"
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) downloadsDir.mkdirs()
            val file = File(downloadsDir, fileName)

            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()

            Toast.makeText(context, "PDF berhasil disimpan di Downloads/$fileName", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal menyimpan PDF: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Struk Pembayaran Digital",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Official Logo Preview
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_aroma),
                        contentDescription = "Logo AROMA",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Official Store Info
                Text(
                    text = storeName,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = storeAddress,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Text(
                    text = "Telp: $storePhone",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant)

                // Meta Info
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("No. Pesanan:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Text(receipt.orderNumber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tanggal:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Text(receipt.dateTime.take(19).replace("T", " "), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pelanggan:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Text(receipt.customerName ?: "Pelanggan", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tipe Pesanan:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Text(
                            if (receipt.orderType == "DINE_IN") "Dine In (${receipt.tableName ?: "-"})" else "Take Away",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Metode / Status:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Text("${receipt.paymentMethod} (${receipt.paymentStatus})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant)

                // Items list
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    receipt.items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${item.quantity}x ${item.productName}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (!item.notes.isNullOrBlank()) {
                                    Text(
                                        text = "Note: ${item.notes}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                            Text(
                                text = formatRp(item.subtotal),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant)

                // Total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("TOTAL BAYAR", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                    Text(
                        formatRp(receipt.total),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Perfectly proportioned and centered buttons for Cetak and Simpan PDF
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { handlePrint() },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(Icons.Default.Print, contentDescription = "Cetak", modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cetak", fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                        }
                    }

                    Button(
                        onClick = { handleSavePdf() },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(Icons.Default.Download, contentDescription = "Simpan PDF", modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Simpan PDF", fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}
