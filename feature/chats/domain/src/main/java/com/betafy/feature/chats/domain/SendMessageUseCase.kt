package com.betafy.feature.chats.domain

class SendMessageUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(chatId: String, senderId: String, text: String): Result<Unit> {
        val message = text.trim()
        if (chatId.isBlank() || senderId.isBlank() || message.isEmpty()) {
            return Result.failure(IllegalArgumentException("Введите текст сообщения"))
        }
        return repository.send(chatId, senderId, message)
    }
}
