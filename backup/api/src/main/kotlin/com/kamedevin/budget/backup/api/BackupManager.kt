package com.kamedevin.budget.backup.api

import android.app.Activity
import java.time.Instant
import kotlinx.coroutines.flow.Flow

data class BackupMetadata(
    val fileId: String,
    val createdAt: Instant,
    val sizeBytes: Long,
    val appVersion: Int,
)

/**
 * The swap point for cloud backup: [com.kamedevin.budget.feature.settings] depends only on this
 * interface, never on a concrete provider, so backup storage can change (or gain a second
 * provider) without touching Settings UI or any other caller.
 */
interface BackupManager {
    suspend fun isSignedIn(): Boolean
    suspend fun signIn(activity: Activity): Result<Unit>
    suspend fun signOut()
    suspend fun backupNow(): Result<BackupMetadata>
    suspend fun restoreLatest(): Result<Unit>
    suspend fun listBackups(): Result<List<BackupMetadata>>
    fun observeLastBackupTime(): Flow<Instant?>
}
