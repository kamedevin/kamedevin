package com.kamedevin.budget.feature.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kamedevin.budget.core.model.formatCentsAsCurrency
import com.kamedevin.budget.core.model.label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: HistoryViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("History") }) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Daily spending this month", style = MaterialTheme.typography.titleMedium)
                    DailyTrendChart(dailyTotals = uiState.dailyTotals, modifier = Modifier.fillMaxWidth())
                }
            }

            uiState.periodComparison?.let { comparison ->
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("This month vs last month", style = MaterialTheme.typography.titleMedium)
                        Card {
                            Column(Modifier.padding(16.dp)) {
                                comparison.current.buckets.forEach { current ->
                                    val previous = comparison.previous.buckets.first { it.bucket == current.bucket }
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Text(current.bucket.label())
                                        Text(
                                            "${formatCentsAsCurrency(current.spentCents)} " +
                                                "(prev ${formatCentsAsCurrency(previous.spentCents)})",
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item { Text("By account", style = MaterialTheme.typography.titleMedium) }

            items(uiState.accountSpend, key = { it.account.id }) { spend ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(spend.account.name)
                    Text(formatCentsAsCurrency(spend.spentCents))
                }
            }
        }
    }
}
