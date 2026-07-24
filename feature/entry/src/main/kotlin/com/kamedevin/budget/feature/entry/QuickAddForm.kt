package com.kamedevin.budget.feature.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kamedevin.budget.core.model.TransactionType

/**
 * The minimal "log an expense" form hosted by the widget's quick-add dialog Activity. Reuses
 * [EntryViewModel] so the widget can never validate transactions differently than the full
 * [EntryScreen] does.
 */
@Composable
fun QuickAddForm(
    onDismiss: () -> Unit,
    onSaved: () -> Unit,
    viewModel: EntryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onTypeChange(TransactionType.EXPENSE)
    }

    LaunchedEffect(uiState.savedSuccessfully) {
        if (uiState.savedSuccessfully) {
            viewModel.consumeSavedEvent()
            onSaved()
        }
    }

    val expenseCategories = remember(uiState.categories) {
        uiState.categories.filter { it.bucket != null }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Add expense", style = MaterialTheme.typography.titleMedium)

        AmountField(value = uiState.amountText, onValueChange = viewModel::onAmountChange)

        CategoryChipGrid(
            categories = expenseCategories,
            selectedCategoryId = uiState.selectedCategoryId,
            onCategorySelected = viewModel::onCategorySelected,
        )

        AccountDropdown(
            accounts = uiState.accounts,
            selectedAccountId = uiState.selectedAccountId,
            onAccountSelected = viewModel::onAccountSelected,
        )

        uiState.error?.let { error ->
            Text(error, color = MaterialTheme.colorScheme.error)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = onDismiss) { Text("Cancel") }
            Button(onClick = viewModel::save, enabled = !uiState.isSaving) {
                Text(if (uiState.isSaving) "Saving..." else "Save")
            }
        }
    }
}
