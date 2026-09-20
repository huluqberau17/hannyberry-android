package com.hannyberry.data.remote

import com.squareup.moshi.Json

/** Bentuk data transaksi di API HannyBerry. */
data class TransactionDto(
    @Json(name = "id") val id: String,
    @Json(name = "amount") val amount: Long,
    @Json(name = "transaction_type") val transactionType: String,
    @Json(name = "transaction_date") val transactionDate: String,
    @Json(name = "notes") val notes: String? = null,
    @Json(name = "category_id") val categoryId: String? = null,
    @Json(name = "category_name") val categoryName: String? = null,
)

/** Payload kirim dari aplikasi; id diisi UUID buatan perangkat. */
data class TransactionUpload(
    @Json(name = "id") val id: String,
    @Json(name = "amount") val amount: Long,
    @Json(name = "transaction_type") val transactionType: String,
    @Json(name = "transaction_date") val transactionDate: String,
    @Json(name = "notes") val notes: String? = null,
    @Json(name = "category_id") val categoryId: String? = null,
)

data class SyncResult(
    @Json(name = "synced") val synced: Int = 0,
    @Json(name = "failed") val failed: Int = 0,
)
