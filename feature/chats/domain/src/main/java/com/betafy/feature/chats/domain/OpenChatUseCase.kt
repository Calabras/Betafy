package com.betafy.feature.chats.domain

class OpenChatUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(chatId: String, matchId: String): Result<Unit> {
        if (chatId.isBlank() || matchId.isBlank()) {
            return Result.failure(IllegalArgumentException("Не удалось открыть чат"))
        }
        return repository.ensureChat(chatId, matchId)
    }
}
