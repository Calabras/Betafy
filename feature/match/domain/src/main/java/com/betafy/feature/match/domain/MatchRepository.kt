package com.betafy.feature.match.domain

import com.betafy.feature.match.domain.model.MatchCatalog
import com.betafy.feature.match.domain.model.NewMatch

interface MatchRepository {
    suspend fun loadCatalog(): Result<MatchCatalog>
    suspend fun createMatch(match: NewMatch): Result<Unit>
}
