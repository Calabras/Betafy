package com.betafy.feature.profile.domain

import com.betafy.feature.profile.domain.model.WriterProfile

class GetWriterProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(userId: String): Result<WriterProfile?> =
        repository.getWriterProfile(userId)
}
