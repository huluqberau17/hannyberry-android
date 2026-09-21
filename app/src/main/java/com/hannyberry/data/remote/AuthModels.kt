package com.hannyberry.data.remote

import com.squareup.moshi.Json

data class LoginRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
)

data class LoginResponse(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "token_type") val tokenType: String? = null,
)

/** Payload pendaftaran; server menentukan role, bukan aplikasi. */
data class SignupRequest(
    @Json(name = "name") val name: String,
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
)

data class UserDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "email") val email: String,
    @Json(name = "role") val role: String,
    @Json(name = "avatar_filename") val avatarFilename: String? = null,
)

data class ProfileUpdate(
    @Json(name = "name") val name: String,
)

data class ApiError(
    @Json(name = "detail") val detail: String? = null,
)
