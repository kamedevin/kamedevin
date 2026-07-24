package com.kamedevin.budget.backup.googledrive.di

import com.kamedevin.budget.backup.api.BackupManager
import com.kamedevin.budget.backup.googledrive.GoogleDriveBackupManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BackupBindingModule {
    @Binds
    @Singleton
    abstract fun bindBackupManager(impl: GoogleDriveBackupManager): BackupManager
}
