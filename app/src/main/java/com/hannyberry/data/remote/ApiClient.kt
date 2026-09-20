package com.hannyberry.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Satu instance Retrofit untuk seluruh aplikasi.
 *
 * Interceptor Authorization dipasang terpisah lewat [TokenProvider] supaya
 * token selalu dibaca dari penyimpanan saat request dikirim, bukan saat
 * instance dibuat.
 */
object ApiClient {
    private var cached: HannyBerryApi? = null

    fun api(tokenProvider: () -> String?): HannyBerryApi = cached ?: synchronized(this) {
        cached ?: build(tokenProvider).also { cached = it }
    }

    private fun build(tokenProvider: () -> String?): HannyBerryApi {
        val logging = HttpLoggingInterceptor().apply {
            // Jangan pernah mencatat body pada build rilis: berisi email, password, dan token.
            level = if (com.hannyberry.BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
            else HttpLoggingInterceptor.Level.NONE
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val token = tokenProvider()
                val request = if (token.isNullOrBlank()) chain.request()
                else chain.request().newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(HannyBerryApi::class.java)
    }
}
