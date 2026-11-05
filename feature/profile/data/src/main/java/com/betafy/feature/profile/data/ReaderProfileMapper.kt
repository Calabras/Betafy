package com.betafy.feature.profile.data

import com.betafy.feature.profile.domain.model.ReaderProfile

object ReaderProfileMapper {
    const val NODE = "ReaderProfiles"

    fun toMap(profile: ReaderProfile): Map<String, Any> = mapOf(
        USER_ID to profile.userId,
        BIO to profile.bio,
        EXPERIENCE_LEVEL to profile.experienceLevel,
        PREFERRED_LANGUAGE to profile.preferredLanguage,
        MAX_WORD_COUNT to profile.maxWordCount,
        IS_AVAILABLE to profile.isAvailable,
        COOPERATION_TYPE to profile.cooperationType,
        PRICE to profile.price,
        GENRE_IDS to profile.genreIds,
        FEEDBACK_TYPE_IDS to profile.feedbackTypeIds
    )

    fun fromMap(userId: String, raw: Map<String, Any?>): ReaderProfile = ReaderProfile(
        userId = raw[USER_ID]?.toString().takeUnless { it.isNullOrBlank() } ?: userId,
        bio = raw[BIO]?.toString().orEmpty(),
        experienceLevel = raw[EXPERIENCE_LEVEL]?.toString().orEmpty(),
        preferredLanguage = raw[PREFERRED_LANGUAGE]?.toString().orEmpty(),
        maxWordCount = raw[MAX_WORD_COUNT].asInt(),
        isAvailable = raw[IS_AVAILABLE].asBoolean(),
        cooperationType = raw[COOPERATION_TYPE]?.toString().orEmpty(),
        price = raw[PRICE].asDouble(),
        genreIds = raw[GENRE_IDS].asStringList(),
        feedbackTypeIds = raw[FEEDBACK_TYPE_IDS].asStringList()
    )

    private const val USER_ID = "user_id"
    private const val BIO = "bio"
    private const val EXPERIENCE_LEVEL = "experience_level"
    private const val PREFERRED_LANGUAGE = "preferred_language"
    private const val MAX_WORD_COUNT = "max_word_count"
    private const val IS_AVAILABLE = "is_available"
    private const val COOPERATION_TYPE = "cooperation_type"
    private const val PRICE = "price"
    private const val GENRE_IDS = "genre_ids"
    private const val FEEDBACK_TYPE_IDS = "feedback_type_ids"
}

internal fun Any?.asInt(): Int = when (this) {
    is Int -> this
    is Long -> toInt()
    is Double -> toInt()
    is String -> toIntOrNull() ?: 0
    else -> 0
}

internal fun Any?.asDouble(): Double = when (this) {
    is Double -> this
    is Float -> toDouble()
    is Long -> toDouble()
    is Int -> toDouble()
    is String -> replace(',', '.').toDoubleOrNull() ?: 0.0
    else -> 0.0
}

internal fun Any?.asBoolean(): Boolean = when (this) {
    is Boolean -> this
    is String -> equals("true", ignoreCase = true)
    is Number -> toInt() != 0
    else -> false
}

internal fun Any?.asStringList(): List<String> = when (this) {
    is List<*> -> mapNotNull { it?.toString() }.filter { it.isNotBlank() }
    is Map<*, *> -> entries
        .sortedBy { it.key?.toString() }
        .mapNotNull { it.value?.toString() }
        .filter { it.isNotBlank() }
    else -> emptyList()
}
