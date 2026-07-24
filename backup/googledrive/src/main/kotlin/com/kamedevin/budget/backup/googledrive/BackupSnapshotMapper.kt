package com.kamedevin.budget.backup.googledrive

import com.kamedevin.budget.core.database.entity.AccountEntity
import com.kamedevin.budget.core.database.entity.BudgetSettingsEntity
import com.kamedevin.budget.core.database.entity.CategoryEntity
import com.kamedevin.budget.core.database.entity.TransactionEntity
import com.kamedevin.budget.core.model.AccountType
import com.kamedevin.budget.core.model.Bucket
import com.kamedevin.budget.core.model.TransactionSource
import com.kamedevin.budget.core.model.TransactionType
import java.time.Instant

fun CategoryEntity.toSnapshot() = CategorySnapshot(
    id = id,
    name = name,
    bucket = bucket?.name,
    colorHex = colorHex,
    isDefault = isDefault,
    sortOrder = sortOrder,
)

fun CategorySnapshot.toEntity() = CategoryEntity(
    id = id,
    name = name,
    bucket = bucket?.let { Bucket.valueOf(it) },
    colorHex = colorHex,
    isDefault = isDefault,
    sortOrder = sortOrder,
)

fun AccountEntity.toSnapshot() = AccountSnapshot(
    id = id,
    name = name,
    type = type.name,
    startingBalanceCents = startingBalanceCents,
    colorHex = colorHex,
    isArchived = isArchived,
    sortOrder = sortOrder,
)

fun AccountSnapshot.toEntity() = AccountEntity(
    id = id,
    name = name,
    type = AccountType.valueOf(type),
    startingBalanceCents = startingBalanceCents,
    colorHex = colorHex,
    isArchived = isArchived,
    sortOrder = sortOrder,
)

fun TransactionEntity.toSnapshot() = TransactionSnapshot(
    id = id,
    amountCents = amountCents,
    type = type.name,
    categoryId = categoryId,
    accountId = accountId,
    dateEpochMillis = date.toEpochMilli(),
    note = note,
    source = source.name,
    externalId = externalId,
    createdAtEpochMillis = createdAt.toEpochMilli(),
    updatedAtEpochMillis = updatedAt.toEpochMilli(),
)

fun TransactionSnapshot.toEntity() = TransactionEntity(
    id = id,
    amountCents = amountCents,
    type = TransactionType.valueOf(type),
    categoryId = categoryId,
    accountId = accountId,
    date = Instant.ofEpochMilli(dateEpochMillis),
    note = note,
    source = TransactionSource.valueOf(source),
    externalId = externalId,
    createdAt = Instant.ofEpochMilli(createdAtEpochMillis),
    updatedAt = Instant.ofEpochMilli(updatedAtEpochMillis),
)

fun BudgetSettingsEntity.toSnapshot() = BudgetSettingsSnapshot(
    needsPercent = needsPercent,
    wantsPercent = wantsPercent,
    savingsPercent = savingsPercent,
)

fun BudgetSettingsSnapshot.toEntity() = BudgetSettingsEntity(
    needsPercent = needsPercent,
    wantsPercent = wantsPercent,
    savingsPercent = savingsPercent,
)
