package com.kuliner.msme.admin.notification

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Menyimpan daftar notifikasi (beserta status sudah dibaca) di perangkat
 * agar badge dan riwayat notifikasi tetap konsisten antar pembukaan layar.
 */
class NotificationStore(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    private val listType = object : TypeToken<List<AppNotification>>() {}.type

    fun load(): List<AppNotification> {
        val raw = prefs.getString(KEY_ITEMS, null) ?: return emptyList()
        return try {
            gson.fromJson<List<AppNotification>>(raw, listType) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun save(items: List<AppNotification>) {
        prefs.edit()
            .putString(KEY_ITEMS, gson.toJson(items, listType))
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "kuliner_app_notifications"
        private const val KEY_ITEMS = "items"
    }
}
