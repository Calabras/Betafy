package com.betafy.feature.profile.domain.model

data class WriterProfile(
    val userId: String,
    val bio: String,
    val experienceLevel: String,
    val preferredLanguage: String
)
