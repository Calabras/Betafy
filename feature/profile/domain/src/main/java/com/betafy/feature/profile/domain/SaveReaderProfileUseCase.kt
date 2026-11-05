package com.betafy.feature.profile.domain

import com.betafy.feature.profile.domain.model.ProfileChoices
import com.betafy.feature.profile.domain.model.ReaderProfile

class SaveReaderProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(profile: ReaderProfile): Result<Unit> {
        if (profile.userId.isBlank()) {
            return Result.failure(IllegalArgumentException("Нет пользователя"))
        }
        if (profile.experienceLevel.isBlank()) {
            return Result.failure(IllegalArgumentException("Выберите опыт бета-чтения"))
        }
        if (profile.preferredLanguage.isBlank()) {
            return Result.failure(IllegalArgumentException("Выберите язык чтения"))
        }
        if (profile.maxWordCount <= 0) {
            return Result.failure(IllegalArgumentException("Укажите максимальный объём больше нуля"))
        }
        if (profile.cooperationType.isBlank()) {
            return Result.failure(IllegalArgumentException("Выберите формат сотрудничества"))
        }
        if (profile.cooperationType == ProfileChoices.PAID && profile.price <= 0.0) {
            return Result.failure(IllegalArgumentException("Укажите цену за платное чтение"))
        }
        val stored = if (profile.cooperationType == ProfileChoices.PAID) {
            profile
        } else {
            profile.copy(price = 0.0)
        }
        return repository.saveReaderProfile(stored)
    }
}
