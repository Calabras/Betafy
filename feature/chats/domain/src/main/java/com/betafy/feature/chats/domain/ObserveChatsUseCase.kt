package com.betafy.feature.chats.domain

import com.betafy.feature.chats.domain.model.ChatSummary
import kotlinx.coroutines.flow.Flow

class ObserveChatsUseCase(
    private val repository: ChatRepository
) {
    operator fun invoke(userId: String): Flow<List<ChatSummary>> = repository.observeChats(userId)
}
