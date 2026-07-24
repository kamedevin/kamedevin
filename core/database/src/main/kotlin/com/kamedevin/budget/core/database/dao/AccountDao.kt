package com.kamedevin.budget.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kamedevin.budget.core.database.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Insert
    suspend fun insert(account: AccountEntity): Long

    @Update
    suspend fun update(account: AccountEntity)

    @Delete
    suspend fun delete(account: AccountEntity)

    @Query("SELECT * FROM accounts WHERE isArchived = 0 ORDER BY sortOrder, name")
    fun observeActive(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts ORDER BY sortOrder, name")
    fun observeAll(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getById(id: Long): AccountEntity?

    @Query("SELECT COUNT(*) FROM accounts")
    suspend fun count(): Int

    @Query(
        """
        SELECT a.startingBalanceCents +
            COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amountCents ELSE -t.amountCents END), 0)
        FROM accounts a
        LEFT JOIN transactions t ON t.accountId = a.id
        WHERE a.id = :accountId
        GROUP BY a.id
        """,
    )
    fun observeBalance(accountId: Long): Flow<Long?>

    @Query(
        """
        SELECT a.id AS id, a.startingBalanceCents +
            COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amountCents ELSE -t.amountCents END), 0) AS balanceCents
        FROM accounts a
        LEFT JOIN transactions t ON t.accountId = a.id
        WHERE a.isArchived = 0
        GROUP BY a.id
        """,
    )
    fun observeAllBalances(): Flow<List<AccountBalanceRow>>
}

data class AccountBalanceRow(
    val id: Long,
    val balanceCents: Long,
)
