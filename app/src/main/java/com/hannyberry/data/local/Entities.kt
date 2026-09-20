package com.hannyberry.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
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
    val type: String,
    val categoryId: String,
    val categoryName: String? = null,
    val occurredOn: String,
    val note: String? = null,
    val dirty: Boolean = false,
    val deletedAt: String? = null,
    val updatedAt: String = "",
)
