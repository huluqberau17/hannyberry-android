package com.hannyberry.data

import com.hannyberry.data.remote.ApiClient
import com.hannyberry.data.remote.ApiError
import com.hannyberry.data.remote.HannyBerryApi
import com.hannyberry.data.remote.LoginRequest
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

sealed interface LoginResult {
    data object Success : LoginResult
    data class Failure(val message: String) : LoginResult
}

class AuthRepository(private val tokens: TokenStore) {
    private val api: HannyBerryApi by lazy { ApiClient.api { tokens.token() } }
    private val errorAdapter = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
        .adapter(ApiError::class.java)

    fun hasSession(): Boolean = tokens.token() != null

    fun logout() = tokens.clear()

    suspend fun login(email: String, password: String): LoginResult {
        if (email.isBlank() || password.isBlank()) {
            return LoginResult.Failure("Email dan password wajib diisi.")
        }
        return try {
            val response = api.login(LoginRequest(email.trim(), password))
            val body = response.body()
            when {
                response.isSuccessful && body != null -> {
                    tokens.save(body.accessToken)
                    LoginResult.Success
                }
                response.code() == 401 -> LoginResult.Failure("Email atau password salah.")
                response.code() == 429 -> LoginResult.Failure("Terlalu banyak percobaan. Coba lagi nanti.")
                else -> LoginResult.Failure(describeError(response.errorBody()?.string()) ?: "Login gagal (HTTP ${response.code()}).")
            }
        } catch (error: Exception) {
            LoginResult.Failure("Tidak dapat menghubungi server. Periksa koneksi internet.")
        }
    }

    private fun describeError(raw: String?): String? = raw
        ?.takeIf { it.isNotBlank() }
        ?.let { runCatching { errorAdapter.fromJson(it)?.detail }.getOrNull() }
}
