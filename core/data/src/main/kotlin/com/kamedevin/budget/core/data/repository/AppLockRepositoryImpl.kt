package com.kamedevin.budget.core.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kamedevin.budget.core.data.datastore.appDataStore
import com.kamedevin.budget.core.domain.repository.AppLockRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.MessageDigest
import java.security.SecureRandom
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AppLockRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : AppLockRepository {

    private val pinHashKey = stringPreferencesKey("lock_pin_salt_and_hash")

    override fun observeIsLockEnabled(): Flow<Boolean> =
        context.appDataStore.data.map { prefs -> prefs[pinHashKey] != null }

    override suspend fun setPin(pin: String) {
        val salt = generateSalt()
        context.appDataStore.edit { prefs ->
            prefs[pinHashKey] = "$salt:${hash(pin, salt)}"
        }
    }

    override suspend fun verifyPin(pin: String): Boolean {
        val stored = context.appDataStore.data.map { it[pinHashKey] }.first() ?: return false
        val parts = stored.split(":", limit = 2)
        if (parts.size != 2) return false
        val (salt, expectedHash) = parts
        return hash(pin, salt) == expectedHash
    }

    override suspend fun disableLock() {
        context.appDataStore.edit { prefs -> prefs.remove(pinHashKey) }
    }

    private fun generateSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.toHex()
    }

    private fun hash(pin: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt.toByteArray(Charsets.UTF_8))
        return digest.digest(pin.toByteArray(Charsets.UTF_8)).toHex()
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
}
