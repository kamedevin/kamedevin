package com.kamedevin.budget.widget

import android.content.Context
import android.graphics.Bitmap
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
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

        val density = context.resources.displayMetrics.density
        val chartBitmap: Bitmap? = when (style) {
            WidgetStyle.RINGS -> {
                val sizePx = (120 * density).toInt()
                WidgetChartRenderer.renderRings(progress, sizePx)
            }
            WidgetStyle.BLOB -> {
                val widthPx = (180 * density).toInt()
                val heightPx = (100 * density).toInt()
                WidgetChartRenderer.renderBlob(progress, widthPx, heightPx)
            }
            WidgetStyle.BARS -> null
        }

        provideContent {
            BudgetWidgetContent(progress = progress, style = style, chartBitmap = chartBitmap)
        }
    }
}
