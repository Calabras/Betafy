package com.betafy.feature.match.data

import com.betafy.feature.match.domain.MatchRepository
import com.betafy.feature.match.domain.model.MatchCatalog
import com.betafy.feature.match.domain.model.NewMatch
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope

class MatchRepositoryImpl(
    private val remote: MatchRemoteDataSource
) : MatchRepository {

    override suspend fun loadCatalog(): Result<MatchCatalog> = resultOf {
        supervisorScope {
            val beta = async { remote.read(BETA_READERS) }
            val appReaders = async { readOptional(APP_READERS) }
            val readerGenres = async { remote.read(READER_GENRES) }
            val readerFeedback = async { remote.read(READER_FEEDBACK) }
            val works = async { remote.read(WORKS) }
            val workGenres = async { remote.read(WORK_GENRES) }
            val requests = async { remote.read(REQUESTS) }
            val requestFeedback = async { remote.read(REQUEST_FEEDBACK) }
            val matches = async { remote.read(MATCHES) }
            val users = async { readOptional(USERS) }
            val legacyUsers = async { readOptional(LEGACY_USERS) }
            val genres = async { readOptional(GENRES) }
            val feedbackTypes = async { readOptional(FEEDBACK_TYPES) }
            val required = listOf(beta, readerGenres, readerFeedback, works, workGenres, requests, requestFeedback, matches)
            val loaded = required.map { runCatching { it.await() } }
            val failure = loaded.firstOrNull { it.isFailure }?.exceptionOrNull()
            if (failure != null) throw failure
            MatchSnapshotParser.parse(
                betaReaders = beta.await(),
                appReaders = appReaders.await(),
                readerGenres = readerGenres.await(),
                readerFeedback = readerFeedback.await(),
                works = works.await(),
                workGenres = workGenres.await(),
                requests = requests.await(),
                requestFeedback = requestFeedback.await(),
                matches = matches.await(),
                users = legacyUsers.await() + users.await(),
                genres = genres.await(),
                feedbackTypes = feedbackTypes.await()
            )
        }
    }

    private suspend fun readOptional(node: String): Map<String, Any?> {
        return try {
            remote.read(node)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            val text = generateSequence<Throwable>(error) { it.cause }
                .joinToString(" ") { it.message.orEmpty() }
            if (text.contains("Permission denied", ignoreCase = true) ||
                text.contains("PERMISSION_DENIED", ignoreCase = true)
            ) {
                emptyMap()
            } else {
                throw error
            }
        }
    }

    override suspend fun createMatch(match: NewMatch): Result<Unit> = resultOf {
        remote.createMatch(
            mapOf(
                "request_id" to match.requestId,
                "reader_id" to match.readerId,
                "compatibility_score" to match.score,
                "initiator_id" to match.initiatorId,
                "status" to "ожидает",
                "created_at" to match.createdAt
            )
        )
    }

    private suspend inline fun <T> resultOf(block: suspend () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (error: TimeoutCancellationException) {
            Result.failure(IllegalStateException("Нет связи с базой, проверьте интернет"))
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Result.failure(IllegalStateException(readable(error)))
        }
    }

    private fun readable(error: Exception): String {
        val text = generateSequence<Throwable>(error) { it.cause }
            .joinToString(" ") { it.message.orEmpty() }
        val offline = generateSequence<Throwable>(error) { it.cause }.any { it is IOException } ||
            text.contains("network", ignoreCase = true) ||
            text.contains("timeout", ignoreCase = true) ||
            text.contains("unavailable", ignoreCase = true)
        if (offline) return "Нет связи с базой, проверьте интернет"
        if (text.contains("Permission denied", ignoreCase = true) ||
            text.contains("PERMISSION_DENIED", ignoreCase = true)
        ) {
            return "Нет доступа к таблицам мэтчинга, правила базы ещё не открыты"
        }
        return "Не удалось выполнить запрос"
    }

    private companion object {
        const val BETA_READERS = "beta_reader_profiles"
        const val APP_READERS = "ReaderProfiles"
        const val READER_GENRES = "reader_genres"
        const val READER_FEEDBACK = "reader_feedback_types"
        const val WORKS = "works"
        const val WORK_GENRES = "work_genres"
        const val REQUESTS = "beta_requests"
        const val REQUEST_FEEDBACK = "request_feedback_types"
        const val MATCHES = "matches"
        const val USERS = "Users"
        const val LEGACY_USERS = "users"
        const val GENRES = "genres"
        const val FEEDBACK_TYPES = "feedback_types"
    }
}
