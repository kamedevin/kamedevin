package com.kamedevin.budget.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import com.kamedevin.budget.core.model.Bucket

object BucketColors {
    fun colorFor(bucket: Bucket): Color = when (bucket) {
        Bucket.NEEDS -> NeedsColor
        Bucket.WANTS -> WantsColor
        Bucket.SAVINGS -> SavingsColor
    }

    fun hexFor(bucket: Bucket): String = when (bucket) {
        Bucket.NEEDS -> "#5C6BC0"
        Bucket.WANTS -> "#FFA726"
        Bucket.SAVINGS -> "#26A69A"
    }
}
