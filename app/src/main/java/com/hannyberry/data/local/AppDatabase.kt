package com.hannyberry.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CategoryEntity::class, TransactionEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "hannyberry.db",
            )
                // Aplikasi masih versi awal dan datanya bisa disinkron ulang dari
                // server, jadi skema lama dibuang alih-alih bikin app gagal buka.
                .fallbackToDestructiveMigration()
                .build()
                .also { instance = it }
        }
    }
}
