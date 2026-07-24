package com.kamedevin.budget.core.domain.usecase

import com.kamedevin.budget.core.domain.repository.AccountRepository
import com.kamedevin.budget.core.model.AccountBalance
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetAccountBalancesUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    operator fun invoke(): Flow<List<AccountBalance>> = accountRepository.observeAllBalances()
}
