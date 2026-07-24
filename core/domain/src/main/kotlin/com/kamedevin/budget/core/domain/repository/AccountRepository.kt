package com.kamedevin.budget.core.domain.repository

import com.kamedevin.budget.core.model.Account
import com.kamedevin.budget.core.model.AccountBalance
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    suspend fun add(account: Account): Long
    suspend fun update(account: Account)
    suspend fun archive(account: Account)
    suspend fun getById(id: Long): Account?
    fun observeActive(): Flow<List<Account>>
    fun observeAll(): Flow<List<Account>>
    fun observeBalance(accountId: Long): Flow<Long>
    fun observeAllBalances(): Flow<List<AccountBalance>>
}
