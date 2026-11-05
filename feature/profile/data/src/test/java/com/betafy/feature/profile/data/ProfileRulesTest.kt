package com.betafy.feature.profile.data

import com.betafy.feature.profile.domain.ProfileRules
import com.betafy.feature.profile.domain.model.ReaderProfile
import com.betafy.feature.profile.domain.model.WriterProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProfileRulesTest {
    @Test
    fun readerRequiresFilledFieldsAndNumericLimits() {
        val empty = reader()
        assertEquals("Напишите о себе", ProfileRules.readerProblem(empty, 0, 0))
        assertEquals(
            "Укажите максимальный объём больше нуля",
            ProfileRules.readerProblem(empty.copy(bio = "о себе", experienceLevel = "Нет опыта", preferredLanguage = "Русский"), 0, 0)
        )
        assertEquals(
            "Укажите цену за платное чтение",
            ProfileRules.readerProblem(
                readyReader().copy(cooperationType = "платно", price = 0.0),
                0,
                0
            )
        )
        assertNull(ProfileRules.readerProblem(readyReader(), 0, 0))
        assertEquals("Выберите хотя бы один жанр", ProfileRules.readerProblem(readyReader(), 2, 0))
    }

    @Test
    fun writerRequiresTextAndChoices() {
        assertEquals("Напишите об авторе", ProfileRules.writerProblem(WriterProfile("u", "", "", "")))
        assertNull(
            ProfileRules.writerProblem(WriterProfile("u", "автор", "Нет опыта", "Русский"))
        )
    }

    private fun reader() = ReaderProfile(
        userId = "u",
        bio = "",
        experienceLevel = "",
        preferredLanguage = "",
        maxWordCount = 0,
        isAvailable = false,
        cooperationType = "",
        price = 0.0,
        genreIds = emptyList(),
        feedbackTypeIds = emptyList()
    )

    private fun readyReader() = reader().copy(
        bio = "о себе",
        experienceLevel = "Нет опыта",
        preferredLanguage = "Русский",
        maxWordCount = 1000,
        cooperationType = "бесплатно"
    )
}
