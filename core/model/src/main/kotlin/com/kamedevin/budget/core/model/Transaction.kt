package com.kamedevin.budget.core.model

import java.time.Instant

/** [amountCents] is always positive; [type] determines the sign's meaning. */
data class Transaction(
    val id: Long = 0,
    val amountCents: Long,
    val type: TransactionType,
    val categoryId: Long,
    val accountId: Long,
    val date: Instant,
    val note: String? = null,
    val source: TransactionSource = TransactionSource.MANUAL,
    val externalId: String? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
)
