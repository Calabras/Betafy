package com.betafy.feature.profile.data

import com.betafy.feature.profile.domain.ProfileRepository
import com.betafy.feature.profile.domain.model.FeedbackType
import com.betafy.feature.profile.domain.model.Genre
import com.betafy.feature.profile.domain.model.ReaderProfile
import com.betafy.feature.profile.domain.model.WriterProfile
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.TimeoutCancellationException

class ProfileRepositoryImpl(
    private val dataSource: ProfileRemoteDataSource
) : ProfileRepository {

    override suspend fun getReaderProfile(userId: String): Result<ReaderProfile?> = resultOf {
        val raw = dataSource.readProfile(ReaderProfileMapper.NODE, userId) ?: return@resultOf null
        ReaderProfileMapper.fromMap(userId, raw)
    }

    override suspend fun getWriterProfile(userId: String): Result<WriterProfile?> = resultOf {
        val raw = dataSource.readProfile(WriterProfileMapper.NODE, userId) ?: return@resultOf null
        WriterProfileMapper.fromMap(userId, raw)
    }

    override suspend fun saveReaderProfile(profile: ReaderProfile): Result<Unit> = resultOf {
        dataSource.writeProfile(
            ReaderProfileMapper.NODE,
            profile.userId,
            ReaderProfileMapper.toMap(profile)
        )
    }

    override suspend fun saveWriterProfile(profile: WriterProfile): Result<Unit> = resultOf {
        dataSource.writeProfile(
            WriterProfileMapper.NODE,
            profile.userId,
            WriterProfileMapper.toMap(profile)
        )
    }

    override suspend fun getGenres(): Result<List<Genre>> = resultOf {
        dataSource.readGenres()
    }

    override suspend fun getFeedbackTypes(): Result<List<FeedbackType>> = resultOf {
        dataSource.readFeedbackTypes()
    }

    private suspend inline fun <T> resultOf(block: suspend () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (e: TimeoutCancellationException) {
            Result.failure(IllegalStateException("Нет связи с базой. Проверьте интернет"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(IllegalStateException(readable(e)))
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
            return "Нет доступа к профилю, правила базы для профилей ещё не открыты"
        }
        return "Не удалось выполнить запрос"
    }
}
