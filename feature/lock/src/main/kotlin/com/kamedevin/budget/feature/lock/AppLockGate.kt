package com.kamedevin.budget.feature.lock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kamedevin.budget.core.domain.repository.AppLockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class AppLockGateViewModel @Inject constructor(
    appLockRepository: AppLockRepository,
    sessionState: AppLockSessionState,
) : ViewModel() {
    val isLockEnabled: StateFlow<Boolean> = appLockRepository.observeIsLockEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)
    val isUnlocked: StateFlow<Boolean> = sessionState.isUnlocked
}

/**
 * Wraps [content], showing [LockScreen] instead whenever a PIN is set and the session hasn't
 * been unlocked yet. Used at the root of both MainActivity and the widget's QuickAddActivity so
 * the lock can't be bypassed by going through the widget.
 *
 * [isLockEnabled] defaults to `true` while first loading (rather than `false`) so there's no
 * frame where locked content is briefly visible before the real DataStore value arrives.
 */
@Composable
fun AppLockGate(
    viewModel: AppLockGateViewModel = hiltViewModel(),
    content: @Composable () -> Unit,
) {
    val isLockEnabled by viewModel.isLockEnabled.collectAsStateWithLifecycle()
    val isUnlocked by viewModel.isUnlocked.collectAsStateWithLifecycle()

    if (isLockEnabled && !isUnlocked) {
        LockScreen()
    } else {
        content()
    }
}
