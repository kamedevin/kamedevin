package com.kamedevin.budget.core.domain.repository

import com.kamedevin.budget.core.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface ThemePreferenceRepository {
    fun observeThemeMode(): Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)
}
