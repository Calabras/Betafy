package com.betafy.feature.profile.data

import com.betafy.feature.profile.domain.model.ReaderProfile
import org.junit.Assert.assertEquals
import org.junit.Test

class ReaderProfileMapperTest {

    @Test
    fun roundTripKeepsFieldsAndUserKey() {
        val profile = ReaderProfile(
            userId = "uid-1",
            bio = "Люблю детективы",
            experienceLevel = "1–3 года",
            preferredLanguage = "Русский",
            maxWordCount = 80000,
            isAvailable = true,
            cooperationType = "платно",
            price = 1500.0,
            genreIds = listOf("g1", "g2"),
            feedbackTypeIds = listOf("f1")
        )

        val restored = ReaderProfileMapper.fromMap("uid-1", ReaderProfileMapper.toMap(profile))

        assertEquals(profile, restored)
    }

    @Test
    fun readsIdsStoredAsFirebaseMap() {
        val raw = mapOf(
            "user_id" to "uid-1",
            "bio" to "Текст",
            "experience_level" to "Нет опыта",
            "preferred_language" to "English",
            "max_word_count" to 10L,
            "is_available" to true,
            "cooperation_type" to "бартер",
            "price" to 0L,
            "genre_ids" to mapOf("0" to "g1", "1" to "g2"),
            "feedback_type_ids" to mapOf("0" to "f1")
        )

        val profile = ReaderProfileMapper.fromMap("uid-1", raw)

        assertEquals(listOf("g1", "g2"), profile.genreIds)
        assertEquals(listOf("f1"), profile.feedbackTypeIds)
        assertEquals(10, profile.maxWordCount)
        assertEquals(0.0, profile.price, 0.0)
    }
}
