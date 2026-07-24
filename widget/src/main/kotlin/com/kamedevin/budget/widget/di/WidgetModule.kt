package com.kamedevin.budget.widget.di

import com.kamedevin.budget.core.domain.repository.WidgetRefreshNotifier
import com.kamedevin.budget.widget.GlanceWidgetRefreshNotifier
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WidgetModule {
    @Binds
    @Singleton
    abstract fun bindWidgetRefreshNotifier(impl: GlanceWidgetRefreshNotifier): WidgetRefreshNotifier
}
