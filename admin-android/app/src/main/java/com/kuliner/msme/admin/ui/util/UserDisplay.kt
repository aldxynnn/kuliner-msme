package com.kuliner.msme.admin.ui.util

/**
 * Nama akun yang ramah untuk ditampilkan di UI.
 *
 * Data lama menyimpan nama akun sebagai "Owner / Admin AROMA". Sebutan
 * Owner/Admin tidak perlu muncul di aplikasi kasir, jadi nama seperti itu
 * diganti dengan label netral "Kasir AROMA". Nama orang sungguhan tetap
 * ditampilkan apa adanya.
 */
fun friendlyUserName(
    rawName: String?,
    fallback: String = "Kasir AROMA"
): String {
    val value = rawName?.trim().orEmpty()
    if (value.isEmpty()) return fallback

    val lower = value.lowercase()
    val containsRoleTitle = lower.contains("owner") || lower.contains("admin")
    return if (containsRoleTitle) fallback else value
}
