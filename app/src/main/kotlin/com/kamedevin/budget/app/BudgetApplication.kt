package com.kamedevin.budget.app

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.kamedevin.budget.feature.lock.AppLockSessionState
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BudgetApplication : Application() {

    @Inject
    lateinit var appLockSessionState: AppLockSessionState

    override fun onCreate() {
        super.onCreate()

        // Whole-app backgrounding (not per-Activity onStop, so switching between MainActivity and
        // the widget's QuickAddActivity doesn't spuriously re-lock) re-locks the session.
        ProcessLifecycleOwner.get().lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onStop(owner: LifecycleOwner) {
                    appLockSessionState.markLocked()
                }
            },
        )
    }
}
