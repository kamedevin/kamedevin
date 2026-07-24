package com.kamedevin.budget.core.data.di

import android.content.Context
import androidx.room.Room
import com.kamedevin.budget.core.database.AppDatabase
import com.kamedevin.budget.core.database.dao.AccountDao
import com.kamedevin.budget.core.database.dao.BudgetSettingsDao
import com.kamedevin.budget.core.database.dao.CategoryDao
import com.kamedevin.budget.core.database.dao.TransactionDao
import com.kamedevin.budget.core.database.seed.DefaultDataSeedCallback
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        val seedScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        // Room only invokes the seed callback lazily on first access, by which point this
        // lateinit var has already been assigned by the `.build()` call below.
        lateinit var database: AppDatabase
        database = Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .addCallback(DefaultDataSeedCallback(seedScope) { database })
            .build()
        return database
    }

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideAccountDao(database: AppDatabase): AccountDao = database.accountDao()

    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionDao = database.transactionDao()

    @Provides
    fun provideBudgetSettingsDao(database: AppDatabase): BudgetSettingsDao = database.budgetSettingsDao()
}
