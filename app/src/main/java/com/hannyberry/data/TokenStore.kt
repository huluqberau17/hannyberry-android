package com.hannyberry.data

import android.content.Context

/**
 * Penyimpanan token sederhana milik aplikasi.
 *
 * Token tidak dicatat ke log dan tidak pernah ditulis ke BuildConfig atau kode.
 */
class TokenStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("hannyberry-auth", Context.MODE_PRIVATE)

    fun token(): String? = prefs.getString(KEY_TOKEN, null)?.takeIf { it.isNotBlank() }

    fun save(token: String) = prefs.edit().putString(KEY_TOKEN, token).apply()

    fun clear() = prefs.edit().remove(KEY_TOKEN).apply()

    private companion object {
        const val KEY_TOKEN = "access_token"
    }
}
