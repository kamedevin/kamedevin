package com.kamedevin.budget.core.domain.repository

import com.kamedevin.budget.core.model.WidgetStyle
import kotlinx.coroutines.flow.Flow

interface WidgetPreferenceRepository {
    fun observeWidgetStyle(): Flow<WidgetStyle>
    suspend fun setWidgetStyle(style: WidgetStyle)
}
