package com.kamedevin.budget.core.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.kamedevin.budget.core.database.entity.AccountEntity
import com.kamedevin.budget.core.database.entity.CategoryEntity
import com.kamedevin.budget.core.database.entity.TransactionEntity
import com.kamedevin.budget.core.model.AccountType
import com.kamedevin.budget.core.model.Bucket
import com.kamedevin.budget.core.model.TransactionType
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertEquals

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var database: AppDatabase

    @Before
    fun createDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun bucketSpend_sumsExpensesGroupedByBucket() = runTest {
        val groceriesId = database.categoryDao()
            .insert(CategoryEntity(name = "Groceries", bucket = Bucket.NEEDS, colorHex = "#000000"))
        val diningId = database.categoryDao()
            .insert(CategoryEntity(name = "Dining", bucket = Bucket.WANTS, colorHex = "#000000"))
        val accountId = database.accountDao()
            .insert(AccountEntity(name = "Checking", type = AccountType.CHECKING, startingBalanceCents = 0, colorHex = "#000000"))

        val now = Instant.now()
        database.transactionDao().insert(
            transaction(amountCents = 5_000, categoryId = groceriesId, accountId = accountId, date = now),
        )
        database.transactionDao().insert(
            transaction(amountCents = 2_000, categoryId = diningId, accountId = accountId, date = now),
        )
        database.transactionDao().insert(
            transaction(amountCents = 1_500, categoryId = diningId, accountId = accountId, date = now),
        )

        val spend = database.transactionDao()
            .observeBucketSpend(now.minus(1, ChronoUnit.DAYS), now.plus(1, ChronoUnit.DAYS))
            .first()
            .associate { it.bucket to it.totalCents }

        assertEquals(5_000L, spend[Bucket.NEEDS])
        assertEquals(3_500L, spend[Bucket.WANTS])
    }

    @Test
    fun accountBalance_reflectsStartingBalancePlusTransactions() = runTest {
        val categoryId = database.categoryDao()
            .insert(CategoryEntity(name = "Income", bucket = null, colorHex = "#000000"))
        val accountId = database.accountDao().insert(
            AccountEntity(name = "Checking", type = AccountType.CHECKING, startingBalanceCents = 10_000, colorHex = "#000000"),
        )
        val now = Instant.now()
        database.transactionDao().insert(
            transaction(
                amountCents = 3_000,
                type = TransactionType.INCOME,
                categoryId = categoryId,
                accountId = accountId,
                date = now,
            ),
        )
        database.transactionDao().insert(
            transaction(
                amountCents = 1_000,
                type = TransactionType.EXPENSE,
                categoryId = categoryId,
                accountId = accountId,
                date = now,
            ),
        )

        val balance = database.accountDao().observeBalance(accountId).first()

        assertEquals(12_000L, balance)
    }

    private fun transaction(
        amountCents: Long,
        type: TransactionType = TransactionType.EXPENSE,
        categoryId: Long,
        accountId: Long,
        date: Instant,
    ) = TransactionEntity(
        amountCents = amountCents,
        type = type,
        categoryId = categoryId,
        accountId = accountId,
        date = date,
        createdAt = date,
        updatedAt = date,
    )
}
