package com.betafy.feature.notification.domain.model

data class InboxNotification(
    val id: String,
    val userId: String,
    val type: String,
    val relatedEntityId: String?,
    val text: String,
    val isRead: Boolean
)
