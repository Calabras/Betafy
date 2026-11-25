package com.betafy.feature.chats.domain

class MarkMessagesReadUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(chatId: String, messageIds: List<String>): Result<Unit> {
        val ids = messageIds.filter { it.isNotBlank() }.distinct()
        if (chatId.isBlank() || ids.isEmpty()) return Result.success(Unit)
        return repository.markRead(chatId, ids)
    }
}
