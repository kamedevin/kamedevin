package com.kamedevin.budget.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
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

        provideContent {
            BudgetWidgetContent(progress = progress)
        }
    }
}
