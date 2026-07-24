package com.kamedevin.budget.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.compose.ui.graphics.toArgb
import com.kamedevin.budget.core.designsystem.theme.BucketColors
import com.kamedevin.budget.core.model.BudgetProgress

/**
 * Renders the RINGS/BLOB widget styles as plain android.graphics bitmaps. Glance/RemoteViews
 * can't draw arbitrary shapes inside its composable tree, so these are drawn off-screen with the
 * ordinary Canvas API — much older and better-established than Glance's own API surface — and
 * shown in the widget via Glance's Image composable instead.
 */
object WidgetChartRenderer {

    private val TRACK_COLOR = AndroidColor.argb(40, 128, 128, 128)

    fun renderRings(progress: BudgetProgress, sizePx: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val strokeWidth = sizePx * 0.09f
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
            strokeCap = Paint.Cap.ROUND
        }

        val ringGap = strokeWidth * 1.4f
        progress.buckets.forEachIndexed { index, bucketProgress ->
            val inset = strokeWidth / 2f + index * ringGap
            val rect = RectF(inset, inset, sizePx - inset, sizePx - inset)

            paint.color = TRACK_COLOR
            canvas.drawArc(rect, 0f, 360f, false, paint)

            paint.color = BucketColors.colorFor(bucketProgress.bucket).toArgb()
            val sweep = bucketProgress.fraction.coerceIn(0f, 1f) * 360f
            canvas.drawArc(rect, -90f, sweep, false, paint)
        }

        return bitmap
    }

    fun renderBlob(progress: BudgetProgress, widthPx: Int, heightPx: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val buckets = progress.buckets
        val gap = widthPx * 0.06f
        val capsuleWidth = (widthPx - gap * (buckets.size - 1)) / buckets.size
        val cornerRadius = capsuleWidth / 2f

        val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = TRACK_COLOR }
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        buckets.forEachIndexed { index, bucketProgress ->
            val left = index * (capsuleWidth + gap)
            val right = left + capsuleWidth
            val trackRect = RectF(left, 0f, right, heightPx.toFloat())
            canvas.drawRoundRect(trackRect, cornerRadius, cornerRadius, trackPaint)

            val fraction = bucketProgress.fraction.coerceIn(0f, 1f)
            val fillTop = heightPx * (1f - fraction)
            val waveHeight = capsuleWidth * 0.2f

            val fillPath = Path().apply {
                moveTo(left, heightPx.toFloat())
                lineTo(left, fillTop + waveHeight)
                quadTo(
                    (left + right) / 2f, fillTop - waveHeight,
                    right, fillTop + waveHeight,
                )
                lineTo(right, heightPx.toFloat())
                close()
            }

            val clipPath = Path().apply {
                addRoundRect(trackRect, cornerRadius, cornerRadius, Path.Direction.CW)
            }

            fillPaint.color = BucketColors.colorFor(bucketProgress.bucket).toArgb()
            canvas.save()
            canvas.clipPath(clipPath)
            canvas.drawPath(fillPath, fillPaint)
            canvas.restore()
        }

        return bitmap
    }
}
