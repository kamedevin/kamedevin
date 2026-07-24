package com.kamedevin.budget.backup.api

import android.content.Intent
import androidx.activity.result.ActivityResult
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Bridges a suspend function (e.g. [BackupManager.signIn]) to a sign-in [Intent] that only the
 * foreground Activity can launch and receive a result from. The hosting Activity registers an
 * `ActivityResultLauncher` once in `onCreate` and forwards launch requests/results through here,
 * so a Singleton-scoped BackupManager impl never needs to hold an Activity reference itself.
 */
@Singleton
class SignInResultCoordinator @Inject constructor() {

    private val _launchRequests = MutableSharedFlow<Intent>(extraBufferCapacity = 1)
    val launchRequests: SharedFlow<Intent> = _launchRequests.asSharedFlow()

    private var pendingResult: CompletableDeferred<ActivityResult>? = null

    suspend fun launchAndAwaitResult(intent: Intent): ActivityResult {
        val deferred = CompletableDeferred<ActivityResult>()
        pendingResult = deferred
        _launchRequests.emit(intent)
        return deferred.await()
    }

    fun deliverResult(result: ActivityResult) {
        pendingResult?.complete(result)
        pendingResult = null
    }
}
