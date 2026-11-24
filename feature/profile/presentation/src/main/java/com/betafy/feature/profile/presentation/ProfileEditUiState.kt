package com.betafy.feature.profile.presentation

import com.betafy.feature.profile.domain.ProfileRules
import com.betafy.feature.profile.domain.model.FeedbackType
import com.betafy.feature.profile.domain.model.Genre
import com.betafy.feature.profile.domain.model.ReaderProfile
import com.betafy.feature.profile.domain.model.WriterProfile

data class ProfileEditUiState(
    val kind: ProfileKind = ProfileKind.READER,
    val userId: String = "",
    val bio: String = "",
    val experienceLevel: String = "",
    val preferredLanguage: String = "",
    val maxWordCount: String = "",
    val isAvailable: Boolean = false,
    val cooperationType: String = "",
    val price: String = "",
    val genres: List<Genre> = emptyList(),
    val feedbackTypes: List<FeedbackType> = emptyList(),
    val selectedGenreIds: Set<String> = emptySet(),
    val selectedFeedbackTypeIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val loaded: Boolean = false,
    val alreadyExists: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false
) {
    fun inputProblem(): String? = if (kind == ProfileKind.READER) {
        ProfileRules.readerProblem(toReader(), genres.size, feedbackTypes.size)
    } else {
        ProfileRules.writerProblem(toWriter())
    }

    fun toReader(): ReaderProfile = ReaderProfile(
        userId = userId,
        bio = bio.trim(),
        experienceLevel = experienceLevel,
        preferredLanguage = preferredLanguage,
        maxWordCount = maxWordCount.toIntOrNull() ?: 0,
        isAvailable = isAvailable,
        cooperationType = cooperationType,
        price = price.replace(',', '.').toDoubleOrNull() ?: 0.0,
        genreIds = selectedGenreIds.toList(),
        feedbackTypeIds = selectedFeedbackTypeIds.toList()
    )

    fun toWriter(): WriterProfile = WriterProfile(
        userId = userId,
        bio = bio.trim(),
        experienceLevel = experienceLevel,
        preferredLanguage = preferredLanguage
    )
}
