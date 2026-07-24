package com.kamedevin.budget.core.model

import java.time.YearMonth

data class BucketProgress(
    val bucket: Bucket,
    val spentCents: Long,
    val targetCents: Long,
) {
    /** Fraction of target spent. Not capped at 1.0 so overspending is still representable. */
    val fraction: Float
        get() = if (targetCents <= 0L) 0f else (spentCents.toFloat() / targetCents.toFloat()).coerceAtLeast(0f)
}

data class BudgetProgress(
    val period: YearMonth,
    val incomeCents: Long,
    val buckets: List<BucketProgress>,
)

/**
 * Splits [incomeCents] across buckets by [settings]'s percentages using integer floors plus a
 * largest-remainder pass for leftover cents, so the three targets always sum to exactly
 * [incomeCents]. A naive `income * percent / 100f` per bucket can drop or gain a cent to
 * float rounding, which would make the three progress bars visibly not add up to the total.
 */
fun allocateBucketTargets(incomeCents: Long, settings: BudgetSettings): Map<Bucket, Long> {
    if (incomeCents <= 0L) return Bucket.entries.associateWith { 0L }

    val rawShares = Bucket.entries.map { bucket ->
        bucket to incomeCents * settings.percentFor(bucket).toDouble() / 100.0
    }
    val floors = rawShares.associate { (bucket, raw) -> bucket to raw.toLong() }
    var remainder = incomeCents - floors.values.sum()

    val byRemainderDesc = rawShares
        .sortedByDescending { (bucket, raw) -> raw - floors.getValue(bucket) }
        .map { it.first }

    val result = floors.toMutableMap()
    for (bucket in byRemainderDesc) {
        if (remainder <= 0L) break
        result[bucket] = result.getValue(bucket) + 1
        remainder--
    }
    return result
}
