package com.kamedevin.budget.core.data.repository

import com.kamedevin.budget.core.data.mapper.toDomain
import com.kamedevin.budget.core.data.mapper.toEntity
import com.kamedevin.budget.core.database.dao.TransactionDao
import com.kamedevin.budget.core.domain.repository.DailyTotal
import com.kamedevin.budget.core.domain.repository.TransactionRepository
import com.kamedevin.budget.core.model.Bucket
import com.kamedevin.budget.core.model.Transaction
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
) : TransactionRepository {

    override suspend fun add(transaction: Transaction): Long = transactionDao.insert(transaction.toEntity())

    override suspend fun update(transaction: Transaction) {
        transactionDao.update(transaction.toEntity())
    }

    override suspend fun delete(id: Long) {
        transactionDao.deleteById(id)
    }

    override suspend fun getById(id: Long): Transaction? = transactionDao.getById(id)?.toDomain()

    override fun observeInRange(start: Instant, end: Instant): Flow<List<Transaction>> =
        transactionDao.observeInRange(start, end).map { list -> list.map { it.toDomain() } }

    override fun observeBucketSpend(start: Instant, end: Instant): Flow<Map<Bucket, Long>> =
        transactionDao.observeBucketSpend(start, end).map { rows ->
            rows.associate { it.bucket to it.totalCents }
        }

    override fun observeIncome(start: Instant, end: Instant): Flow<Long> =
        transactionDao.observeIncome(start, end)

    override fun observeDailyTotals(start: Instant, end: Instant): Flow<List<DailyTotal>> =
        transactionDao.observeDailyTotals(start, end).map { rows ->
            rows.map { DailyTotal(it.day, it.expenseCents, it.incomeCents) }
        }

    override fun observeSpendByAccount(start: Instant, end: Instant): Flow<Map<Long, Long>> =
        transactionDao.observeSpendByAccount(start, end).map { rows ->
            rows.associate { it.accountId to it.spentCents }
        }
}
