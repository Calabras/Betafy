package com.betafy.feature.profile.domain

import com.betafy.feature.profile.domain.model.FeedbackType

class GetFeedbackTypesUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Result<List<FeedbackType>> = repository.getFeedbackTypes()
}
