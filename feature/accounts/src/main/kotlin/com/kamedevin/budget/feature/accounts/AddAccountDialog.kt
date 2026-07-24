package com.kamedevin.budget.feature.accounts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kamedevin.budget.core.model.AccountType
import com.kamedevin.budget.core.model.label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, type: AccountType, startingBalanceCents: Long) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(AccountType.CHECKING) }
    var typeMenuExpanded by remember { mutableStateOf(false) }
    var startingBalance by remember { mutableStateOf("0.00") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add account") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    singleLine = true,
                )

                ExposedDropdownMenuBox(
                    expanded = typeMenuExpanded,
                    onExpandedChange = { typeMenuExpanded = it },
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = type.label(),
                        onValueChange = {},
                        label = { Text("Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeMenuExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth().padding(bottom = 8.dp),
                    )
                    DropdownMenu(
                        expanded = typeMenuExpanded,
                        onDismissRequest = { typeMenuExpanded = false },
                    ) {
                        AccountType.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.label()) },
                                onClick = {
                                    type = option
                                    typeMenuExpanded = false
                                },
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = startingBalance,
                    onValueChange = { startingBalance = it },
                    label = { Text("Starting balance ($)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val cents = ((startingBalance.toDoubleOrNull() ?: 0.0) * 100).toLong()
                    if (name.isNotBlank()) onConfirm(name.trim(), type, cents)
                },
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
