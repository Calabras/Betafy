package com.betafy.feature.profile.domain

import com.betafy.feature.profile.domain.model.Genre

class GetGenresUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Result<List<Genre>> = repository.getGenres()
}
