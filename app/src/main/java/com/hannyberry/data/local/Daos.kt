package com.hannyberry.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE deletedAt IS NULL AND active = 1 ORDER BY transactionType, name")
    fun observeActive(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE dirty = 1")
    suspend fun dirty(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CategoryEntity>): Unit

    @Query("UPDATE categories SET dirty = 0 WHERE id IN (:ids)")
    suspend fun markClean(ids: List<String>): Unit
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE deletedAt IS NULL ORDER BY occurredOn DESC, updatedAt DESC")
    fun observeAll(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE dirty = 1")
    suspend fun dirty(): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun byId(id: String): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: TransactionEntity): Unit

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<TransactionEntity>): Unit

    @Query("UPDATE transactions SET dirty = 0 WHERE id IN (:ids)")
    suspend fun markClean(ids: List<String>): Unit

    @Query("UPDATE transactions SET deletedAt = :deletedAt, dirty = 1 WHERE id = :id")
    suspend fun softDelete(id: String, deletedAt: String): Unit
}
