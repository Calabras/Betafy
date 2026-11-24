package com.betafy.feature.match.data

import com.betafy.feature.match.domain.MatchBoardBuilder
import org.junit.Assert.assertEquals
import org.junit.Test

class MatchSnapshotParserTest {
    @Test
    fun readsSchemaTablesAndAppProfile() {
        val catalog = MatchSnapshotParser.parse(
            betaReaders = mapOf(
                "ira" to mapOf(
                    "user_id" to "ira",
                    "experience_level" to "Больше 3 лет",
                    "preferred_language" to "Русский",
                    "max_word_count" to 100_000L,
                    "is_available" to true,
                    "cooperation_type" to "бесплатно"
                )
            ),
            appReaders = mapOf(
                "kostya" to mapOf(
                    "experience_level" to "1–3 года",
                    "preferred_language" to "Русский",
                    "max_word_count" to 10_000L,
                    "is_available" to true,
                    "cooperation_type" to "бесплатно",
                    "genre_ids" to mapOf("0" to "fantasy", "1" to "detective"),
                    "feedback_type_ids" to listOf("plot", "characters")
                )
            ),
            readerGenres = mapOf("ira" to mapOf("reader_id" to "ira", "fantasy" to true, "detective" to true)),
            readerFeedback = mapOf("ira" to mapOf("reader_id" to "ira", "plot" to true, "characters" to true)),
            works = mapOf(
                "north" to mapOf(
                    "writer_id" to "anna",
                    "title" to "Северный свет",
                    "word_count" to 40_000L,
                    "language" to "Русский"
                )
            ),
            workGenres = mapOf("north" to mapOf("writer_id" to "anna", "fantasy" to true, "detective" to true)),
            requests = mapOf(
                "req_north" to mapOf(
                    "work_id" to "north",
                    "cooperation_type" to "бесплатно",
                    "status" to "Открыт",
                    "deadline" to "2026-12-20"
                )
            ),
            requestFeedback = mapOf("req_north" to mapOf("writer_id" to "anna", "plot" to true, "characters" to true)),
            matches = emptyMap(),
            users = mapOf(
                "anna" to mapOf("username" to "Анна"),
                "ira" to mapOf("username" to "Ира"),
                "kostya" to mapOf("username" to "Костя")
            ),
            genres = mapOf("fantasy" to mapOf("name" to "Фэнтези"), "detective" to "Детектив"),
            feedbackTypes = mapOf("plot" to mapOf("name" to "Сюжет"), "characters" to mapOf("name" to "Персонажи"))
        )
        val board = MatchBoardBuilder.build(catalog, "anna", "2026-11-22")
        assertEquals(listOf("Ира" to 100), board.writerLines.map { it.counterpart to it.score })
        assertEquals("Фэнтези, Детектив · бесплатно · 40000 слов · Сюжет, Персонажи", board.writerLines.first().caption)
        val labels = board.writerLines.first().traits.map { it.label }
        assertEquals(listOf("Жанр: Фэнтези", "Жанр: Детектив"), labels.filter { it.startsWith("Жанр") })
        assertEquals(listOf("Интерес: Сюжет", "Интерес: Персонажи"), labels.filter { it.startsWith("Интерес") })
    }
}
