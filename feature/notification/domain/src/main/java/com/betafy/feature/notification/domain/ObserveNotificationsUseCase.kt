package com.betafy.feature.notification.domain

import com.betafy.feature.notification.domain.model.InboxNotification
import kotlinx.coroutines.flow.Flow

class ObserveNotificationsUseCase(
    private val repository: NotificationRepository
) {
    operator fun invoke(userId: String): Flow<List<InboxNotification>> = repository.observe(userId)
}
