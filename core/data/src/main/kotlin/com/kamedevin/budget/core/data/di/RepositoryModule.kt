package com.kamedevin.budget.core.data.di

import com.kamedevin.budget.core.data.repository.AccountRepositoryImpl
import com.kamedevin.budget.core.data.repository.AppLockRepositoryImpl
import com.kamedevin.budget.core.data.repository.BudgetSettingsRepositoryImpl
import com.kamedevin.budget.core.data.repository.CategoryRepositoryImpl
import com.kamedevin.budget.core.data.repository.ThemePreferenceRepositoryImpl
import com.kamedevin.budget.core.data.repository.TransactionRepositoryImpl
import com.kamedevin.budget.core.data.repository.WidgetPreferenceRepositoryImpl
import com.kamedevin.budget.core.domain.repository.AccountRepository
import com.kamedevin.budget.core.domain.repository.AppLockRepository
import com.kamedevin.budget.core.domain.repository.BudgetSettingsRepository
import com.kamedevin.budget.core.domain.repository.CategoryRepository
import com.kamedevin.budget.core.domain.repository.ThemePreferenceRepository
import com.kamedevin.budget.core.domain.repository.TransactionRepository
import com.kamedevin.budget.core.domain.repository.WidgetPreferenceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindAccountRepository(impl: AccountRepositoryImpl): AccountRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindBudgetSettingsRepository(impl: BudgetSettingsRepositoryImpl): BudgetSettingsRepository

    @Binds
    @Singleton
    abstract fun bindThemePreferenceRepository(impl: ThemePreferenceRepositoryImpl): ThemePreferenceRepository

    @Binds
    @Singleton
    abstract fun bindWidgetPreferenceRepository(impl: WidgetPreferenceRepositoryImpl): WidgetPreferenceRepository

    @Binds
    @Singleton
    abstract fun bindAppLockRepository(impl: AppLockRepositoryImpl): AppLockRepository
}
