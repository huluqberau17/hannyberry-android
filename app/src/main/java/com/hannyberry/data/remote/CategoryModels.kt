package com.hannyberry.data.remote

import com.squareup.moshi.Json

data class CategoryDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "transaction_type") val transactionType: String,
    @Json(name = "active") val active: Boolean = true,
)
