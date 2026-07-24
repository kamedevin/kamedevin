package com.kamedevin.budget.backup.googledrive

import androidx.room.withTransaction
import com.kamedevin.budget.core.database.AppDatabase
import com.kamedevin.budget.core.database.entity.BudgetSettingsEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/**
 * Reads/writes the whole local database for backup/restore. Goes straight to [AppDatabase]'s DAOs
 * rather than through the app's normal repositories, since a bulk "replace everything" operation
 * is a fundamentally different concern from the additive, observational repository API the rest
 * of the app uses.
 */
class BackupSnapshotRepository @Inject constructor(
    private val database: AppDatabase,
) {
    suspend fun buildSnapshot(): BackupSnapshot {
        val categories = database.categoryDao().getAll().map { it.toSnapshot() }
        val accounts = database.accountDao().getAll().map { it.toSnapshot() }
        val transactions = database.transactionDao().getAll().map { it.toSnapshot() }
        val settings = database.budgetSettingsDao().observe().first() ?: BudgetSettingsEntity()

        return BackupSnapshot(
            categories = categories,
            accounts = accounts,
            transactions = transactions,
            budgetSettings = settings.toSnapshot(),
        )
    }

    /**
     * Transactions are deleted before categories/accounts (and inserted after them) because the
     * FK constraints from transactions to categories/accounts are RESTRICT — violating that order
     * would fail the whole transaction.
     */
    suspend fun applySnapshot(snapshot: BackupSnapshot) {
        database.withTransaction {
            database.transactionDao().deleteAll()
            database.categoryDao().deleteAll()
            database.accountDao().deleteAll()

            database.categoryDao().insertAll(snapshot.categories.map { it.toEntity() })
            database.accountDao().insertAll(snapshot.accounts.map { it.toEntity() })
            database.transactionDao().insertAll(snapshot.transactions.map { it.toEntity() })
            database.budgetSettingsDao().upsert(snapshot.budgetSettings.toEntity())
        }
    }
}
