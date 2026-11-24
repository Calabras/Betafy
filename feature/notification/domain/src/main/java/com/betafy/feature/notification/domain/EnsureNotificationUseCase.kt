package com.betafy.feature.notification.domain

class EnsureNotificationUseCase(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(
        userId: String,
        type: String,
        text: String,
        relatedEntityId: String
    ): Result<Unit> {
        if (userId.isBlank() || text.isBlank() || relatedEntityId.isBlank()) {
            return Result.success(Unit)
        }
        val existing = repository.list(userId).getOrElse { return Result.failure(it) }
        if (existing.any { it.relatedEntityId == relatedEntityId && it.type == type }) {
            return Result.success(Unit)
        }
        return repository.save(userId, type, notificationText(text), relatedEntityId)
    }
}
