package com.kamedevin.budget.feature.lock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamedevin.budget.core.domain.repository.AppLockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val PIN_LENGTH = 4

data class PinEntryUiState(
    val enteredPin: String = "",
    val error: String? = null,
)

/** Drives the unlock (existing-PIN entry) flow shown by [LockScreen]. */
@HiltViewModel
class AppLockViewModel @Inject constructor(
    private val appLockRepository: AppLockRepository,
    private val sessionState: AppLockSessionState,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinEntryUiState())
    val uiState: StateFlow<PinEntryUiState> = _uiState.asStateFlow()

    fun onDigitEntered(digit: Char) {
        val current = _uiState.value.enteredPin
        if (current.length >= PIN_LENGTH) return
        val updated = current + digit
        _uiState.update { it.copy(enteredPin = updated, error = null) }
        if (updated.length == PIN_LENGTH) {
            verify(updated)
        }
    }

    fun onBackspace() {
        _uiState.update { it.copy(enteredPin = it.enteredPin.dropLast(1), error = null) }
    }

    private fun verify(pin: String) {
        viewModelScope.launch {
            if (appLockRepository.verifyPin(pin)) {
                sessionState.markUnlocked()
            } else {
                _uiState.update { it.copy(enteredPin = "", error = "Incorrect PIN") }
            }
        }
    }
}
