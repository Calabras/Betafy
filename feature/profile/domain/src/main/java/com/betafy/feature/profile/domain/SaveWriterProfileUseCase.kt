package com.betafy.feature.profile.domain

import com.betafy.feature.profile.domain.model.WriterProfile

class SaveWriterProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(profile: WriterProfile): Result<Unit> {
        val problem = ProfileRules.writerProblem(profile)
        if (problem != null) return Result.failure(IllegalArgumentException(problem))
        return repository.saveWriterProfile(profile)
    }
}
