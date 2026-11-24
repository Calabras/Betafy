package com.betafy.feature.match.domain

import com.betafy.feature.match.domain.model.MatchBoard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LoadMatchBoardUseCase(
    private val repository: MatchRepository
) {
    suspend operator fun invoke(userId: String): Result<MatchBoard> {
        if (userId.isBlank()) {
            return Result.failure(IllegalStateException("Нужно войти в аккаунт"))
        }
        return repository.loadCatalog().map { catalog ->
            MatchBoardBuilder.build(catalog, userId, today())
        }
    }

    private fun today(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
}
