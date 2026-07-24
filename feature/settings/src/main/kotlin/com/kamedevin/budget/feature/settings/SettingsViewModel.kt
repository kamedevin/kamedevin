package com.kamedevin.budget.feature.settings

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamedevin.budget.backup.api.BackupManager
import com.kamedevin.budget.core.domain.repository.ThemePreferenceRepository
import com.kamedevin.budget.core.model.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isSignedIn: Boolean = false,
    val lastBackupTime: Instant? = null,
    val isWorking: Boolean = false,
    val message: String? = null,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val backupManager: BackupManager,
    private val themePreferenceRepository: ThemePreferenceRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    val themeMode: StateFlow<ThemeMode> = themePreferenceRepository.observeThemeMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeMode.SYSTEM)

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isSignedIn = backupManager.isSignedIn()) }
        }
        viewModelScope.launch {
            backupManager.observeLastBackupTime().collect { time ->
                _uiState.update { it.copy(lastBackupTime = time) }
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { themePreferenceRepository.setThemeMode(mode) }
    }

    fun signIn(activity: Activity) {
        viewModelScope.launch {
            _uiState.update { it.copy(isWorking = true, message = null) }
            val result = backupManager.signIn(activity)
            _uiState.update {
                it.copy(
                    isWorking = false,
                    isSignedIn = result.isSuccess,
                    message = result.exceptionOrNull()?.message,
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            backupManager.signOut()
            _uiState.update { it.copy(isSignedIn = false) }
        }
    }

    fun backupNow() {
        viewModelScope.launch {
            _uiState.update { it.copy(isWorking = true, message = null) }
            val result = backupManager.backupNow()
            _uiState.update {
                it.copy(
                    isWorking = false,
                    message = result.fold(
                        onSuccess = { "Backup complete" },
                        onFailure = { e -> e.message ?: "Backup failed" },
                    ),
                )
            }
        }
    }

    fun restoreLatest() {
        viewModelScope.launch {
            _uiState.update { it.copy(isWorking = true, message = null) }
            val result = backupManager.restoreLatest()
            _uiState.update {
                it.copy(
                    isWorking = false,
                    message = result.fold(
                        onSuccess = { "Restore complete" },
                        onFailure = { e -> e.message ?: "Restore failed" },
                    ),
                )
            }
        }
    }
}
