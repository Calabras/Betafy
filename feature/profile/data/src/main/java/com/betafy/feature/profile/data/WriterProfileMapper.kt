package com.betafy.feature.profile.data

import com.betafy.feature.profile.domain.model.WriterProfile

object WriterProfileMapper {
    const val NODE = "WriterProfiles"

    fun toMap(profile: WriterProfile): Map<String, Any> = mapOf(
        USER_ID to profile.userId,
        BIO to profile.bio,
        EXPERIENCE_LEVEL to profile.experienceLevel,
        PREFERRED_LANGUAGE to profile.preferredLanguage
    )

    fun fromMap(userId: String, raw: Map<String, Any?>): WriterProfile = WriterProfile(
        userId = raw[USER_ID]?.toString().takeUnless { it.isNullOrBlank() } ?: userId,
        bio = raw[BIO]?.toString().orEmpty(),
        experienceLevel = raw[EXPERIENCE_LEVEL]?.toString().orEmpty(),
        preferredLanguage = raw[PREFERRED_LANGUAGE]?.toString().orEmpty()
    )

    private const val USER_ID = "user_id"
    private const val BIO = "bio"
    private const val EXPERIENCE_LEVEL = "experience_level"
    private const val PREFERRED_LANGUAGE = "preferred_language"
}
