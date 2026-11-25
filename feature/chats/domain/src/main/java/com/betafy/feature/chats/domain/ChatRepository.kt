package com.betafy.feature.chats.domain

import com.betafy.feature.chats.domain.model.ChatMessage
import com.betafy.feature.chats.domain.model.ChatSummary
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observeChats(userId: String): Flow<List<ChatSummary>>
    fun observeMessages(chatId: String, userId: String): Flow<List<ChatMessage>>
    suspend fun ensureChat(chatId: String, matchId: String): Result<Unit>
    suspend fun send(chatId: String, senderId: String, text: String): Result<Unit>
    suspend fun markRead(chatId: String, messageIds: List<String>): Result<Unit>
}
