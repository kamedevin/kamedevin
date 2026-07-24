package com.kamedevin.budget.core.model

data class BudgetSettings(
    val needsPercent: Float = 50f,
    val wantsPercent: Float = 30f,
    val savingsPercent: Float = 20f,
) {
    fun percentFor(bucket: Bucket): Float = when (bucket) {
        Bucket.NEEDS -> needsPercent
        Bucket.WANTS -> wantsPercent
        Bucket.SAVINGS -> savingsPercent
    }
}
