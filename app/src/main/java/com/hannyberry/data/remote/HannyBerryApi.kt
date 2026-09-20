package com.hannyberry.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface HannyBerryApi {
    @GET("health")
    suspend fun health(): Response<Map<String, String>>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @GET("categories")
    suspend fun categories(): Response<List<CategoryDto>>

    @GET("transactions")
    suspend fun transactions(): Response<List<TransactionDto>>

    /** Kirim baris offline sekaligus; idempotent karena id dibuat di perangkat. */
    @POST("transactions/sync")
    suspend fun syncTransactions(
        @Body items: List<TransactionUpload>,
    ): Response<List<TransactionDto>>
}
