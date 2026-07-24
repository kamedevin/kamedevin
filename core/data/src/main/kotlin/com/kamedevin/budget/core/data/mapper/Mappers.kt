package com.kamedevin.budget.core.data.mapper

import com.kamedevin.budget.core.database.entity.AccountEntity
import com.kamedevin.budget.core.database.entity.CategoryEntity
import com.kamedevin.budget.core.database.entity.TransactionEntity
import com.kamedevin.budget.core.model.Account
import com.kamedevin.budget.core.model.Category
import com.kamedevin.budget.core.model.Transaction

fun CategoryEntity.toDomain() = Category(
    id = id,
    name = name,
    bucket = bucket,
    colorHex = colorHex,
    isDefault = isDefault,
    sortOrder = sortOrder,
)

fun Category.toEntity() = CategoryEntity(
    id = id,
    name = name,
    bucket = bucket,
    colorHex = colorHex,
    isDefault = isDefault,
    sortOrder = sortOrder,
)

fun AccountEntity.toDomain() = Account(
    id = id,
    name = name,
    type = type,
    startingBalanceCents = startingBalanceCents,
    colorHex = colorHex,
    isArchived = isArchived,
    sortOrder = sortOrder,
)

fun Account.toEntity() = AccountEntity(
    id = id,
    name = name,
    type = type,
    startingBalanceCents = startingBalanceCents,
    colorHex = colorHex,
    isArchived = isArchived,
    sortOrder = sortOrder,
)

fun TransactionEntity.toDomain() = Transaction(
    id = id,
    amountCents = amountCents,
    type = type,
    categoryId = categoryId,
    accountId = accountId,
    date = date,
    note = note,
    source = source,
    externalId = externalId,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Transaction.toEntity() = TransactionEntity(
    id = id,
    amountCents = amountCents,
    type = type,
    categoryId = categoryId,
    accountId = accountId,
    date = date,
    note = note,
    source = source,
    externalId = externalId,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
