package com.betafy.feature.notification.data

import com.betafy.feature.notification.domain.NotificationRepository
import com.betafy.feature.notification.domain.model.InboxNotification
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class NotificationRepositoryImpl(
    private val dataSource: NotificationRemoteDataSource
) : NotificationRepository {

    override fun observe(userId: String): Flow<List<InboxNotification>> =
        dataSource.observe(userId).catch { error ->
            throw IllegalStateException(readable(error))
        }

    override suspend fun list(userId: String): Result<List<InboxNotification>> = resultOf {
        dataSource.list(userId)
    }

    override suspend fun save(
        userId: String,
        type: String,
        text: String,
        relatedEntityId: String?
    ): Result<Unit> = resultOf {
        dataSource.save(userId, type, text, relatedEntityId)
    }

    override suspend fun markRead(ids: List<String>): Result<Unit> = resultOf {
        dataSource.markRead(ids)
    }

    override suspend fun delete(ids: List<String>): Result<Unit> = resultOf {
        dataSource.delete(ids)
    }

    private suspend inline fun <T> resultOf(block: suspend () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (e: TimeoutCancellationException) {
            Result.failure(IllegalStateException("Нет связи с базой, проверьте интернет"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(IllegalStateException(readable(e)))
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
            return "Нет доступа к уведомлениям, правила базы ещё не открыты"
        }
        return "Не удалось выполнить запрос"
    }
}
