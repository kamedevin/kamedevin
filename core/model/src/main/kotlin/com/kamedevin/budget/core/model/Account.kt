package com.kamedevin.budget.core.model

data class Account(
    val id: Long = 0,
    val name: String,
    val type: AccountType,
    val startingBalanceCents: Long,
    val colorHex: String,
    val isArchived: Boolean = false,
    val sortOrder: Int = 0,
)

data class AccountBalance(
    val account: Account,
    val currentBalanceCents: Long,
)
