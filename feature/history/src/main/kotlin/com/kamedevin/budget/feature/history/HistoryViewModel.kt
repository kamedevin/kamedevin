package com.kamedevin.budget.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamedevin.budget.core.domain.repository.AccountRepository
import com.kamedevin.budget.core.domain.repository.CategoryRepository
import com.kamedevin.budget.core.domain.repository.DailyTotal
import com.kamedevin.budget.core.domain.repository.TransactionRepository
import com.kamedevin.budget.core.domain.usecase.GetPeriodComparisonUseCase
import com.kamedevin.budget.core.domain.usecase.GetSpendingTrendUseCase
import com.kamedevin.budget.core.domain.usecase.PeriodComparison
import com.kamedevin.budget.core.model.Account
import com.kamedevin.budget.core.model.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AccountSpend(val account: Account, val spentCents: Long)

data class TransactionDisplay(
    val transaction: Transaction,
    val categoryName: String,
    val accountName: String,
)

data class HistoryUiState(
    val dailyTotals: List<DailyTotal> = emptyList(),
    val periodComparison: PeriodComparison? = null,
    val accountSpend: List<AccountSpend> = emptyList(),
    val transactions: List<TransactionDisplay> = emptyList(),
    val isLoading: Boolean = true,
)

private data class PeriodStats(
    val dailyTotals: List<DailyTotal>,
    val periodComparison: PeriodComparison,
    val accountSpend: List<AccountSpend>,
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    getSpendingTrend: GetSpendingTrendUseCase,
    getPeriodComparison: GetPeriodComparisonUseCase,
    private val transactionRepository: TransactionRepository,
    accountRepository: AccountRepository,
    categoryRepository: CategoryRepository,
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState>

    init {
        val period = YearMonth.now()
        val zone = ZoneId.systemDefault()
        val start = period.atDay(1).atStartOfDay(zone).toInstant()
        val end = period.plusMonths(1).atDay(1).atStartOfDay(zone).toInstant()

        // combine() only has typed overloads up to 5 flows; these differ in type, so the two
        // groups are combined separately and merged, rather than reaching for the vararg overload
        // (which requires every flow to share one element type).
        val periodStats = combine(
            getSpendingTrend(start, end),
            getPeriodComparison(period, zone),
            transactionRepository.observeSpendByAccount(start, end),
            accountRepository.observeActive(),
        ) { dailyTotals, comparison, spendByAccount, accounts ->
            PeriodStats(
                dailyTotals = dailyTotals,
                periodComparison = comparison,
                accountSpend = accounts.map { AccountSpend(it, spendByAccount[it.id] ?: 0L) },
            )
        }

        val transactionList = combine(
            transactionRepository.observeInRange(start, end),
            categoryRepository.observeAll(),
            accountRepository.observeActive(),
        ) { transactions, categories, accounts ->
            val categoryNames = categories.associate { it.id to it.name }
            val accountNames = accounts.associate { it.id to it.name }
            transactions
                .sortedByDescending { it.date }
                .map { transaction ->
                    TransactionDisplay(
                        transaction = transaction,
                        categoryName = categoryNames[transaction.categoryId] ?: "Unknown",
                        accountName = accountNames[transaction.accountId] ?: "Unknown",
                    )
                }
        }

        uiState = combine(periodStats, transactionList) { stats, transactions ->
            HistoryUiState(
                dailyTotals = stats.dailyTotals,
                periodComparison = stats.periodComparison,
                accountSpend = stats.accountSpend,
                transactions = transactions,
                isLoading = false,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HistoryUiState())
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch { transactionRepository.delete(transaction.id) }
    }
}
