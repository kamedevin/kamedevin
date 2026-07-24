package com.kamedevin.budget.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamedevin.budget.core.domain.usecase.GetAccountBalancesUseCase
import com.kamedevin.budget.core.domain.usecase.GetBudgetProgressUseCase
import com.kamedevin.budget.core.model.AccountBalance
import com.kamedevin.budget.core.model.BudgetProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val progress: BudgetProgress? = null,
    val accountBalances: List<AccountBalance> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    getBudgetProgress: GetBudgetProgressUseCase,
    getAccountBalances: GetAccountBalancesUseCase,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        getBudgetProgress(YearMonth.now()),
        getAccountBalances(),
    ) { progress, balances ->
        HomeUiState(progress = progress, accountBalances = balances, isLoading = false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())
}
