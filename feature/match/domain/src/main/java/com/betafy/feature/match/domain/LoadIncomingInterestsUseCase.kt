package com.betafy.feature.match.domain

import com.betafy.feature.match.domain.model.IncomingInterest
import com.betafy.feature.match.domain.model.MatchCatalog
import com.betafy.feature.match.domain.model.StoredMatch

class LoadIncomingInterestsUseCase(
    private val repository: MatchRepository
) {
    suspend operator fun invoke(userId: String): Result<List<IncomingInterest>> {
        if (userId.isBlank()) return Result.success(emptyList())
        return repository.loadCatalog().map { catalog -> interests(catalog, userId) }
    }

    companion object {
        fun interests(catalog: MatchCatalog, userId: String): List<IncomingInterest> =
            catalog.matches.mapNotNull { match -> interest(catalog, userId, match) }

        private fun interest(catalog: MatchCatalog, userId: String, match: StoredMatch): IncomingInterest? {
            if (match.readerId != userId) return null
            if (!match.status.trim().equals(WAITING, ignoreCase = true)) return null
            val person = catalog.people[match.initiatorId]
            val username = person?.username?.takeIf { it.isNotBlank() }
                ?: catalog.names[match.initiatorId].orEmpty()
            val name = listOf(person?.firstName.orEmpty(), person?.lastName.orEmpty())
                .filter { it.isNotBlank() }
                .joinToString(" ")
            return IncomingInterest(match.id, interestText(name, username))
        }

        private const val WAITING = "ожидает"
    }
}
