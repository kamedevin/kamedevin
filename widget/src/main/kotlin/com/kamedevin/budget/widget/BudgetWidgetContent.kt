package com.kamedevin.budget.widget

import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
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
import androidx.glance.layout.size
import androidx.glance.semantics.contentDescription
import androidx.glance.semantics.semantics
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.kamedevin.budget.core.designsystem.theme.BucketColors
import com.kamedevin.budget.core.designsystem.theme.md_theme_dark_background
import com.kamedevin.budget.core.designsystem.theme.md_theme_dark_onBackground
import com.kamedevin.budget.core.designsystem.theme.md_theme_light_background
import com.kamedevin.budget.core.designsystem.theme.md_theme_light_onBackground
import com.kamedevin.budget.core.model.BucketProgress
import com.kamedevin.budget.core.model.BudgetProgress
import com.kamedevin.budget.core.model.WidgetStyle
import com.kamedevin.budget.core.model.label

/**
 * Deliberately shows no raw dollar amounts — only progress fractions per the app's requirement
 * that the widget stay visual-only. Each bar's percentage is still exposed via [contentDescription]
 * so screen readers announce it even though it isn't drawn. For the RINGS/BLOB styles, the
 * per-bucket percentages are combined into one contentDescription on the chart image since a
 * bitmap can't carry per-region semantics.
 *
 * [isDarkTheme] mirrors whatever theme the app itself is resolved to (its System/Light/Dark
 * Settings choice, not just the raw system flag) using the same light/dark tokens as the app's
 * own [com.kamedevin.budget.core.designsystem.theme.KameBudgetTheme], so the widget doesn't
 * always render as if the app were in light mode.
 */
@Composable
fun BudgetWidgetContent(
    progress: BudgetProgress,
    style: WidgetStyle,
    chartBitmap: Bitmap?,
    isDarkTheme: Boolean,
) {
    val context = LocalContext.current
    val backgroundColor = if (isDarkTheme) md_theme_dark_background else md_theme_light_background
    val onBackgroundColor = if (isDarkTheme) md_theme_dark_onBackground else md_theme_light_onBackground
    val textStyle = TextStyle(fontWeight = FontWeight.Medium, color = ColorProvider(onBackgroundColor))

    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(ColorProvider(backgroundColor))
            .padding(12.dp),
    ) {
        if (style == WidgetStyle.BARS || chartBitmap == null) {
            progress.buckets.forEach { bucketProgress ->
                BucketRow(bucketProgress, textStyle)
                Spacer(modifier = GlanceModifier.height(8.dp))
            }
        } else {
            val chartDescription = progress.buckets.joinToString(separator = ", ") { bucketProgress ->
                "${bucketProgress.bucket.label()} ${(bucketProgress.fraction * 100).toInt()}% of target"
            }
            val imageModifier = if (style == WidgetStyle.RINGS) {
                GlanceModifier.size(120.dp)
            } else {
                GlanceModifier.fillMaxWidth().height(100.dp)
            }
            Image(
                provider = ImageProvider(chartBitmap),
                contentDescription = chartDescription,
                modifier = imageModifier,
            )
            Spacer(modifier = GlanceModifier.height(8.dp))
        }

        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .clickable(actionStartActivity(Intent(context, QuickAddActivity::class.java))),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "+ Add expense", style = textStyle)
        }
    }
}

@Composable
private fun BucketRow(bucketProgress: BucketProgress, textStyle: TextStyle) {
    val percent = (bucketProgress.fraction * 100).toInt()

    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .semantics { contentDescription = "${bucketProgress.bucket.label()}: $percent% of target" },
    ) {
        Text(text = bucketProgress.bucket.label(), style = textStyle)
        Spacer(modifier = GlanceModifier.height(4.dp))
        LinearProgressIndicator(
            progress = bucketProgress.fraction.coerceIn(0f, 1f),
            modifier = GlanceModifier.fillMaxWidth().height(8.dp),
            color = ColorProvider(BucketColors.colorFor(bucketProgress.bucket)),
            backgroundColor = ColorProvider(Color.LightGray),
        )
    }
}
