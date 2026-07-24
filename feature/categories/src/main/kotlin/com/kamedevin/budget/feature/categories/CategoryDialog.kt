package com.kamedevin.budget.feature.categories

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
import com.kamedevin.budget.core.model.Bucket
import com.kamedevin.budget.core.model.label

private val BUCKET_OPTIONS: List<Bucket?> = listOf(null) + Bucket.entries

/** Shared by both the "add category" and "rename/re-bucket category" flows. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDialog(
    title: String,
    confirmLabel: String,
    initialName: String = "",
    initialBucket: Bucket? = Bucket.NEEDS,
    onDismiss: () -> Unit,
    onConfirm: (name: String, bucket: Bucket?) -> Unit,
) {
    var name by remember { mutableStateOf(initialName) }
    var bucket by remember { mutableStateOf(initialBucket) }
    var menuExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
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
                    expanded = menuExpanded,
                    onExpandedChange = { menuExpanded = it },
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = bucket?.label() ?: "Income",
                        onValueChange = {},
                        label = { Text("Bucket") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                    )
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        BUCKET_OPTIONS.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option?.label() ?: "Income") },
                                onClick = {
                                    bucket = option
                                    menuExpanded = false
                                },
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onConfirm(name.trim(), bucket) }) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
