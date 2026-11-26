package com.kuliner.msme.admin.ui.screens

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kuliner.msme.admin.R
import com.kuliner.msme.admin.data.model.ReceiptInfo
import com.kuliner.msme.admin.ui.components.AromaDashedDivider
import com.kuliner.msme.admin.ui.components.AromaStatusBadge
import com.kuliner.msme.admin.ui.components.formatRupiah
import com.kuliner.msme.admin.ui.components.paymentStatusVisual
import com.kuliner.msme.admin.ui.theme.*
import java.io.File
import java.io.FileOutputStream

@Composable
fun ReceiptDialog(
    receipt: ReceiptInfo?,
    onDismiss: () -> Unit
) {
    if (receipt == null) return

    val context = LocalContext.current

    // Official Business Identity
    val storeName = "Ayam Goreng Lengkuas Aroma"
    val storeAddress =
        "Ruko Puri Cempaka Blok R1 No. 10, Panancangan, Kec. Cipocok Jaya, Kota Serang, Banten 42124"
    val storePhone = "0821-1215-2744"

    fun handlePrint() {
        try {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
            if (printManager == null) {
                Toast.makeText(
                    context,
                    "Layanan cetak tidak tersedia pada perangkat ini",
                    Toast.LENGTH_SHORT
                ).show()
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
                    append("<tr><td style='padding: 4px 0;'>${item.quantity}x ${item.productName}</td><td style='text-align: right; padding: 4px 0;'>${formatRupiah(item.subtotal)}</td></tr>")
                    if (!item.notes.isNullOrBlank()) {
                        append("<tr><td colspan='2' style='font-size: 11px; color: #555; padding-bottom: 4px;'>Note: ${item.notes}</td></tr>")
                    }
                }
                append("</table>")
                append("<hr>")
                append("<h3 style='text-align: right;'>Total: ${formatRupiah(receipt.total)}</h3>")
                append("<p style='text-align: center; font-size: 11px; margin-top: 20px;'>Terima kasih telah berkunjung ke AROMA!</p>")
                append("</body></html>")
            }.toString()

            val webView = WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {
                        val printAdapter = view.createPrintDocumentAdapter("Struk-${receipt.orderNumber}")
                        printManager.print(
                            "Struk-${receipt.orderNumber}",
                            printAdapter,
                            PrintAttributes.Builder().build()
                        )
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

            paint.textSize = 14f
            paint.isFakeBoldText = true
            canvas.drawText(storeName, 20f, 30f, paint)

            paint.textSize = 10f
            paint.isFakeBoldText = false
            paint.color = android.graphics.Color.DKGRAY
            canvas.drawText("Alamat: $storeAddress", 20f, 50f, paint)
            canvas.drawText("Telp: $storePhone", 20f, 65f, paint)

            paint.color = android.graphics.Color.BLACK
            canvas.drawLine(20f, 75f, 380f, 75f, paint)

            var y = 95f
            paint.textSize = 11f
            canvas.drawText("No. Pesanan: ${receipt.orderNumber}", 20f, y, paint); y += 18f
            canvas.drawText("Tanggal: ${receipt.dateTime.take(19).replace("T", " ")}", 20f, y, paint); y += 18f
            canvas.drawText("Pelanggan: ${receipt.customerName ?: "Pelanggan"}", 20f, y, paint); y += 18f
            canvas.drawText(
                "Tipe: ${if (receipt.orderType == "DINE_IN") "Dine In (${receipt.tableName ?: "-"})" else "Take Away"}",
                20f,
                y,
                paint
            ); y += 18f
            canvas.drawText(
                "Pembayaran: ${receipt.paymentMethod} (${receipt.paymentStatus})",
                20f,
                y,
                paint
            ); y += 10f

            canvas.drawLine(20f, y, 380f, y, paint); y += 20f

            paint.isFakeBoldText = true
            canvas.drawText("Rincian Item:", 20f, y, paint); y += 18f
            paint.isFakeBoldText = false

            receipt.items.forEach { item ->
                canvas.drawText("${item.quantity}x ${item.productName}", 20f, y, paint)
                canvas.drawText(formatRupiah(item.subtotal), 280f, y, paint)
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
            paint.color = android.graphics.Color.rgb(200, 67, 25)
            canvas.drawText("TOTAL: ${formatRupiah(receipt.total)}", 20f, y, paint)

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
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = AromaSurface,
            border = BorderStroke(1.dp, AromaBorder)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STRUK DIGITAL",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        color = AromaCharcoal
                    )
                    Surface(
                        onClick = onDismiss,
                        modifier = Modifier.size(34.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = AromaCreamSoft,
                        border = BorderStroke(1.dp, AromaBorder)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Tutup",
                                tint = AromaCharcoal,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Store Thermal Header
                Surface(
                    modifier = Modifier.size(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = AromaCreamSoft,
                    border = BorderStroke(1.dp, AromaBorder)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_aroma),
                        contentDescription = "Logo AROMA",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = storeName,
                    style = AromaBrandSerifStyle,
                    fontSize = 16.sp,
                    color = AromaCharcoal,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = storeAddress,
                    fontSize = 11.sp,
                    color = AromaTextMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp
                )
                Text(
                    text = "Telp: $storePhone",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = AromaTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))
                AromaDashedDivider()
                Spacer(modifier = Modifier.height(12.dp))

                // Order Metadata
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    ReceiptMetaRow("No. Pesanan", receipt.orderNumber, boldValue = true, monoValue = true)
                    ReceiptMetaRow("Tanggal", receipt.dateTime.take(19).replace("T", " "), monoValue = true)
                    ReceiptMetaRow("Pelanggan", receipt.customerName ?: "Pelanggan")
                    ReceiptMetaRow(
                        "Tipe Pesanan",
                        if (receipt.orderType == "DINE_IN") {
                            "Dine In (${receipt.tableName ?: "-"})"
                        } else {
                            "Take Away"
                        }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Pembayaran", fontSize = 11.sp, color = AromaTextMuted)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = receipt.paymentMethod,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AromaCharcoal
                            )
                            AromaStatusBadge(visual = paymentStatusVisual(receipt.paymentStatus))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                AromaDashedDivider()
                Spacer(modifier = Modifier.height(12.dp))

                // Purchased Items
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    receipt.items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${item.quantity}× ${item.productName}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AromaCharcoal
                                )
                                if (!item.notes.isNullOrBlank()) {
                                    Text(
                                        text = "Catatan: ${item.notes}",
                                        fontSize = 10.sp,
                                        color = AromaTextMuted
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = formatRupiah(item.subtotal),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AromaCharcoal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Total Summary Band
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = AromaReceiptBg,
                    border = BorderStroke(1.dp, AromaBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL BAYAR",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = AromaTextSecondary
                        )
                        Text(
                            text = formatRupiah(receipt.total),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = AromaTerracotta
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Print (#171311) & Save PDF (#C84319) Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { handlePrint() },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AromaCharcoal,
                            contentColor = AromaSurface
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = "Cetak", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cetak", fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                    }

                    Button(
                        onClick = { handleSavePdf() },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AromaTerracotta,
                            contentColor = AromaSurface
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = "Simpan PDF",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simpan PDF", fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptMetaRow(
    label: String,
    value: String,
    boldValue: Boolean = false,
    monoValue: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = AromaTextMuted)
        Text(
            text = value,
            fontFamily = if (monoValue) FontFamily.Monospace else FontFamily.SansSerif,
            fontSize = 11.sp,
            fontWeight = if (boldValue) FontWeight.Bold else FontWeight.Medium,
            color = AromaCharcoal
        )
    }
}
