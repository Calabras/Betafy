package com.betafy.feature.profile.domain

import com.betafy.feature.profile.domain.model.ProfileChoices
import com.betafy.feature.profile.domain.model.ReaderProfile
import com.betafy.feature.profile.domain.model.WriterProfile

object ProfileRules {
    fun readerProblem(
        profile: ReaderProfile,
        genreCatalogSize: Int,
        feedbackCatalogSize: Int
    ): String? {
        if (profile.userId.isBlank()) return "Нет пользователя"
        if (profile.bio.isBlank()) return "Напишите о себе"
        if (profile.experienceLevel.isBlank()) return "Выберите опыт бета-чтения"
        if (profile.preferredLanguage.isBlank()) return "Выберите язык чтения"
        if (profile.maxWordCount <= 0) return "Укажите максимальный объём больше нуля"
        if (profile.cooperationType.isBlank()) return "Выберите формат сотрудничества"
        if (profile.cooperationType == ProfileChoices.PAID && profile.price <= 0.0) {
            return "Укажите цену за платное чтение"
        }
        if (genreCatalogSize > 0 && profile.genreIds.isEmpty()) return "Выберите хотя бы один жанр"
        if (feedbackCatalogSize > 0 && profile.feedbackTypeIds.isEmpty()) {
            return "Выберите хотя бы одну компетенцию"
        }
        return null
    }

    fun writerProblem(profile: WriterProfile): String? {
        if (profile.userId.isBlank()) return "Нет пользователя"
        if (profile.bio.isBlank()) return "Напишите об авторе"
        if (profile.experienceLevel.isBlank()) return "Выберите опыт писательства"
        if (profile.preferredLanguage.isBlank()) return "Выберите язык произведений"
        return null
    }
}
