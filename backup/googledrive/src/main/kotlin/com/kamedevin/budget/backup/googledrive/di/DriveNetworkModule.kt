package com.kamedevin.budget.backup.googledrive.di

import com.kamedevin.budget.backup.googledrive.DriveApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object DriveNetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
            .build()

    // No converter factory needed — every DriveApi call uses raw RequestBody/ResponseBody, with
    // JSON (de)serialization done manually via kotlinx.serialization in GoogleDriveBackupManager.
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/")
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideDriveApi(retrofit: Retrofit): DriveApi = retrofit.create(DriveApi::class.java)
}
