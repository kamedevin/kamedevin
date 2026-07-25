package com.kamedevin.budget.feature.lock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LockScreen(viewModel: AppLockViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Enter PIN", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(PIN_LENGTH) { index ->
                    PinDot(filled = index < uiState.enteredPin.length)
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = uiState.error ?: " ",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(24.dp))

            NumberPad(
                onDigit = viewModel::onDigitEntered,
                onBackspace = viewModel::onBackspace,
            )
        }
    }
}

@Composable
private fun PinDot(filled: Boolean) {
    Surface(
        modifier = Modifier.size(16.dp),
        shape = CircleShape,
        color = if (filled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
    ) {}
}

@Composable
private fun NumberPad(onDigit: (Char) -> Unit, onBackspace: () -> Unit) {
    val rows = listOf(
        listOf('1', '2', '3'),
        listOf('4', '5', '6'),
        listOf('7', '8', '9'),
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                row.forEach { digit ->
                    NumberPadKey(label = digit.toString(), onClick = { onDigit(digit) })
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Spacer(Modifier.size(64.dp))
            NumberPadKey(label = "0", onClick = { onDigit('0') })
            Box(modifier = Modifier.size(64.dp), contentAlignment = Alignment.Center) {
                IconButton(onClick = onBackspace) {
                    Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Delete digit")
                }
            }
        }
    }
}

@Composable
private fun NumberPadKey(label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.size(64.dp),
        shape = CircleShape,
    ) {
        Text(label, style = MaterialTheme.typography.headlineSmall)
    }
}
