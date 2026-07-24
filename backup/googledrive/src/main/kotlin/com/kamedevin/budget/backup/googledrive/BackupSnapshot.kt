package com.kamedevin.budget.backup.googledrive

import kotlinx.serialization.Serializable

/**
 * Deliberately a separate serializable shape from the Room entities (rather than annotating
 * those directly) so the backup format can stay stable even as the DB schema evolves —
 * [SCHEMA_VERSION] is bumped only when this shape itself changes, independent of Room's own
 * schema version.
 */
@Serializable
data class BackupSnapshot(
    val schemaVersion: Int = SCHEMA_VERSION,
    val categories: List<CategorySnapshot>,
    val accounts: List<AccountSnapshot>,
    val transactions: List<TransactionSnapshot>,
    val budgetSettings: BudgetSettingsSnapshot,
) {
    companion object {
        const val SCHEMA_VERSION = 1
    }
}

@Serializable
data class CategorySnapshot(
    val id: Long,
    val name: String,
    val bucket: String?,
    val colorHex: String,
    val isDefault: Boolean,
    val sortOrder: Int,
)

@Serializable
data class AccountSnapshot(
    val id: Long,
    val name: String,
    val type: String,
    val startingBalanceCents: Long,
    val colorHex: String,
    val isArchived: Boolean,
    val sortOrder: Int,
)

@Serializable
data class TransactionSnapshot(
    val id: Long,
    val amountCents: Long,
    val type: String,
    val categoryId: Long,
    val accountId: Long,
    val dateEpochMillis: Long,
    val note: String?,
    val source: String,
    val externalId: String?,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)

@Serializable
data class BudgetSettingsSnapshot(
    val needsPercent: Float,
    val wantsPercent: Float,
    val savingsPercent: Float,
)
