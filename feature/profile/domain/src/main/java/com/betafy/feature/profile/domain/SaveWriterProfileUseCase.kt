package com.betafy.feature.profile.domain

import com.betafy.feature.profile.domain.model.WriterProfile

class SaveWriterProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(profile: WriterProfile): Result<Unit> {
        if (profile.userId.isBlank()) {
            return Result.failure(IllegalArgumentException("Нет пользователя"))
        }
        if (profile.experienceLevel.isBlank()) {
            return Result.failure(IllegalArgumentException("Выберите опыт писательства"))
        }
        if (profile.preferredLanguage.isBlank()) {
            return Result.failure(IllegalArgumentException("Выберите язык произведений"))
        }
        return repository.saveWriterProfile(profile)
    }
}
