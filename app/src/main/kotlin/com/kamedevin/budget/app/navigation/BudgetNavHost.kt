package com.kamedevin.budget.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kamedevin.budget.feature.accounts.AccountsScreen
import com.kamedevin.budget.feature.categories.CategoriesScreen
import com.kamedevin.budget.feature.entry.EntryScreen
import com.kamedevin.budget.feature.history.HistoryScreen
import com.kamedevin.budget.feature.home.HomeScreen
import com.kamedevin.budget.feature.settings.SettingsScreen

private data class TopLevelDestination(val route: String, val label: String, val icon: ImageVector)

private val topLevelDestinations = listOf(
    TopLevelDestination("home", "Home", Icons.Default.Home),
    TopLevelDestination("history", "History", Icons.Filled.ShowChart),
    TopLevelDestination("settings", "Settings", Icons.Default.Settings),
)

@Composable
fun BudgetNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (topLevelDestinations.any { it.route == currentRoute }) {
                NavigationBar {
                    topLevelDestinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destination.icon, contentDescription = destination.label) },
                            label = { Text(destination.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding),
        ) {
            composable("home") {
                HomeScreen(onAddTransaction = { navController.navigate("entry") })
            }
            composable("history") {
                HistoryScreen()
            }
            composable("settings") {
                SettingsScreen(
                    onNavigateToCategories = { navController.navigate("categories") },
                    onNavigateToAccounts = { navController.navigate("accounts") },
                )
            }
            composable("entry") {
                EntryScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() },
                )
            }
            composable("categories") {
                CategoriesScreen(onBack = { navController.popBackStack() })
            }
            composable("accounts") {
                AccountsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
