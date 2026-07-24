package com.kamedevin.budget.core.domain.usecase

import com.kamedevin.budget.core.domain.repository.DailyTotal
import com.kamedevin.budget.core.domain.repository.TransactionRepository
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetSpendingTrendUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(start: Instant, end: Instant): Flow<List<DailyTotal>> =
        transactionRepository.observeDailyTotals(start, end)
}
