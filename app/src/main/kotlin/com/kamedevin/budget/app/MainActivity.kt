package com.kamedevin.budget.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kamedevin.budget.app.navigation.BudgetNavHost
import com.kamedevin.budget.core.designsystem.theme.KameBudgetTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KameBudgetTheme {
                BudgetNavHost()
            }
        }
    }
}
