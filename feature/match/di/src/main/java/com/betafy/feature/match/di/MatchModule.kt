package com.betafy.feature.match.di

import com.betafy.feature.match.data.MatchRemoteDataSource
import com.betafy.feature.match.data.MatchRepositoryImpl
import com.betafy.feature.match.domain.LoadMatchBoardUseCase
import com.betafy.feature.match.domain.MatchRepository
import com.betafy.feature.match.domain.ProposeMatchUseCase
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MatchModule {

    @Provides
    @Singleton
    fun provideMatchRemoteDataSource(
        database: FirebaseDatabase
    ): MatchRemoteDataSource = MatchRemoteDataSource(database)

    @Provides
    @Singleton
    fun provideMatchRepository(
        dataSource: MatchRemoteDataSource
    ): MatchRepository = MatchRepositoryImpl(dataSource)

    @Provides
    fun provideLoadMatchBoardUseCase(
        repository: MatchRepository
    ): LoadMatchBoardUseCase = LoadMatchBoardUseCase(repository)

    @Provides
    fun provideProposeMatchUseCase(
        repository: MatchRepository
    ): ProposeMatchUseCase = ProposeMatchUseCase(repository)
}
