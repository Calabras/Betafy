package com.betafy.feature.match.di

import android.content.Context
import com.betafy.feature.match.data.LocalSwipeLog
import com.betafy.feature.match.data.MatchRemoteDataSource
import com.betafy.feature.match.data.MatchRepositoryImpl
import com.betafy.feature.match.domain.LoadIncomingInterestsUseCase
import com.betafy.feature.match.domain.LoadMatchBoardUseCase
import com.betafy.feature.match.domain.MatchRepository
import com.betafy.feature.match.domain.ProposeMatchUseCase
import com.betafy.feature.match.domain.SwipeLog
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MatchModule {

    @Provides
    @Singleton
    fun provideSwipeLog(
        @ApplicationContext context: Context
    ): SwipeLog = LocalSwipeLog(context)

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
    fun provideLoadIncomingInterestsUseCase(
        repository: MatchRepository
    ): LoadIncomingInterestsUseCase = LoadIncomingInterestsUseCase(repository)

    @Provides
    fun provideLoadMatchBoardUseCase(
        repository: MatchRepository
    ): LoadMatchBoardUseCase = LoadMatchBoardUseCase(repository)

    @Provides
    fun provideProposeMatchUseCase(
        repository: MatchRepository
    ): ProposeMatchUseCase = ProposeMatchUseCase(repository)
}
