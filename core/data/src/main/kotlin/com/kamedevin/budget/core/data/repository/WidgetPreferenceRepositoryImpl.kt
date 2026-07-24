package com.kamedevin.budget.core.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kamedevin.budget.core.data.datastore.appDataStore
import com.kamedevin.budget.core.domain.repository.WidgetPreferenceRepository
import com.kamedevin.budget.core.model.WidgetStyle
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WidgetPreferenceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : WidgetPreferenceRepository {

    private val widgetStyleKey = stringPreferencesKey("widget_style")

    override fun observeWidgetStyle(): Flow<WidgetStyle> =
        context.appDataStore.data.map { prefs ->
            prefs[widgetStyleKey]?.let { stored -> runCatching { WidgetStyle.valueOf(stored) }.getOrNull() }
                ?: WidgetStyle.BARS
        }

    override suspend fun setWidgetStyle(style: WidgetStyle) {
        context.appDataStore.edit { prefs ->
            prefs[widgetStyleKey] = style.name
        }
    }
}
