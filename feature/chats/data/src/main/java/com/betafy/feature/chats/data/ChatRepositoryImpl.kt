package com.betafy.feature.chats.data

import com.betafy.feature.chats.domain.ChatRepository
import com.betafy.feature.chats.domain.model.ChatMessage
import com.betafy.feature.chats.domain.model.ChatSummary
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class ChatRepositoryImpl(
    private val remote: ChatRemoteDataSource
) : ChatRepository {
    override fun observeChats(userId: String): Flow<List<ChatSummary>> =
        remote.observeChats(userId).catch { error -> throw IllegalStateException(readable(error)) }

    override fun observeMessages(chatId: String, userId: String): Flow<List<ChatMessage>> =
        remote.observeMessages(chatId, userId).catch { error -> throw IllegalStateException(readable(error)) }

    override suspend fun ensureChat(chatId: String, matchId: String): Result<Unit> = resultOf {
        remote.ensureChat(chatId, matchId)
    }

    override suspend fun send(chatId: String, senderId: String, text: String): Result<Unit> = resultOf {
        remote.send(chatId, senderId, text)
    }

    override suspend fun markRead(chatId: String, messageIds: List<String>): Result<Unit> = resultOf {
        remote.markRead(chatId, messageIds)
    }

    private suspend inline fun resultOf(block: suspend () -> Unit): Result<Unit> {
        return try {
            block()
            Result.success(Unit)
        } catch (error: TimeoutCancellationException) {
            Result.failure(IllegalStateException("Нет связи с базой, проверьте интернет"))
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Result.failure(IllegalStateException(readable(error)))
        }
    }

    private fun readable(error: Throwable): String {
        val text = generateSequence(error) { it.cause }.joinToString(" ") { it.message.orEmpty() }
        val offline = generateSequence(error) { it.cause }.any { it is IOException } ||
            text.contains("network", ignoreCase = true) ||
            text.contains("timeout", ignoreCase = true) ||
            text.contains("unavailable", ignoreCase = true)
        if (offline) return "Нет связи с базой, проверьте интернет"
        if (text.contains("Permission denied", ignoreCase = true) ||
            text.contains("PERMISSION_DENIED", ignoreCase = true)
        ) {
            return "Нет доступа к чатам, правила базы ещё не открыты"
        }
        if (error is IllegalStateException && error.message?.startsWith("Нет ") == true) {
            return error.message.orEmpty()
        }
        return error.message?.takeIf { it.isNotBlank() } ?: "Не удалось выполнить запрос"
    }
}
