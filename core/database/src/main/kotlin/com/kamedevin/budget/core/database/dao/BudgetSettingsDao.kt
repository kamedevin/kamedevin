package com.kamedevin.budget.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kamedevin.budget.core.database.entity.BudgetSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetSettingsDao {
    @Upsert
    suspend fun upsert(settings: BudgetSettingsEntity)

    @Query("SELECT * FROM budget_settings WHERE id = 0")
    fun observe(): Flow<BudgetSettingsEntity?>
}
