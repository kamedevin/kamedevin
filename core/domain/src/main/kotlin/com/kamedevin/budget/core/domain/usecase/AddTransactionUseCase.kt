package com.kamedevin.budget.core.domain.usecase

import com.kamedevin.budget.core.domain.repository.AccountRepository
import com.kamedevin.budget.core.domain.repository.CategoryRepository
import com.kamedevin.budget.core.domain.repository.TransactionRepository
import com.kamedevin.budget.core.model.Transaction
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(transaction: Transaction): Result<Long> {
        if (transaction.amountCents <= 0L) {
            return Result.failure(IllegalArgumentException("Amount must be greater than zero"))
        }
        if (categoryRepository.getById(transaction.categoryId) == null) {
            return Result.failure(IllegalArgumentException("Unknown category"))
        }
        if (accountRepository.getById(transaction.accountId) == null) {
            return Result.failure(IllegalArgumentException("Unknown account"))
        }
        return Result.success(transactionRepository.add(transaction))
    }
}
