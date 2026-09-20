package com.hannyberry.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface HannyBerryApi {
    @GET("health")
    suspend fun health(): Response<Map<String, String>>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>
}
