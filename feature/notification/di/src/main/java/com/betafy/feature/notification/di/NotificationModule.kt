package com.betafy.feature.notification.di

import com.betafy.feature.notification.data.NotificationRemoteDataSource
import com.betafy.feature.notification.data.NotificationRepositoryImpl
import com.betafy.feature.notification.domain.DeleteNotificationsUseCase
import com.betafy.feature.notification.domain.EnsureNotificationUseCase
import com.betafy.feature.notification.domain.MarkNotificationsReadUseCase
import com.betafy.feature.notification.domain.NotificationRepository
import com.betafy.feature.notification.domain.ObserveNotificationsUseCase
import com.betafy.feature.notification.domain.SaveNotificationUseCase
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    @Singleton
    fun provideNotificationRemoteDataSource(
        database: FirebaseDatabase
    ): NotificationRemoteDataSource = NotificationRemoteDataSource(database)

    @Provides
    @Singleton
    fun provideNotificationRepository(
        dataSource: NotificationRemoteDataSource
    ): NotificationRepository = NotificationRepositoryImpl(dataSource)

    @Provides
    fun provideObserveNotificationsUseCase(
        repository: NotificationRepository
    ): ObserveNotificationsUseCase = ObserveNotificationsUseCase(repository)

    @Provides
    fun provideEnsureNotificationUseCase(
        repository: NotificationRepository
    ): EnsureNotificationUseCase = EnsureNotificationUseCase(repository)

    @Provides
    fun provideSaveNotificationUseCase(
        repository: NotificationRepository
    ): SaveNotificationUseCase = SaveNotificationUseCase(repository)

    @Provides
    fun provideMarkNotificationsReadUseCase(
        repository: NotificationRepository
    ): MarkNotificationsReadUseCase = MarkNotificationsReadUseCase(repository)

    @Provides
    fun provideDeleteNotificationsUseCase(
        repository: NotificationRepository
    ): DeleteNotificationsUseCase = DeleteNotificationsUseCase(repository)
}
