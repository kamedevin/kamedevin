package com.kamedevin.budget.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

class BudgetProgressTest {

    @Test
    fun allocateBucketTargets_sumsExactlyToIncome_forDefaultSplit() {
        val income = 100_00L // $100.00 in cents
        val targets = allocateBucketTargets(income, BudgetSettings())

        assertEquals(income, targets.values.sum())
    }

    @Test
    fun allocateBucketTargets_sumsExactlyToIncome_forAwkwardCentsAmount() {
        // $10.01 split 50/30/20 does not divide evenly into cents; the remainder must be
        // distributed rather than dropped or double counted.
        val income = 1001L
        val targets = allocateBucketTargets(income, BudgetSettings())

        assertEquals(income, targets.values.sum())
    }

    @Test
    fun allocateBucketTargets_matchesExpectedSplit_forRoundNumbers() {
        val income = 1_000_00L // $1000.00
        val targets = allocateBucketTargets(income, BudgetSettings())

        assertEquals(500_00L, targets.getValue(Bucket.NEEDS))
        assertEquals(300_00L, targets.getValue(Bucket.WANTS))
        assertEquals(200_00L, targets.getValue(Bucket.SAVINGS))
    }

    @Test
    fun allocateBucketTargets_returnsZeroForEveryBucket_whenIncomeIsZero() {
        val targets = allocateBucketTargets(0L, BudgetSettings())

        Bucket.entries.forEach { bucket -> assertEquals(0L, targets.getValue(bucket)) }
    }

    @Test
    fun bucketProgress_fraction_isZero_whenTargetIsZero() {
        val progress = BucketProgress(bucket = Bucket.NEEDS, spentCents = 500, targetCents = 0)

        assertEquals(0f, progress.fraction)
    }

    @Test
    fun bucketProgress_fraction_canExceedOne_whenOverspent() {
        val progress = BucketProgress(bucket = Bucket.WANTS, spentCents = 150, targetCents = 100)

        assertEquals(1.5f, progress.fraction)
    }
}
