package com.betafy.feature.profile.presentation

import com.betafy.feature.profile.domain.model.FeedbackType
import com.betafy.feature.profile.domain.model.Genre

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
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false
)
