package com.hannyberry.data.remote

import com.hannyberry.BuildConfig

/**
 * Satu titik konfigurasi untuk alamat API.
 *
 * Base URL selalu berakhiran garis miring supaya Retrofit menggabungkan path
 * relatif dengan benar.
 */
object ApiConfig {
    val BASE_URL: String = BuildConfig.API_BASE_URL.trimEnd('/') + "/"
}
