package com.kamedevin.budget.core.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kamedevin.budget.core.data.datastore.appDataStore
import com.kamedevin.budget.core.domain.repository.ThemePreferenceRepository
import com.kamedevin.budget.core.model.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemePreferenceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : ThemePreferenceRepository {

    private val themeModeKey = stringPreferencesKey("theme_mode")

    override fun observeThemeMode(): Flow<ThemeMode> =
        context.appDataStore.data.map { prefs ->
            prefs[themeModeKey]?.let { stored -> runCatching { ThemeMode.valueOf(stored) }.getOrNull() }
                ?: ThemeMode.SYSTEM
        }

    override suspend fun setThemeMode(mode: ThemeMode) {
        context.appDataStore.edit { prefs ->
            prefs[themeModeKey] = mode.name
        }
    }
}
