package com.kamedevin.budget.core.model

import java.text.NumberFormat
import java.util.Locale

/** v1 is USD-only; a currency parameter can be threaded through here later if that changes. */
fun formatCentsAsCurrency(cents: Long, locale: Locale = Locale.US): String =
    NumberFormat.getCurrencyInstance(locale).format(cents / 100.0)
