package com.kamedevin.budget.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kamedevin.budget.core.model.ThemeMode
import com.kamedevin.budget.core.model.WidgetStyle
import com.kamedevin.budget.core.model.label
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToCategories: () -> Unit,
    onNavigateToAccounts: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val widgetStyle by viewModel.widgetStyle.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            ListItem(
                headlineContent = { Text("Categories") },
                leadingContent = { Icon(Icons.Default.Category, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().clickable(onClick = onNavigateToCategories),
            )
            ListItem(
                headlineContent = { Text("Accounts") },
                leadingContent = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().clickable(onClick = onNavigateToAccounts),
            )

            HorizontalDivider()

            Column(Modifier.padding(16.dp)) {
                Text("Appearance", style = MaterialTheme.typography.titleMedium)
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    ThemeMode.entries.forEachIndexed { index, mode ->
                        SegmentedButton(
                            selected = themeMode == mode,
                            onClick = { viewModel.setThemeMode(mode) },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = ThemeMode.entries.size),
                        ) {
                            Text(mode.label())
                        }
                    }
                }
            }

            HorizontalDivider()

            Column(Modifier.padding(16.dp)) {
                Text("Widget style", style = MaterialTheme.typography.titleMedium)
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    WidgetStyle.entries.forEachIndexed { index, style ->
                        SegmentedButton(
                            selected = widgetStyle == style,
                            onClick = { viewModel.setWidgetStyle(style) },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = WidgetStyle.entries.size),
                        ) {
                            Text(style.label())
                        }
                    }
                }
            }

            HorizontalDivider()

            Column(Modifier.padding(16.dp)) {
                Text("Backup", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = uiState.lastBackupTime?.let { "Last backup: ${formatInstant(it)}" }
                        ?: "No backup yet",
                    style = MaterialTheme.typography.bodyMedium,
                )
                uiState.message?.let { message ->
                    Text(message, style = MaterialTheme.typography.bodySmall)
                }

                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (uiState.isSignedIn) {
                        Button(onClick = viewModel::backupNow, enabled = !uiState.isWorking) {
                            Text("Back up now")
                        }
                        OutlinedButton(onClick = viewModel::restoreLatest, enabled = !uiState.isWorking) {
                            Text("Restore latest backup")
                        }
                        OutlinedButton(onClick = viewModel::signOut, enabled = !uiState.isWorking) {
                            Text("Sign out of Google Drive")
                        }
                    } else {
                        Button(
                            onClick = {
                                (context as? android.app.Activity)?.let(viewModel::signIn)
                            },
                            enabled = !uiState.isWorking,
                        ) {
                            Text("Sign in with Google to back up")
                        }
                    }
                }
            }
        }
    }
}

private fun formatInstant(instant: java.time.Instant): String =
    DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a").withZone(ZoneId.systemDefault()).format(instant)
