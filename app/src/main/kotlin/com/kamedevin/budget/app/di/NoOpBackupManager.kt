package com.kamedevin.budget.app.di

import android.app.Activity
import com.kamedevin.budget.backup.api.BackupManager
import com.kamedevin.budget.backup.api.BackupMetadata
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Placeholder bound until [com.kamedevin.budget.backup.googledrive] (M10) is wired in — lets
 * Settings compile and run today without a real Google Drive integration.
 */
class NoOpBackupManager @Inject constructor() : BackupManager {
    private val lastBackupTime = MutableStateFlow<Instant?>(null)

    override suspend fun isSignedIn(): Boolean = false

    override suspend fun signIn(activity: Activity): Result<Unit> =
        Result.failure(UnsupportedOperationException("Google Drive backup isn't wired up yet"))

    override suspend fun signOut() = Unit

    override suspend fun backupNow(): Result<BackupMetadata> =
        Result.failure(UnsupportedOperationException("Google Drive backup isn't wired up yet"))

    override suspend fun restoreLatest(): Result<Unit> =
        Result.failure(UnsupportedOperationException("Google Drive backup isn't wired up yet"))

    override suspend fun listBackups(): Result<List<BackupMetadata>> = Result.success(emptyList())

    override fun observeLastBackupTime(): Flow<Instant?> = lastBackupTime
}
