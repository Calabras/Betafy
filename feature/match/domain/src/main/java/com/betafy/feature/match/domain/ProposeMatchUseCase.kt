package com.betafy.feature.match.domain

import com.betafy.feature.match.domain.model.NewMatch

class ProposeMatchUseCase(
    private val repository: MatchRepository
) {
    suspend operator fun invoke(userId: String, requestId: String, readerId: String): Result<Unit> {
        if (userId.isBlank()) {
            return Result.failure(IllegalStateException("Нужно войти в аккаунт"))
        }
        val board = LoadMatchBoardUseCase(repository).invoke(userId).getOrElse { return Result.failure(it) }
        val line = (board.writerLines + board.readerLines).firstOrNull {
            it.requestId == requestId && it.readerId == readerId
        } ?: return Result.failure(IllegalStateException("Эта пара больше не подходит"))
        return repository.createMatch(
            NewMatch(
                requestId = requestId,
                readerId = readerId,
                initiatorId = userId,
                score = line.score.toDouble(),
                createdAt = System.currentTimeMillis()
            )
        )
    }
}
