package com.kamedevin.budget.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kamedevin.budget.core.database.converter.Converters
import com.kamedevin.budget.core.database.dao.AccountDao
import com.kamedevin.budget.core.database.dao.BudgetSettingsDao
import com.kamedevin.budget.core.database.dao.CategoryDao
import com.kamedevin.budget.core.database.dao.TransactionDao
import com.kamedevin.budget.core.database.entity.AccountEntity
import com.kamedevin.budget.core.database.entity.BudgetSettingsEntity
import com.kamedevin.budget.core.database.entity.CategoryEntity
import com.kamedevin.budget.core.database.entity.TransactionEntity

@Database(
    entities = [
        CategoryEntity::class,
        AccountEntity::class,
        TransactionEntity::class,
        BudgetSettingsEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetSettingsDao(): BudgetSettingsDao

    companion object {
        const val DATABASE_NAME = "kamedevin-budget.db"
    }
}
