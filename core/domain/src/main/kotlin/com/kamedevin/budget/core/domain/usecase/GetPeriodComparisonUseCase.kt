package com.kamedevin.budget.core.domain.usecase

import com.kamedevin.budget.core.model.BudgetProgress
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class PeriodComparison(
    val current: BudgetProgress,
    val previous: BudgetProgress,
)

class GetPeriodComparisonUseCase @Inject constructor(
    private val getBudgetProgress: GetBudgetProgressUseCase,
) {
    operator fun invoke(period: YearMonth, zoneId: ZoneId = ZoneId.systemDefault()): Flow<PeriodComparison> =
        combine(
            getBudgetProgress(period, zoneId),
            getBudgetProgress(period.minusMonths(1), zoneId),
        ) { current, previous -> PeriodComparison(current, previous) }
}
