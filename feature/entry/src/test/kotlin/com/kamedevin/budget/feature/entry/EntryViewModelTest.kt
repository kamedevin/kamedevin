package com.kamedevin.budget.feature.entry

import com.kamedevin.budget.core.domain.repository.AccountRepository
import com.kamedevin.budget.core.domain.repository.CategoryRepository
import com.kamedevin.budget.core.domain.repository.DailyTotal
import com.kamedevin.budget.core.domain.repository.TransactionRepository
import com.kamedevin.budget.core.domain.usecase.AddTransactionUseCase
import com.kamedevin.budget.core.model.Account
import com.kamedevin.budget.core.model.AccountBalance
import com.kamedevin.budget.core.model.AccountType
import com.kamedevin.budget.core.model.Bucket
import com.kamedevin.budget.core.model.Category
import com.kamedevin.budget.core.model.Transaction
import java.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EntryViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private val category = Category(id = 1, name = "Groceries", bucket = Bucket.NEEDS, colorHex = "#000000")
    private val account = Account(id = 1, name = "Checking", type = AccountType.CHECKING, startingBalanceCents = 0, colorHex = "#000000")

    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var viewModel: EntryViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        transactionRepository = FakeTransactionRepository()
        val categoryRepository = FakeCategoryRepository(listOf(category))
        val accountRepository = FakeAccountRepository(listOf(account))
        viewModel = EntryViewModel(
            addTransactionUseCase = AddTransactionUseCase(
                transactionRepository,
                categoryRepository,
                accountRepository,
            ),
            categoryRepository = categoryRepository,
            accountRepository = accountRepository,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_selectsFirstCategoryAndAccount() = runTest(dispatcher) {
        dispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(category.id, state.selectedCategoryId)
        assertEquals(account.id, state.selectedAccountId)
    }

    @Test
    fun save_withoutAmount_setsError() = runTest(dispatcher) {
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.save()
        dispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.error)
        assertEquals(0, transactionRepository.added.size)
    }

    @Test
    fun save_withValidAmount_succeeds() = runTest(dispatcher) {
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onAmountChange("12.50")
        viewModel.save()
        dispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.savedSuccessfully)
        assertEquals(1, transactionRepository.added.size)
        assertEquals(1_250L, transactionRepository.added.first().amountCents)
    }

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
        override fun observeAll() = flowOf(categories)
    }

    private class FakeAccountRepository(private val accounts: List<Account>) : AccountRepository {
        override suspend fun add(account: Account) = error("not used in this test")
        override suspend fun update(account: Account) = error("not used in this test")
        override suspend fun archive(account: Account) = error("not used in this test")
        override suspend fun getById(id: Long): Account? = accounts.firstOrNull { it.id == id }
        override fun observeActive() = flowOf(accounts)
        override fun observeAll() = flowOf(accounts)
        override fun observeBalance(accountId: Long): Flow<Long> = error("not used in this test")
        override fun observeAllBalances(): Flow<List<AccountBalance>> = error("not used in this test")
    }
}
