package com.kamedevin.budget.widget.di

import com.kamedevin.budget.core.domain.repository.ThemePreferenceRepository
import com.kamedevin.budget.core.domain.repository.WidgetPreferenceRepository
import com.kamedevin.budget.core.domain.usecase.GetBudgetProgressUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * [com.kamedevin.budget.widget.BudgetGlanceWidget] isn't an Activity/Fragment/BroadcastReceiver,
 * so Hilt can't field-inject it directly — this entry point lets it pull what it needs out of
 * the singleton Hilt graph using the app [android.content.Context] it's already given.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun getBudgetProgressUseCase(): GetBudgetProgressUseCase
    fun widgetPreferenceRepository(): WidgetPreferenceRepository
    fun themePreferenceRepository(): ThemePreferenceRepository
}
