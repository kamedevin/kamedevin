package com.kamedevin.budget.backup.googledrive

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.backupDataStore by preferencesDataStore(name = "backup_prefs")

class BackupPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val lastBackupTimeKey = longPreferencesKey("last_backup_time_millis")

    fun observeLastBackupTime(): Flow<Instant?> =
        context.backupDataStore.data.map { prefs ->
            prefs[lastBackupTimeKey]?.let { Instant.ofEpochMilli(it) }
        }

    suspend fun setLastBackupTime(instant: Instant) {
        context.backupDataStore.edit { prefs ->
            prefs[lastBackupTimeKey] = instant.toEpochMilli()
        }
    }
}
