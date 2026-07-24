package com.kamedevin.budget.core.data.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

/** Shared preferences DataStore for small app-wide settings (theme, widget style, ...). */
internal val Context.appDataStore by preferencesDataStore(name = "app_prefs")
