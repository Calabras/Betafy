package com.betafy.feature.notification.domain

class MarkNotificationsReadUseCase(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(ids: List<String>): Result<Unit> {
        val clean = ids.filter { it.isNotBlank() }.distinct()
        if (clean.isEmpty()) return Result.success(Unit)
        return repository.markRead(clean)
    }
}
