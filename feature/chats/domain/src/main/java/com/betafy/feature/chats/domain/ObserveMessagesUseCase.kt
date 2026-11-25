package com.betafy.feature.chats.domain

import com.betafy.feature.chats.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

class ObserveMessagesUseCase(
    private val repository: ChatRepository
) {
    operator fun invoke(chatId: String, userId: String): Flow<List<ChatMessage>> =
        repository.observeMessages(chatId, userId)
}
