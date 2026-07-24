package com.kamedevin.budget.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Singleton row (id is always [SINGLETON_ID]) holding the 50/30/20 percentages. */
@Entity(tableName = "budget_settings")
data class BudgetSettingsEntity(
    @PrimaryKey val id: Int = SINGLETON_ID,
    val needsPercent: Float = 50f,
    val wantsPercent: Float = 30f,
    val savingsPercent: Float = 20f,
) {
    companion object {
        const val SINGLETON_ID = 0
    }
}
