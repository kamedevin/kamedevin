package com.kamedevin.budget.core.domain.repository

/**
 * Lets [com.kamedevin.budget.core.data.repository.TransactionRepositoryImpl] tell the home-screen
 * widget to refresh after a mutation, without :core:data depending on :widget directly (:widget
 * already depends on :feature:entry for the shared quick-add form, so the reverse dependency
 * would be circular). The real implementation lives in :widget and is Hilt-bound there.
 */
interface WidgetRefreshNotifier {
    suspend fun notifyTransactionsChanged()
}
