package com.hannyberry.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Salinan lokal dari data server.
 *
 * `dirty = true` berarti baris ini dibuat/diubah di perangkat dan belum
 * terkirim ke server. `deletedAt` adalah penghapusan lunak supaya riwayat
 * tidak hilang dan penghapusan bisa ikut tersinkron.
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val transactionType: String,
    val active: Boolean = true,
    val dirty: Boolean = false,
    val deletedAt: String? = null,
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val amount: Long,
    val transactionType: String,
    val transactionDate: String,
    val categoryId: String? = null,
    val categoryName: String? = null,
    val notes: String? = null,
    val dirty: Boolean = true,
    val deletedAt: String? = null,
)
