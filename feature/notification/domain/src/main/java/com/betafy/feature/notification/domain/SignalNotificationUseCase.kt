package com.betafy.feature.notification.domain

class SignalNotificationUseCase(
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
        val clean = notificationText(text)
        val existing = repository.list(userId).getOrElse { return Result.failure(it) }
        val same = existing.find { it.relatedEntityId == relatedEntityId && it.type == type }
        if (same == null) return repository.save(userId, type, clean, relatedEntityId)
        if (!same.isRead && same.text == clean) return Result.success(Unit)
        return repository.revive(same.id, clean)
    }
}
