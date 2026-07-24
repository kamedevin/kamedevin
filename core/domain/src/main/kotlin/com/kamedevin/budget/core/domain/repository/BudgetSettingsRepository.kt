package com.kamedevin.budget.core.domain.repository

import com.kamedevin.budget.core.model.BudgetSettings
import kotlinx.coroutines.flow.Flow

interface BudgetSettingsRepository {
    fun observeSettings(): Flow<BudgetSettings>
    suspend fun updateSettings(settings: BudgetSettings)
}
