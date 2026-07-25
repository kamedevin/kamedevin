package com.kamedevin.budget.core.domain.repository

import kotlinx.coroutines.flow.Flow

/** A 4-digit PIN is stored as a salted hash — [verifyPin] compares hashes, never the raw PIN. */
interface AppLockRepository {
    fun observeIsLockEnabled(): Flow<Boolean>
    suspend fun setPin(pin: String)
    suspend fun verifyPin(pin: String): Boolean
    suspend fun disableLock()
}
