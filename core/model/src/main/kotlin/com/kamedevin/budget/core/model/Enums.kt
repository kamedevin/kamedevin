package com.kamedevin.budget.core.model

/** The three 50/30/20 spending buckets. */
enum class Bucket {
    NEEDS,
    WANTS,
    SAVINGS,
}

enum class TransactionType {
    INCOME,
    EXPENSE,
}

/**
 * Where a transaction came from. Kept on every transaction (rather than a separate
 * "manual add" vs "synced add" API) so a future bank-sync integration can write through
 * the same [com.kamedevin.budget.core.domain.repository.TransactionRepository.add] call.
 */
enum class TransactionSource {
    MANUAL,
    SYNCED,
}

enum class AccountType {
    CHECKING,
    CREDIT_CARD,
    CASH,
    SAVINGS,
    OTHER,
}
