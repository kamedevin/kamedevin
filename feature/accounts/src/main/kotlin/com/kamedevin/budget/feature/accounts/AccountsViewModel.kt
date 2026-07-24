package com.kamedevin.budget.feature.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamedevin.budget.core.domain.repository.AccountRepository
import com.kamedevin.budget.core.model.Account
import com.kamedevin.budget.core.model.AccountBalance
import com.kamedevin.budget.core.model.AccountType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AccountsUiState(
    val accounts: List<AccountBalance> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
) : ViewModel() {

    val uiState: StateFlow<AccountsUiState> = accountRepository.observeAllBalances()
        .map { balances -> AccountsUiState(accounts = balances, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AccountsUiState())

    fun addAccount(name: String, type: AccountType, startingBalanceCents: Long) {
        viewModelScope.launch {
            accountRepository.add(
                Account(
                    name = name,
                    type = type,
                    startingBalanceCents = startingBalanceCents,
                    colorHex = "#607D8B",
                ),
            )
        }
    }

    fun archiveAccount(account: Account) {
        viewModelScope.launch { accountRepository.archive(account) }
    }
}
