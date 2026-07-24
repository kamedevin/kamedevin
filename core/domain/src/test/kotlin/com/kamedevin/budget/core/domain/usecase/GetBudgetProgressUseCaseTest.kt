package com.kamedevin.budget.core.domain.usecase

import app.cash.turbine.test
import com.kamedevin.budget.core.domain.repository.BudgetSettingsRepository
import com.kamedevin.budget.core.domain.repository.DailyTotal
import com.kamedevin.budget.core.domain.repository.TransactionRepository
import com.kamedevin.budget.core.model.Bucket
import com.kamedevin.budget.core.model.BudgetSettings
import com.kamedevin.budget.core.model.Transaction
import java.time.Instant
import java.time.YearMonth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetBudgetProgressUseCaseTest {

    @Test
    fun invoke_combinesIncomeSpendAndSettings_intoBucketProgress() = runTest {
        val income = MutableStateFlow(1_000_00L)
        val spend = MutableStateFlow(mapOf(Bucket.NEEDS to 200_00L, Bucket.WANTS to 100_00L))
        val settings = MutableStateFlow(BudgetSettings())

        val useCase = GetBudgetProgressUseCase(
            transactionRepository = FakeTransactionRepository(income, spend),
            budgetSettingsRepository = FakeBudgetSettingsRepository(settings),
        )

        useCase(YearMonth.of(2026, 7)).test {
            val progress = awaitItem()

            assertEquals(1_000_00L, progress.incomeCents)
            val needs = progress.buckets.first { it.bucket == Bucket.NEEDS }
            val wants = progress.buckets.first { it.bucket == Bucket.WANTS }
            val savings = progress.buckets.first { it.bucket == Bucket.SAVINGS }

            assertEquals(200_00L, needs.spentCents)
            assertEquals(500_00L, needs.targetCents)
            assertEquals(100_00L, wants.spentCents)
            assertEquals(300_00L, wants.targetCents)
            assertEquals(0L, savings.spentCents)
            assertEquals(200_00L, savings.targetCents)
        }
    }

    private class FakeTransactionRepository(
        private val income: Flow<Long>,
        private val bucketSpend: Flow<Map<Bucket, Long>>,
    ) : TransactionRepository {
        override suspend fun add(transaction: Transaction) = error("not used in this test")
        override suspend fun update(transaction: Transaction) = error("not used in this test")
        override suspend fun delete(id: Long) = error("not used in this test")
        override suspend fun getById(id: Long): Transaction? = error("not used in this test")
        override fun observeInRange(start: Instant, end: Instant) = error("not used in this test")
        override fun observeBucketSpend(start: Instant, end: Instant): Flow<Map<Bucket, Long>> = bucketSpend
        override fun observeIncome(start: Instant, end: Instant): Flow<Long> = income
        override fun observeDailyTotals(start: Instant, end: Instant): Flow<List<DailyTotal>> =
            error("not used in this test")
        override fun observeSpendByAccount(start: Instant, end: Instant): Flow<Map<Long, Long>> =
            error("not used in this test")
    }

    private class FakeBudgetSettingsRepository(
        private val settings: Flow<BudgetSettings>,
    ) : BudgetSettingsRepository {
        override fun observeSettings(): Flow<BudgetSettings> = settings
        override suspend fun updateSettings(settings: BudgetSettings) = error("not used in this test")
    }
}
