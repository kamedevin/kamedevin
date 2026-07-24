package com.kamedevin.budget.widget

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import com.kamedevin.budget.core.model.ThemeMode
import com.kamedevin.budget.core.model.WidgetStyle
import com.kamedevin.budget.widget.di.WidgetEntryPoint
import dagger.hilt.android.EntryPointAccessors
import java.time.YearMonth
import kotlinx.coroutines.flow.first

class BudgetGlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java,
        )
        val progress = entryPoint.getBudgetProgressUseCase()(YearMonth.now()).first()
        val style = entryPoint.widgetPreferenceRepository().observeWidgetStyle().first()
        val themeMode = entryPoint.themePreferenceRepository().observeThemeMode().first()
        val isDarkTheme = when (themeMode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> isSystemInDarkTheme(context)
        }

        val density = context.resources.displayMetrics.density
        val chartBitmap: Bitmap? = when (style) {
            WidgetStyle.RINGS -> {
                val sizePx = (120 * density).toInt()
                WidgetChartRenderer.renderRings(progress, sizePx)
            }
            WidgetStyle.BLOB -> {
                val widthPx = (180 * density).toInt()
                val heightPx = (110 * density).toInt()
                WidgetChartRenderer.renderBlob(progress, widthPx, heightPx)
            }
            WidgetStyle.BARS -> null
        }

        provideContent {
            BudgetWidgetContent(
                progress = progress,
                style = style,
                chartBitmap = chartBitmap,
                isDarkTheme = isDarkTheme,
            )
        }
    }

    private fun isSystemInDarkTheme(context: Context): Boolean {
        val nightModeFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }
}
