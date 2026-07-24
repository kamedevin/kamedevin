package com.kamedevin.budget.core.data.repository

import com.kamedevin.budget.core.data.mapper.toDomain
import com.kamedevin.budget.core.data.mapper.toEntity
import com.kamedevin.budget.core.database.dao.AccountDao
import com.kamedevin.budget.core.domain.repository.AccountRepository
import com.kamedevin.budget.core.model.Account
import com.kamedevin.budget.core.model.AccountBalance
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao,
) : AccountRepository {

    override suspend fun add(account: Account): Long = accountDao.insert(account.toEntity())

    override suspend fun update(account: Account) {
        accountDao.update(account.toEntity())
    }

    override suspend fun archive(account: Account) {
        accountDao.update(account.toEntity().copy(isArchived = true))
    }

    override suspend fun getById(id: Long): Account? = accountDao.getById(id)?.toDomain()

    override fun observeActive(): Flow<List<Account>> =
        accountDao.observeActive().map { list -> list.map { it.toDomain() } }

    override fun observeAll(): Flow<List<Account>> =
        accountDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeBalance(accountId: Long): Flow<Long> =
        accountDao.observeBalance(accountId).map { it ?: 0L }

    override fun observeAllBalances(): Flow<List<AccountBalance>> =
        combine(accountDao.observeActive(), accountDao.observeAllBalances()) { accounts, balances ->
            val balanceById = balances.associate { it.id to it.balanceCents }
            accounts.map { entity ->
                AccountBalance(
                    account = entity.toDomain(),
                    currentBalanceCents = balanceById[entity.id] ?: entity.startingBalanceCents,
                )
            }
        }
}
