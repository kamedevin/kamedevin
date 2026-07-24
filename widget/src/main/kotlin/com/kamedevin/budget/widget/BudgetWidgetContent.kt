package com.kamedevin.budget.widget

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.semantics.contentDescription
import androidx.glance.semantics.semantics
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.kamedevin.budget.core.designsystem.theme.BucketColors
import com.kamedevin.budget.core.model.BucketProgress
import com.kamedevin.budget.core.model.BudgetProgress
import com.kamedevin.budget.core.model.label

/**
 * Deliberately shows no raw dollar amounts — only progress fractions per the app's requirement
 * that the widget stay visual-only. Each bar's percentage is still exposed via [contentDescription]
 * so screen readers announce it even though it isn't drawn.
 */
@Composable
fun BudgetWidgetContent(progress: BudgetProgress) {
    val context = LocalContext.current

    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(ColorProvider(Color.White))
            .padding(12.dp),
    ) {
        progress.buckets.forEach { bucketProgress ->
            BucketRow(bucketProgress)
            Spacer(modifier = GlanceModifier.height(8.dp))
        }

        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .clickable(actionStartActivity(Intent(context, QuickAddActivity::class.java))),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "+ Add expense",
                style = TextStyle(fontWeight = FontWeight.Medium),
            )
        }
    }
}

@Composable
private fun BucketRow(bucketProgress: BucketProgress) {
    val percent = (bucketProgress.fraction * 100).toInt()

    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .semantics { contentDescription = "${bucketProgress.bucket.label()}: $percent% of target" },
    ) {
        Text(text = bucketProgress.bucket.label(), style = TextStyle(fontWeight = FontWeight.Medium))
        Spacer(modifier = GlanceModifier.height(4.dp))
        LinearProgressIndicator(
            progress = bucketProgress.fraction.coerceIn(0f, 1f),
            modifier = GlanceModifier.fillMaxWidth().height(8.dp),
            color = ColorProvider(BucketColors.colorFor(bucketProgress.bucket)),
            backgroundColor = ColorProvider(Color.LightGray),
        )
    }
}
