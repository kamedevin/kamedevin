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
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

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

    /**
     * Each bucket gets its own irregular, organic outline (not a uniform capsule) — a fixed seed
     * per bucket index keeps each shape stable across widget refreshes rather than jittering
     * randomly every time the data changes, while still giving the three buckets visibly
     * different silhouettes from one another.
     */
    fun renderBlob(progress: BudgetProgress, widthPx: Int, heightPx: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val buckets = progress.buckets
        val cellWidth = widthPx / buckets.size.toFloat()
        val cellPadding = cellWidth * 0.14f
        val baseRadius = (cellWidth - cellPadding * 2f) / 2f
        val centerY = heightPx / 2f
        val verticalReach = baseRadius * 1.15f

        val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = TRACK_COLOR }
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        buckets.forEachIndexed { index, bucketProgress ->
            val centerX = cellWidth * index + cellWidth / 2f
            val blobPath = buildBlobPath(centerX, centerY, baseRadius, seed = BLOB_SEEDS[index % BLOB_SEEDS.size])

            canvas.drawPath(blobPath, trackPaint)

            val fraction = bucketProgress.fraction.coerceIn(0f, 1f)
            val top = centerY - verticalReach
            val bottom = centerY + verticalReach
            val fillTop = bottom - (bottom - top) * fraction
            val waveHeight = baseRadius * 0.15f
            val left = centerX - verticalReach
            val right = centerX + verticalReach

            val fillPath = Path().apply {
                moveTo(left, bottom)
                lineTo(left, fillTop + waveHeight)
                quadTo(
                    (left + right) / 2f, fillTop - waveHeight,
                    right, fillTop + waveHeight,
                )
                lineTo(right, bottom)
                close()
            }

            fillPaint.color = BucketColors.colorFor(bucketProgress.bucket).toArgb()
            canvas.save()
            canvas.clipPath(blobPath)
            canvas.drawPath(fillPath, fillPaint)
            canvas.restore()
        }

        return bitmap
    }

    /**
     * Builds a smooth, irregular closed shape by jittering points around a circle and connecting
     * them with quadratic curves through each segment's midpoint — a standard technique for
     * organic "blob" outlines that stays smooth (no sharp corners) despite the randomization.
     */
    private fun buildBlobPath(centerX: Float, centerY: Float, baseRadius: Float, seed: Long): Path {
        val random = Random(seed)
        val pointCount = 9
        val angleStep = (2 * Math.PI / pointCount).toFloat()

        val points = (0 until pointCount).map { i ->
            val angle = i * angleStep
            val jitteredRadius = baseRadius * (0.72f + random.nextFloat() * 0.56f)
            Pair(
                centerX + jitteredRadius * cos(angle),
                centerY + jitteredRadius * sin(angle),
            )
        }

        val path = Path()
        val startMidpoint = midpoint(points.first(), points.last())
        path.moveTo(startMidpoint.first, startMidpoint.second)
        for (i in points.indices) {
            val current = points[i]
            val next = points[(i + 1) % points.size]
            val mid = midpoint(current, next)
            path.quadTo(current.first, current.second, mid.first, mid.second)
        }
        path.close()
        return path
    }

    private fun midpoint(a: Pair<Float, Float>, b: Pair<Float, Float>) =
        Pair((a.first + b.first) / 2f, (a.second + b.second) / 2f)

    private val BLOB_SEEDS = longArrayOf(1927L, 4051L, 7793L)
}
