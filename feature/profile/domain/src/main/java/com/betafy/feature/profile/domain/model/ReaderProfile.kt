package com.betafy.feature.profile.domain.model

data class ReaderProfile(
    val userId: String,
    val bio: String,
    val experienceLevel: String,
    val preferredLanguage: String,
    val maxWordCount: Int,
    val isAvailable: Boolean,
    val cooperationType: String,
    val price: Double,
    val genreIds: List<String>,
    val feedbackTypeIds: List<String>
)
