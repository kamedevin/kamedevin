package com.kamedevin.budget.core.domain.repository

/**
 * Lets callers (transaction mutations, widget style/preference changes) tell the home-screen
 * widget to refresh, without depending on :widget directly — :widget already depends on
 * :feature:entry for the shared quick-add form, so a reverse dependency from a module like
 * :core:data would be circular. The real implementation lives in :widget and is Hilt-bound there.
 */
interface WidgetRefreshNotifier {
    suspend fun refreshWidget()
}
