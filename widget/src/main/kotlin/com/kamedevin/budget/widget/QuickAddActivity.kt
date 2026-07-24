package com.kamedevin.budget.widget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kamedevin.budget.core.designsystem.theme.KameBudgetTheme
import com.kamedevin.budget.feature.entry.QuickAddForm
import dagger.hilt.android.AndroidEntryPoint

/**
 * A lightweight dialog-themed Activity launched from the widget's "+ Add expense" tap. Hosts
 * [QuickAddForm], which reuses the same EntryViewModel/AddTransactionUseCase as the full app's
 * entry screen so the widget can never validate transactions differently. By the time `onSaved`
 * fires, TransactionRepositoryImpl has already suspended through WidgetRefreshNotifier, so the
 * widget is already refreshed before this Activity closes.
 */
@AndroidEntryPoint
class QuickAddActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KameBudgetTheme {
                QuickAddForm(
                    onDismiss = { finish() },
                    onSaved = { finish() },
                )
            }
        }
    }
}
