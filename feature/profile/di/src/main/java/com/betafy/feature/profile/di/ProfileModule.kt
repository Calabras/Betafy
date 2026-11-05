package com.betafy.feature.profile.di

import com.betafy.feature.profile.data.ProfileRemoteDataSource
import com.betafy.feature.profile.data.ProfileRepositoryImpl
import com.betafy.feature.profile.domain.GetFeedbackTypesUseCase
import com.betafy.feature.profile.domain.GetGenresUseCase
import com.betafy.feature.profile.domain.GetReaderProfileUseCase
import com.betafy.feature.profile.domain.GetWriterProfileUseCase
import com.betafy.feature.profile.domain.ProfileRepository
import com.betafy.feature.profile.domain.SaveReaderProfileUseCase
import com.betafy.feature.profile.domain.SaveWriterProfileUseCase
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Provides
    @Singleton
    fun provideProfileRemoteDataSource(
        database: FirebaseDatabase
    ): ProfileRemoteDataSource = ProfileRemoteDataSource(database)

    @Provides
    @Singleton
    fun provideProfileRepository(
        dataSource: ProfileRemoteDataSource
    ): ProfileRepository = ProfileRepositoryImpl(dataSource)

    @Provides
    fun provideGetReaderProfileUseCase(
        repository: ProfileRepository
    ): GetReaderProfileUseCase = GetReaderProfileUseCase(repository)

    @Provides
    fun provideSaveReaderProfileUseCase(
        repository: ProfileRepository
    ): SaveReaderProfileUseCase = SaveReaderProfileUseCase(repository)

    @Provides
    fun provideGetWriterProfileUseCase(
        repository: ProfileRepository
    ): GetWriterProfileUseCase = GetWriterProfileUseCase(repository)

    @Provides
    fun provideSaveWriterProfileUseCase(
        repository: ProfileRepository
    ): SaveWriterProfileUseCase = SaveWriterProfileUseCase(repository)

    @Provides
    fun provideGetGenresUseCase(
        repository: ProfileRepository
    ): GetGenresUseCase = GetGenresUseCase(repository)

    @Provides
    fun provideGetFeedbackTypesUseCase(
        repository: ProfileRepository
    ): GetFeedbackTypesUseCase = GetFeedbackTypesUseCase(repository)
}
