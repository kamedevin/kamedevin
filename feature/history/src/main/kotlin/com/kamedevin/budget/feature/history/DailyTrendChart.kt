package com.kamedevin.budget.feature.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.kamedevin.budget.core.domain.repository.DailyTotal

/** A minimal hand-rolled bar chart — avoids pulling in a third-party charting dependency for v1. */
@Composable
fun DailyTrendChart(dailyTotals: List<DailyTotal>, modifier: Modifier = Modifier) {
    val expenseColor = MaterialTheme.colorScheme.error
    val maxValue = (dailyTotals.maxOfOrNull { it.expenseCents } ?: 0L).coerceAtLeast(1L)

    Canvas(modifier = modifier.fillMaxWidth().height(160.dp)) {
        if (dailyTotals.isEmpty()) return@Canvas
        val barWidth = size.width / dailyTotals.size
        dailyTotals.forEachIndexed { index, day ->
            val barHeight = (day.expenseCents.toFloat() / maxValue) * size.height
            drawRect(
                color = expenseColor,
                topLeft = Offset(x = index * barWidth + barWidth * 0.15f, y = size.height - barHeight),
                size = Size(width = barWidth * 0.7f, height = barHeight),
            )
        }
    }
}
