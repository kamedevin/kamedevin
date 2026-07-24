package com.kamedevin.budget.feature.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.DatePicker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kamedevin.budget.core.model.TransactionType
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: EntryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.savedSuccessfully) {
        if (uiState.savedSuccessfully) {
            viewModel.consumeSavedEvent()
            onSaved()
        }
    }

    val visibleCategories = uiState.categories.filter {
        if (uiState.type == TransactionType.INCOME) it.bucket == null else it.bucket != null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add transaction") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TransactionTypeToggle(type = uiState.type, onTypeChange = viewModel::onTypeChange)

            AmountField(value = uiState.amountText, onValueChange = viewModel::onAmountChange)

            CategoryChipGrid(
                categories = visibleCategories,
                selectedCategoryId = uiState.selectedCategoryId,
                onCategorySelected = viewModel::onCategorySelected,
            )

            AccountDropdown(
                accounts = uiState.accounts,
                selectedAccountId = uiState.selectedAccountId,
                onAccountSelected = viewModel::onAccountSelected,
            )

            TextButton(onClick = { showDatePicker = true }) {
                Text("Date: ${formatDate(uiState.date)}")
            }

            OutlinedTextField(
                value = uiState.note,
                onValueChange = viewModel::onNoteChange,
                label = { Text("Note (optional)") },
                modifier = Modifier.fillMaxWidth(),
            )

            uiState.error?.let { error ->
                Text(error, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = viewModel::save,
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (uiState.isSaving) "Saving..." else "Save")
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.date.toEpochMilli(),
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        viewModel.onDateChange(Instant.ofEpochMilli(millis))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun formatDate(instant: Instant): String =
    DateTimeFormatter.ofPattern("MMM d, yyyy").withZone(ZoneId.systemDefault()).format(instant)
