package com.kamedevin.budget.core.model

// Small shared display-label helpers so every feature module formats these enums the same way.

fun AccountType.label(): String = when (this) {
    AccountType.CHECKING -> "Checking"
    AccountType.CREDIT_CARD -> "Credit Card"
    AccountType.CASH -> "Cash"
    AccountType.SAVINGS -> "Savings"
    AccountType.OTHER -> "Other"
}

fun Bucket.label(): String = when (this) {
    Bucket.NEEDS -> "Needs"
    Bucket.WANTS -> "Wants"
    Bucket.SAVINGS -> "Savings"
}
