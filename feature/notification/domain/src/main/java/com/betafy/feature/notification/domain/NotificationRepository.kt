package com.betafy.feature.notification.domain

import com.betafy.feature.notification.domain.model.InboxNotification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun observe(userId: String): Flow<List<InboxNotification>>
    suspend fun list(userId: String): Result<List<InboxNotification>>
    suspend fun save(
        userId: String,
        type: String,
        text: String,
        relatedEntityId: String?
    ): Result<Unit>
    suspend fun revive(id: String, text: String): Result<Unit>
    suspend fun markRead(ids: List<String>): Result<Unit>
    suspend fun delete(ids: List<String>): Result<Unit>
}
