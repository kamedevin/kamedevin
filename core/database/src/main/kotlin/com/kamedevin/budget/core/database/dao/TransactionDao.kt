package com.kamedevin.budget.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kamedevin.budget.core.database.entity.TransactionEntity
import com.kamedevin.budget.core.model.Bucket
import java.time.Instant
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun observeInRange(start: Instant, end: Instant): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT c.bucket AS bucket, SUM(t.amountCents) AS totalCents
        FROM transactions t
        INNER JOIN categories c ON c.id = t.categoryId
        WHERE t.type = 'EXPENSE' AND t.date BETWEEN :start AND :end AND c.bucket IS NOT NULL
        GROUP BY c.bucket
        """,
    )
    fun observeBucketSpend(start: Instant, end: Instant): Flow<List<BucketSpendRow>>

    @Query(
        """
        SELECT COALESCE(SUM(amountCents), 0) FROM transactions
        WHERE type = 'INCOME' AND date BETWEEN :start AND :end
        """,
    )
    fun observeIncome(start: Instant, end: Instant): Flow<Long>

    @Query(
        """
        SELECT strftime('%Y-%m-%d', date / 1000, 'unixepoch') AS day,
               SUM(CASE WHEN type = 'EXPENSE' THEN amountCents ELSE 0 END) AS expenseCents,
               SUM(CASE WHEN type = 'INCOME' THEN amountCents ELSE 0 END) AS incomeCents
        FROM transactions
        WHERE date BETWEEN :start AND :end
        GROUP BY day
        ORDER BY day
        """,
    )
    fun observeDailyTotals(start: Instant, end: Instant): Flow<List<DailyTotalRow>>

    @Query(
        """
        SELECT a.id AS accountId, SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amountCents ELSE 0 END) AS spentCents
        FROM transactions t
        INNER JOIN accounts a ON a.id = t.accountId
        WHERE t.date BETWEEN :start AND :end
        GROUP BY a.id
        """,
    )
    fun observeSpendByAccount(start: Instant, end: Instant): Flow<List<AccountSpendRow>>
}

data class BucketSpendRow(
    val bucket: Bucket,
    val totalCents: Long,
)

data class DailyTotalRow(
    val day: String,
    val expenseCents: Long,
    val incomeCents: Long,
)

data class AccountSpendRow(
    val accountId: Long,
    val spentCents: Long,
)
