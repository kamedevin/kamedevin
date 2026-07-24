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

fun ThemeMode.label(): String = when (this) {
    ThemeMode.SYSTEM -> "System default"
    ThemeMode.LIGHT -> "Light"
    ThemeMode.DARK -> "Dark"
}

fun WidgetStyle.label(): String = when (this) {
    WidgetStyle.BARS -> "Bars"
    WidgetStyle.RINGS -> "Rings"
    WidgetStyle.BLOB -> "Blob"
}
