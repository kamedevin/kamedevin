package com.kamedevin.budget.core.model

/**
 * A transaction category. [bucket] is null only for the built-in Income category, which is
 * excluded from 50/30/20 spend sums since it represents money coming in, not a spending bucket.
 */
data class Category(
    val id: Long = 0,
    val name: String,
    val bucket: Bucket?,
    val colorHex: String,
    val isDefault: Boolean = false,
    val sortOrder: Int = 0,
)
