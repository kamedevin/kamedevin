package com.kamedevin.budget.core.database.seed

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kamedevin.budget.core.database.AppDatabase
import com.kamedevin.budget.core.database.entity.AccountEntity
import com.kamedevin.budget.core.database.entity.BudgetSettingsEntity
import com.kamedevin.budget.core.database.entity.CategoryEntity
import com.kamedevin.budget.core.model.AccountType
import com.kamedevin.budget.core.model.Bucket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Populates starter categories/account/budget-settings the first time the database file is
 * created. [databaseProvider] is a lambda rather than a direct [AppDatabase] reference because
 * Room only invokes [onCreate] lazily on first access, by which point the caller's
 * `Room.databaseBuilder(...).build()` has already returned and assigned its result.
 */
class DefaultDataSeedCallback(
    private val scope: CoroutineScope,
    private val databaseProvider: () -> AppDatabase,
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        scope.launch {
            seed(databaseProvider())
        }
    }

    private suspend fun seed(database: AppDatabase) {
        database.categoryDao().insertAll(DEFAULT_CATEGORIES)
        database.accountDao().insert(DEFAULT_ACCOUNT)
        database.budgetSettingsDao().upsert(BudgetSettingsEntity())
    }

    companion object {
        private val DEFAULT_CATEGORIES = listOf(
            CategoryEntity(name = "Income", bucket = null, colorHex = "#4CAF50", isDefault = true, sortOrder = 0),
            CategoryEntity(name = "Groceries", bucket = Bucket.NEEDS, colorHex = "#5C6BC0", isDefault = true, sortOrder = 1),
            CategoryEntity(name = "Rent/Mortgage", bucket = Bucket.NEEDS, colorHex = "#5C6BC0", isDefault = true, sortOrder = 2),
            CategoryEntity(name = "Utilities", bucket = Bucket.NEEDS, colorHex = "#5C6BC0", isDefault = true, sortOrder = 3),
            CategoryEntity(name = "Transport", bucket = Bucket.NEEDS, colorHex = "#5C6BC0", isDefault = true, sortOrder = 4),
            CategoryEntity(name = "Insurance", bucket = Bucket.NEEDS, colorHex = "#5C6BC0", isDefault = true, sortOrder = 5),
            CategoryEntity(name = "Eating Out", bucket = Bucket.WANTS, colorHex = "#FFA726", isDefault = true, sortOrder = 6),
            CategoryEntity(name = "Entertainment", bucket = Bucket.WANTS, colorHex = "#FFA726", isDefault = true, sortOrder = 7),
            CategoryEntity(name = "Shopping", bucket = Bucket.WANTS, colorHex = "#FFA726", isDefault = true, sortOrder = 8),
            CategoryEntity(name = "Subscriptions", bucket = Bucket.WANTS, colorHex = "#FFA726", isDefault = true, sortOrder = 9),
            CategoryEntity(name = "Savings", bucket = Bucket.SAVINGS, colorHex = "#26A69A", isDefault = true, sortOrder = 10),
            CategoryEntity(name = "Investments", bucket = Bucket.SAVINGS, colorHex = "#26A69A", isDefault = true, sortOrder = 11),
            CategoryEntity(name = "Debt Payment", bucket = Bucket.SAVINGS, colorHex = "#26A69A", isDefault = true, sortOrder = 12),
        )

        private val DEFAULT_ACCOUNT = AccountEntity(
            name = "Cash",
            type = AccountType.CASH,
            startingBalanceCents = 0L,
            colorHex = "#607D8B",
            sortOrder = 0,
        )
    }
}
