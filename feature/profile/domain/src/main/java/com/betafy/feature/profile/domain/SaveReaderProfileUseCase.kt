package com.betafy.feature.profile.domain

import com.betafy.feature.profile.domain.model.ProfileChoices
import com.betafy.feature.profile.domain.model.ReaderProfile

class SaveReaderProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(
        profile: ReaderProfile,
        genreCatalogSize: Int = 0,
        feedbackCatalogSize: Int = 0
    ): Result<Unit> {
        val problem = ProfileRules.readerProblem(profile, genreCatalogSize, feedbackCatalogSize)
        if (problem != null) return Result.failure(IllegalArgumentException(problem))
        val stored = if (profile.cooperationType == ProfileChoices.PAID) {
            profile
        } else {
            profile.copy(price = 0.0)
        }
        return repository.saveReaderProfile(stored)
    }
}
