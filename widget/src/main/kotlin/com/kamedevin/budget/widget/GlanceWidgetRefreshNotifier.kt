package com.kamedevin.budget.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.kamedevin.budget.core.domain.repository.WidgetRefreshNotifier
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GlanceWidgetRefreshNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) : WidgetRefreshNotifier {
    override suspend fun notifyTransactionsChanged() {
        BudgetGlanceWidget().updateAll(context)
    }
}
