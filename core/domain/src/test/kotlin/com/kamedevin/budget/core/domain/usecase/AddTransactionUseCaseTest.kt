package com.kamedevin.budget.core.domain.usecase

import com.kamedevin.budget.core.domain.repository.AccountRepository
import com.kamedevin.budget.core.domain.repository.CategoryRepository
import com.kamedevin.budget.core.domain.repository.DailyTotal
import com.kamedevin.budget.core.domain.repository.TransactionRepository
import com.kamedevin.budget.core.model.Account
import com.kamedevin.budget.core.model.AccountBalance
import com.kamedevin.budget.core.model.AccountType
import com.kamedevin.budget.core.model.Bucket
import com.kamedevin.budget.core.model.Category
import com.kamedevin.budget.core.model.Transaction
import com.kamedevin.budget.core.model.TransactionType
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AddTransactionUseCaseTest {

    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var useCase: AddTransactionUseCase

    private val knownCategory = Category(id = 1, name = "Groceries", bucket = Bucket.NEEDS, colorHex = "#000000")
    private val knownAccount = Account(id = 1, name = "Checking", type = AccountType.CHECKING, startingBalanceCents = 0, colorHex = "#000000")

    @Before
    fun setUp() {
        transactionRepository = FakeTransactionRepository()
        useCase = AddTransactionUseCase(
            transactionRepository = transactionRepository,
            categoryRepository = FakeCategoryRepository(listOf(knownCategory)),
            accountRepository = FakeAccountRepository(listOf(knownAccount)),
        )
    }

    @Test
    fun invoke_rejectsZeroAmount() = runTest {
        val result = useCase(transaction(amountCents = 0))

        assertTrue(result.isFailure)
        assertEquals(0, transactionRepository.added.size)
    }

    @Test
    fun invoke_rejectsNegativeAmount() = runTest {
        val result = useCase(transaction(amountCents = -500))

        assertTrue(result.isFailure)
    }

    @Test
    fun invoke_rejectsUnknownCategory() = runTest {
        val result = useCase(transaction(categoryId = 999))

        assertTrue(result.isFailure)
        assertEquals(0, transactionRepository.added.size)
    }

    @Test
    fun invoke_rejectsUnknownAccount() = runTest {
        val result = useCase(transaction(accountId = 999))

        assertTrue(result.isFailure)
        assertEquals(0, transactionRepository.added.size)
    }

    @Test
    fun invoke_savesValidTransaction() = runTest {
        val result = useCase(transaction(amountCents = 1_500))

        assertTrue(result.isSuccess)
        assertEquals(1, transactionRepository.added.size)
        assertEquals(1_500L, transactionRepository.added.first().amountCents)
    }

    private fun transaction(
        amountCents: Long = 1_000,
        categoryId: Long = knownCategory.id,
        accountId: Long = knownAccount.id,
    ) = Transaction(
        amountCents = amountCents,
        type = TransactionType.EXPENSE,
        categoryId = categoryId,
        accountId = accountId,
        date = Instant.now(),
    )

    private class FakeTransactionRepository : TransactionRepository {
        val added = mutableListOf<Transaction>()

        override suspend fun add(transaction: Transaction): Long {
            added += transaction
            return added.size.toLong()
        }

        override suspend fun update(transaction: Transaction) = error("not used in this test")
        override suspend fun delete(id: Long) = error("not used in this test")
        override suspend fun getById(id: Long): Transaction? = error("not used in this test")
        override fun observeInRange(start: Instant, end: Instant) = error("not used in this test")
        override fun observeBucketSpend(start: Instant, end: Instant) = error("not used in this test")
        override fun observeIncome(start: Instant, end: Instant): Flow<Long> = error("not used in this test")
        override fun observeDailyTotals(start: Instant, end: Instant): Flow<List<DailyTotal>> =
            error("not used in this test")
        override fun observeSpendByAccount(start: Instant, end: Instant) = error("not used in this test")
    }

    private class FakeCategoryRepository(private val categories: List<Category>) : CategoryRepository {
        override suspend fun add(category: Category) = error("not used in this test")
        override suspend fun update(category: Category) = error("not used in this test")
        override suspend fun delete(category: Category) = error("not used in this test")
        override suspend fun getById(id: Long): Category? = categories.firstOrNull { it.id == id }
        override fun observeAll() = error("not used in this test")
    }

    private class FakeAccountRepository(private val accounts: List<Account>) : AccountRepository {
        override suspend fun add(account: Account) = error("not used in this test")
        override suspend fun update(account: Account) = error("not used in this test")
        override suspend fun archive(account: Account) = error("not used in this test")
        override suspend fun getById(id: Long): Account? = accounts.firstOrNull { it.id == id }
        override fun observeActive() = error("not used in this test")
        override fun observeAll() = error("not used in this test")
        override fun observeBalance(accountId: Long): Flow<Long> = error("not used in this test")
        override fun observeAllBalances(): Flow<List<AccountBalance>> = error("not used in this test")
    }
}
