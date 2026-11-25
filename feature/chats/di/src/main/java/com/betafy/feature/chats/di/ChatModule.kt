package com.betafy.feature.chats.di

import com.betafy.feature.chats.data.ChatRemoteDataSource
import com.betafy.feature.chats.data.ChatRepositoryImpl
import com.betafy.feature.chats.domain.ChatRepository
import com.betafy.feature.chats.domain.MarkMessagesReadUseCase
import com.betafy.feature.chats.domain.ObserveChatsUseCase
import com.betafy.feature.chats.domain.ObserveMessagesUseCase
import com.betafy.feature.chats.domain.OpenChatUseCase
import com.betafy.feature.chats.domain.SendMessageUseCase
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {
    @Provides
    @Singleton
    fun provideChatRemoteDataSource(
        database: FirebaseDatabase
    ): ChatRemoteDataSource = ChatRemoteDataSource(database)

    @Provides
    @Singleton
    fun provideChatRepository(
        dataSource: ChatRemoteDataSource
    ): ChatRepository = ChatRepositoryImpl(dataSource)

    @Provides
    fun provideObserveChatsUseCase(
        repository: ChatRepository
    ): ObserveChatsUseCase = ObserveChatsUseCase(repository)

    @Provides
    fun provideObserveMessagesUseCase(
        repository: ChatRepository
    ): ObserveMessagesUseCase = ObserveMessagesUseCase(repository)

    @Provides
    fun provideOpenChatUseCase(
        repository: ChatRepository
    ): OpenChatUseCase = OpenChatUseCase(repository)

    @Provides
    fun provideSendMessageUseCase(
        repository: ChatRepository
    ): SendMessageUseCase = SendMessageUseCase(repository)

    @Provides
    fun provideMarkMessagesReadUseCase(
        repository: ChatRepository
    ): MarkMessagesReadUseCase = MarkMessagesReadUseCase(repository)
}
