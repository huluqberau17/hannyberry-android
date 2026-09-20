package com.hannyberry.data

import com.hannyberry.data.local.AppDatabase
import com.hannyberry.data.local.CategoryEntity
import com.hannyberry.data.local.TransactionEntity
import com.hannyberry.data.remote.ApiClient
import com.hannyberry.data.remote.CategoryDto
import com.hannyberry.data.remote.HannyBerryApi
import com.hannyberry.data.remote.TransactionDto
import com.hannyberry.data.remote.TransactionUpload
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID

/**
 * Penyimpanan transaksi: perangkat dulu, server kemudian.
 *
 * Menambah transaksi selalu menulis ke Room, jadi aplikasi tetap bisa dipakai
 * tanpa internet. Baris ditandai `dirty` sampai berhasil dikirim.
 */
class TransactionRepository(
    private val db: AppDatabase,
    private val tokens: TokenStore,
) {
    private val api: HannyBerryApi by lazy { ApiClient.api { tokens.token() } }

    fun observeAll(): Flow<List<TransactionEntity>> = db.transactionDao().observeAll()

    fun observeCategories(): Flow<List<CategoryEntity>> = db.categoryDao().observeActive()

    suspend fun categoriesForType(type: String): List<CategoryEntity> =
        db.categoryDao().forType(type)

    suspend fun add(
        amount: Long,
        transactionType: String,
        transactionDate: LocalDate = LocalDate.now(),
        categoryId: String? = null,
        categoryName: String? = null,
        notes: String? = null,
    ): TransactionEntity {
        val entity = TransactionEntity(
            id = UUID.randomUUID().toString(),
            amount = amount,
            transactionType = transactionType,
            transactionDate = transactionDate.toString(),
            categoryId = categoryId,
            categoryName = categoryName,
            notes = notes?.takeIf { it.isNotBlank() },
            dirty = true,
        )
        db.transactionDao().upsert(entity)
        return entity
    }

    suspend fun pendingCount(): Int = db.transactionDao().dirty().size

    /** Kirim baris lokal ke server; baris yang berhasil ditandai bersih. */
    suspend fun sync(): SyncOutcome {
        if (tokens.token() == null) return SyncOutcome.noSession()
        val pending = db.transactionDao().dirty()
        if (pending.isEmpty()) return refreshFromServer()

        return try {
            val payload = pending.map { it.toUpload() }
            val response = api.syncTransactions(payload)
            if (!response.isSuccessful) return SyncOutcome.failed("Server menolak sync (HTTP ${response.code()}).")
            db.transactionDao().markClean(pending.map { it.id })
            refreshFromServer()
        } catch (error: Exception) {
            SyncOutcome.failed("Tidak dapat menghubungi server.")
        }
    }

    /** Ambil data server sebagai sumber kebenaran, lalu sisipkan yang lokal. */
    private suspend fun refreshFromServer(): SyncOutcome {
        return try {
            val categoryResponse = api.categories()
            if (categoryResponse.isSuccessful) {
                db.categoryDao().upsertAll(categoryResponse.body().orEmpty().map { it.toEntity() })
            }

            val response = api.transactions()
            if (!response.isSuccessful) return SyncOutcome.failed("Gagal memuat transaksi (HTTP ${response.code()}).")
            val remote = response.body().orEmpty().map { it.toEntity() }
            val keepLocal = db.transactionDao().dirty().map { it.id }.toSet()
            // Jangan timpa baris yang belum terkirim supaya perubahan lokal tidak hilang.
            db.transactionDao().upsertAll(remote.filterNot { it.id in keepLocal })
            SyncOutcome.success(remote.size, pendingCount())
        } catch (error: Exception) {
            SyncOutcome.failed("Tidak dapat memuat transaksi dari server.")
        }
    }

    private fun TransactionEntity.toUpload() = TransactionUpload(
        id = id,
        amount = amount,
        transactionType = transactionType,
        transactionDate = transactionDate,
        notes = notes,
        categoryId = categoryId,
    )

    private fun TransactionDto.toEntity() = TransactionEntity(
        id = id,
        amount = amount,
        transactionType = transactionType,
        transactionDate = transactionDate,
        notes = notes,
        categoryId = categoryId,
        categoryName = categoryName,
        dirty = false,
        deletedAt = null,
    )

    private fun CategoryDto.toEntity() = CategoryEntity(
        id = id,
        name = name,
        transactionType = transactionType,
        active = active,
        dirty = false,
        deletedAt = null,
    )
}

data class SyncOutcome(
    val success: Boolean,
    val message: String,
    val syncedCount: Int = 0,
    val pendingCount: Int = 0,
) {
    companion object {
        fun success(synced: Int, pending: Int) = SyncOutcome(
            success = true,
            message = if (pending == 0) "Semua transaksi tersinkron." else "$synced transaksi dari server, $pending belum terkirim.",
            syncedCount = synced,
            pendingCount = pending,
        )

        fun failed(message: String) = SyncOutcome(success = false, message = message)

        fun noSession() = SyncOutcome(success = false, message = "Silakan login kembali untuk sinkronisasi.")
    }
}
