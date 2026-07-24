package com.kamedevin.budget.core.domain.repository

import com.kamedevin.budget.core.model.Bucket
import com.kamedevin.budget.core.model.Transaction
import java.time.Instant
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    /**
     * Adds a fully-formed [Transaction]. There is no separate manual-vs-synced variant of this
     * call — [Transaction.source] carries that distinction, so a future automatic bank-sync
     * integration can write through this same method with `source = TransactionSource.SYNCED`.
     */
    suspend fun add(transaction: Transaction): Long
    suspend fun update(transaction: Transaction)
    suspend fun delete(id: Long)
    suspend fun getById(id: Long): Transaction?
    fun observeInRange(start: Instant, end: Instant): Flow<List<Transaction>>
    fun observeBucketSpend(start: Instant, end: Instant): Flow<Map<Bucket, Long>>
    fun observeIncome(start: Instant, end: Instant): Flow<Long>
    fun observeDailyTotals(start: Instant, end: Instant): Flow<List<DailyTotal>>
    fun observeSpendByAccount(start: Instant, end: Instant): Flow<Map<Long, Long>>
}

data class DailyTotal(
    val day: String,
    val expenseCents: Long,
    val incomeCents: Long,
)
