package com.kuliner.msme.admin.notification

import android.content.Context

/**
 * Menyimpan id pesanan yang sudah dikenal supaya polling pertama
 * tidak menembakkan notifikasi untuk seluruh riwayat.
 * Prefs ini sengaja terpisah dari token agar logout tidak menghapusnya.
 */
class SeenOrderStore(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isSeeded(): Boolean = prefs.getBoolean(KEY_SEEDED, false)

    fun knownIds(): Set<String> = prefs.getStringSet(KEY_IDS, emptySet()) ?: emptySet()

    fun seed(ids: Set<String>) {
        prefs.edit()
            .putStringSet(KEY_IDS, ids.takeLast(MAX_IDS).toSet())
            .putBoolean(KEY_SEEDED, true)
            .apply()
    }

    fun remember(ids: Set<String>) {
        val merged = (knownIds() + ids).toList().takeLast(MAX_IDS).toSet()
        prefs.edit().putStringSet(KEY_IDS, merged).apply()
    }

    private fun Set<String>.takeLast(limit: Int): List<String> {
        val list = toList()
        return if (list.size <= limit) list else list.takeLast(limit)
    }

    companion object {
        private const val PREFS_NAME = "kuliner_order_notifications"
        private const val KEY_IDS = "known_order_ids"
        private const val KEY_SEEDED = "seeded"
        private const val MAX_IDS = 400
    }
}
