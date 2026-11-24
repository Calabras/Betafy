package com.betafy.feature.notification.domain

class SaveNotificationUseCase(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(
        userId: String,
        type: String,
        text: String,
        relatedEntityId: String? = null
    ): Result<Unit> {
        if (userId.isBlank() || text.isBlank()) {
            return Result.failure(IllegalArgumentException("Не удалось сохранить уведомление"))
        }
        return repository.save(userId, type, notificationText(text), relatedEntityId)
    }
}
