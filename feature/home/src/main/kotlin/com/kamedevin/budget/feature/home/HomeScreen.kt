package com.kamedevin.budget.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kamedevin.budget.core.designsystem.theme.BucketColors
import com.kamedevin.budget.core.model.BucketProgress
import com.kamedevin.budget.core.model.formatCentsAsCurrency
import com.kamedevin.budget.core.model.label

@Composable
fun HomeScreen(
    onAddTransaction: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTransaction) {
                Icon(Icons.Default.Add, contentDescription = "Add transaction")
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { Text("This Month", style = MaterialTheme.typography.titleLarge) }

            uiState.progress?.buckets?.forEach { bucketProgress ->
                item(key = bucketProgress.bucket.name) {
                    BucketProgressCard(bucketProgress)
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                Text("Accounts", style = MaterialTheme.typography.titleLarge)
            }

            items(uiState.accountBalances, key = { it.account.id }) { balance ->
                ListItem(
                    headlineContent = { Text(balance.account.name) },
                    trailingContent = { Text(formatCentsAsCurrency(balance.currentBalanceCents)) },
                )
            }
        }
    }
}

@Composable
private fun BucketProgressCard(progress: BucketProgress) {
    Card {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(progress.bucket.label(), style = MaterialTheme.typography.titleMedium)
                Text("${formatCentsAsCurrency(progress.spentCents)} / ${formatCentsAsCurrency(progress.targetCents)}")
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress.fraction.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = BucketColors.colorFor(progress.bucket),
            )
        }
    }
}
