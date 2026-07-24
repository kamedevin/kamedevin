package com.kamedevin.budget.core.data.repository

import com.kamedevin.budget.core.database.dao.BudgetSettingsDao
import com.kamedevin.budget.core.database.entity.BudgetSettingsEntity
import com.kamedevin.budget.core.domain.repository.BudgetSettingsRepository
import com.kamedevin.budget.core.model.BudgetSettings
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BudgetSettingsRepositoryImpl @Inject constructor(
    private val budgetSettingsDao: BudgetSettingsDao,
) : BudgetSettingsRepository {

    override fun observeSettings(): Flow<BudgetSettings> =
        budgetSettingsDao.observe().map { entity ->
            entity?.let { BudgetSettings(it.needsPercent, it.wantsPercent, it.savingsPercent) }
                ?: BudgetSettings()
        }

    override suspend fun updateSettings(settings: BudgetSettings) {
        budgetSettingsDao.upsert(
            BudgetSettingsEntity(
                needsPercent = settings.needsPercent,
                wantsPercent = settings.wantsPercent,
                savingsPercent = settings.savingsPercent,
            ),
        )
    }
}
