package com.kamedevin.budget.feature.lock

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * In-memory "has the user entered the correct PIN this session" flag. Singleton-scoped so it's
 * shared across every entry point in the process — the main app (MainActivity) and the widget's
 * QuickAddActivity both read/write the same instance, since otherwise unlocking the app wouldn't
 * also unlock the widget's quick-add dialog (or worse, the widget would bypass the lock entirely).
 * Reset to locked whenever the whole app goes to background — see BudgetApplication.
 */
@Singleton
class AppLockSessionState @Inject constructor() {
    private val _isUnlocked = MutableStateFlow(false)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked

    fun markUnlocked() {
        _isUnlocked.value = true
    }

    fun markLocked() {
        _isUnlocked.value = false
    }
}
