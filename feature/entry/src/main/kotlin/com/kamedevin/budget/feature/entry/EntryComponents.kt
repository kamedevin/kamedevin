package com.kamedevin.budget.feature.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kamedevin.budget.core.model.Account
import com.kamedevin.budget.core.model.Category
import com.kamedevin.budget.core.model.TransactionType

@Composable
fun AmountField(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = { input -> if (input.matches(Regex("^\\d*\\.?\\d{0,2}$"))) onValueChange(input) },
        label = { Text("Amount") },
        prefix = { Text("$") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionTypeToggle(type: TransactionType, onTypeChange: (TransactionType) -> Unit, modifier: Modifier = Modifier) {
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        TransactionType.entries.forEachIndexed { index, option ->
            SegmentedButton(
                selected = type == option,
                onClick = { onTypeChange(option) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = TransactionType.entries.size),
            ) {
                Text(if (option == TransactionType.EXPENSE) "Expense" else "Income")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryChipGrid(
    categories: List<Category>,
    selectedCategoryId: Long?,
    onCategorySelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        categories.forEach { category ->
            FilterChip(
                selected = category.id == selectedCategoryId,
                onClick = { onCategorySelected(category.id) },
                label = { Text(category.name) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDropdown(
    accounts: List<Account>,
    selectedAccountId: Long?,
    onAccountSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = accounts.firstOrNull { it.id == selectedAccountId }?.name ?: "Select account"

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier) {
        OutlinedTextField(
            readOnly = true,
            value = selectedName,
            onValueChange = {},
            label = { Text("Account") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            accounts.forEach { account ->
                DropdownMenuItem(
                    text = { Text(account.name) },
                    onClick = {
                        onAccountSelected(account.id)
                        expanded = false
                    },
                )
            }
        }
    }
}
