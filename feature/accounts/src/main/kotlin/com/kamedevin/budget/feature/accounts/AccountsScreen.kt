package com.kamedevin.budget.feature.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kamedevin.budget.core.model.formatCentsAsCurrency
import com.kamedevin.budget.core.model.label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    onBack: () -> Unit,
    viewModel: AccountsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Accounts") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add account")
            }
        },
    ) { padding ->
        if (uiState.accounts.isEmpty() && !uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No accounts yet. Tap + to add one.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(uiState.accounts, key = { it.account.id }) { balance ->
                    ListItem(
                        headlineContent = { Text(balance.account.name) },
                        supportingContent = { Text(balance.account.type.label()) },
                        trailingContent = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(formatCentsAsCurrency(balance.currentBalanceCents))
                                IconButton(onClick = { viewModel.archiveAccount(balance.account) }) {
                                    Icon(
                                        Icons.Default.Archive,
                                        contentDescription = "Archive ${balance.account.name}",
                                    )
                                }
                            }
                        },
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddAccountDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, type, startingBalance ->
                viewModel.addAccount(name, type, startingBalance)
                showAddDialog = false
            },
        )
    }
}
