package com.kamedevin.budget.feature.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamedevin.budget.core.domain.repository.AccountRepository
import com.kamedevin.budget.core.domain.repository.CategoryRepository
import com.kamedevin.budget.core.domain.usecase.AddTransactionUseCase
import com.kamedevin.budget.core.model.Account
import com.kamedevin.budget.core.model.Category
import com.kamedevin.budget.core.model.Transaction
import com.kamedevin.budget.core.model.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EntryUiState(
    val amountText: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val categories: List<Category> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val selectedCategoryId: Long? = null,
    val selectedAccountId: Long? = null,
    val date: Instant = Instant.now(),
    val note: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
    val savedSuccessfully: Boolean = false,
) {
    val selectedCategory: Category? get() = categories.firstOrNull { it.id == selectedCategoryId }
    val selectedAccount: Account? get() = accounts.firstOrNull { it.id == selectedAccountId }
}

/**
 * Shared entry logic for both the full-app [EntryScreen] and the widget's [QuickAddForm] —
 * keeping one ViewModel means the widget's quick-add flow can never drift from the app's
 * validation rules.
 */
@HiltViewModel
class EntryViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val categoryRepository: CategoryRepository,
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EntryUiState())
    val uiState: StateFlow<EntryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                categoryRepository.observeAll(),
                accountRepository.observeActive(),
            ) { categories, accounts -> categories to accounts }
                .collect { (categories, accounts) ->
                    _uiState.update { state ->
                        state.copy(
                            categories = categories,
                            accounts = accounts,
                            selectedCategoryId = state.selectedCategoryId
                                ?: categories.firstOrNull { it.bucket != null }?.id,
                            selectedAccountId = state.selectedAccountId ?: accounts.firstOrNull()?.id,
                        )
                    }
                }
        }
    }

    fun onAmountChange(text: String) {
        _uiState.update { it.copy(amountText = text, error = null) }
    }

    fun onTypeChange(type: TransactionType) {
        _uiState.update { it.copy(type = type) }
    }

    fun onCategorySelected(id: Long) {
        _uiState.update { it.copy(selectedCategoryId = id) }
    }

    fun onAccountSelected(id: Long) {
        _uiState.update { it.copy(selectedAccountId = id) }
    }

    fun onNoteChange(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun onDateChange(date: Instant) {
        _uiState.update { it.copy(date = date) }
    }

    fun save() {
        val state = _uiState.value
        val cents = state.amountText.toDoubleOrNull()?.let { (it * 100).toLong() }
        val categoryId = state.selectedCategoryId
        val accountId = state.selectedAccountId

        if (cents == null || cents <= 0L) {
            _uiState.update { it.copy(error = "Enter a valid amount") }
            return
        }
        if (categoryId == null || accountId == null) {
            _uiState.update { it.copy(error = "Select a category and account") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            val result = addTransactionUseCase(
                Transaction(
                    amountCents = cents,
                    type = state.type,
                    categoryId = categoryId,
                    accountId = accountId,
                    date = state.date,
                    note = state.note.ifBlank { null },
                ),
            )
            result.fold(
                onSuccess = { _uiState.update { it.copy(isSaving = false, savedSuccessfully = true) } },
                onFailure = { e ->
                    _uiState.update { it.copy(isSaving = false, error = e.message ?: "Couldn't save") }
                },
            )
        }
    }

    fun consumeSavedEvent() {
        _uiState.update { it.copy(savedSuccessfully = false) }
    }
}
