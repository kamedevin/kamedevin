package com.kamedevin.budget.core.domain.usecase

import com.kamedevin.budget.core.domain.repository.BudgetSettingsRepository
import com.kamedevin.budget.core.domain.repository.TransactionRepository
import com.kamedevin.budget.core.model.Bucket
import com.kamedevin.budget.core.model.BucketProgress
import com.kamedevin.budget.core.model.BudgetProgress
import com.kamedevin.budget.core.model.allocateBucketTargets
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/** Computes 50/30/20 progress for [period], shared by the app UI and the widget. */
class GetBudgetProgressUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val budgetSettingsRepository: BudgetSettingsRepository,
) {
    operator fun invoke(period: YearMonth, zoneId: ZoneId = ZoneId.systemDefault()): Flow<BudgetProgress> {
        val start = period.atDay(1).atStartOfDay(zoneId).toInstant()
        val end = period.plusMonths(1).atDay(1).atStartOfDay(zoneId).toInstant()

        return combine(
            transactionRepository.observeIncome(start, end),
            transactionRepository.observeBucketSpend(start, end),
            budgetSettingsRepository.observeSettings(),
        ) { incomeCents, spendByBucket, settings ->
            val targets = allocateBucketTargets(incomeCents, settings)
            BudgetProgress(
                period = period,
                incomeCents = incomeCents,
                buckets = Bucket.entries.map { bucket ->
                    BucketProgress(
                        bucket = bucket,
                        spentCents = spendByBucket[bucket] ?: 0L,
                        targetCents = targets.getValue(bucket),
                    )
                },
            )
        }
    }
}
