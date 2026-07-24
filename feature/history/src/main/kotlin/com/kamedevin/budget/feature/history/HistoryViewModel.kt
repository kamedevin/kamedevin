package com.kamedevin.budget.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamedevin.budget.core.domain.repository.AccountRepository
import com.kamedevin.budget.core.domain.repository.DailyTotal
import com.kamedevin.budget.core.domain.repository.TransactionRepository
import com.kamedevin.budget.core.domain.usecase.GetPeriodComparisonUseCase
import com.kamedevin.budget.core.domain.usecase.GetSpendingTrendUseCase
import com.kamedevin.budget.core.domain.usecase.PeriodComparison
import com.kamedevin.budget.core.model.Account
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class AccountSpend(val account: Account, val spentCents: Long)

data class HistoryUiState(
    val dailyTotals: List<DailyTotal> = emptyList(),
    val periodComparison: PeriodComparison? = null,
    val accountSpend: List<AccountSpend> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    getSpendingTrend: GetSpendingTrendUseCase,
    getPeriodComparison: GetPeriodComparisonUseCase,
    transactionRepository: TransactionRepository,
    accountRepository: AccountRepository,
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState>

    init {
        val period = YearMonth.now()
        val zone = ZoneId.systemDefault()
        val start = period.atDay(1).atStartOfDay(zone).toInstant()
        val end = period.plusMonths(1).atDay(1).atStartOfDay(zone).toInstant()

        uiState = combine(
            getSpendingTrend(start, end),
            getPeriodComparison(period, zone),
            transactionRepository.observeSpendByAccount(start, end),
            accountRepository.observeActive(),
        ) { dailyTotals, comparison, spendByAccount, accounts ->
            HistoryUiState(
                dailyTotals = dailyTotals,
                periodComparison = comparison,
                accountSpend = accounts.map { AccountSpend(it, spendByAccount[it.id] ?: 0L) },
                isLoading = false,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HistoryUiState())
    }
}
