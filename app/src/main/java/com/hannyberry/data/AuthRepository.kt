package com.hannyberry.data

import com.hannyberry.data.remote.ApiClient
import com.hannyberry.data.remote.ApiError
import com.hannyberry.data.remote.HannyBerryApi
import com.hannyberry.data.remote.LoginRequest
import com.hannyberry.data.remote.ProfileUpdate
import com.hannyberry.data.remote.SignupRequest
import com.hannyberry.data.remote.UserDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

sealed interface LoginResult {
    data object Success : LoginResult
    data class Failure(val message: String) : LoginResult
}

sealed interface SignupResult {
    data object Success : SignupResult
    data class Failure(val message: String) : SignupResult
}

sealed interface ProfileResult {
    data class Success(val profile: UserDto) : ProfileResult
    data class Failure(val message: String) : ProfileResult
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
            val response = api.login(LoginRequest(email.trim().lowercase(), password))
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

    suspend fun signup(name: String, email: String, password: String): SignupResult {
        if (name.isBlank()) return SignupResult.Failure("Nama wajib diisi.")
        if (email.isBlank()) return SignupResult.Failure("Email wajib diisi.")
        if (password.length < 10) return SignupResult.Failure("Password minimal 10 karakter.")
        if (!password.any(Char::isLetter) || !password.any(Char::isDigit)) {
            return SignupResult.Failure("Password harus berisi huruf dan angka.")
        }
        return try {
            val response = api.signup(
                SignupRequest(name.trim(), email.trim().lowercase(), password)
            )
            when {
                response.isSuccessful -> SignupResult.Success
                response.code() == 409 -> SignupResult.Failure("Email sudah terdaftar. Silakan login.")
                response.code() == 422 -> SignupResult.Failure("Data belum lengkap atau password terlalu lemah.")
                else -> SignupResult.Failure(describeError(response.errorBody()?.string()) ?: "Pendaftaran gagal (HTTP ${response.code()}).")
            }
        } catch (error: Exception) {
            SignupResult.Failure("Tidak dapat menghubungi server. Periksa koneksi internet.")
        }
    }

    suspend fun profile(): ProfileResult {
        if (tokens.token() == null) return ProfileResult.Failure("Silakan login kembali.")
        return try {
            val response = api.me()
            val body = response.body()
            when {
                response.isSuccessful && body != null -> ProfileResult.Success(body)
                response.code() == 401 -> ProfileResult.Failure("Sesi berakhir. Silakan login lagi.")
                else -> ProfileResult.Failure("Profil tidak dapat dimuat (HTTP ${response.code()}).")
            }
        } catch (error: Exception) {
            ProfileResult.Failure("Tidak dapat menghubungi server.")
        }
    }

    suspend fun rename(name: String): ProfileResult {
        if (name.isBlank()) return ProfileResult.Failure("Nama wajib diisi.")
        if (tokens.token() == null) return ProfileResult.Failure("Silakan login kembali.")
        return try {
            val response = api.updateProfile(ProfileUpdate(name.trim()))
            val body = response.body()
            when {
                response.isSuccessful && body != null -> ProfileResult.Success(body)
                response.code() == 401 -> ProfileResult.Failure("Sesi berakhir. Silakan login lagi.")
                else -> ProfileResult.Failure("Nama gagal disimpan (HTTP ${response.code()}).")
            }
        } catch (error: Exception) {
            ProfileResult.Failure("Tidak dapat menghubungi server.")
        }
    }

    private fun describeError(raw: String?): String? = raw
        ?.takeIf { it.isNotBlank() }
        ?.let { runCatching { errorAdapter.fromJson(it)?.detail }.getOrNull() }
}
