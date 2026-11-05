package com.betafy.feature.profile.domain

import com.betafy.feature.profile.domain.model.ReaderProfile

class GetReaderProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(userId: String): Result<ReaderProfile?> =
        repository.getReaderProfile(userId)
}
