package com.betafy.feature.match.domain

import com.betafy.feature.match.domain.model.MatchCatalog
import com.betafy.feature.match.domain.model.ReaderCard
import com.betafy.feature.match.domain.model.RequestCard
import com.betafy.feature.match.domain.model.StoredMatch
import com.betafy.feature.match.domain.model.WorkCard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MatchRulesTest {
    @Test
    fun miniDatabasePairs() {
        val catalog = miniCatalog()
        assertEquals(listOf("Ира" to 100, "Борис" to 52), pairs(catalog, "anna"))
        assertEquals(listOf("Лена" to 97), pairs(catalog, "oleg"))
        assertEquals(listOf("Джон" to 100), pairs(catalog, "mia"))
        assertEquals(listOf("Северный свет" to 100), requests(catalog, "ira"))
        assertEquals(listOf("Северный свет" to 52), requests(catalog, "boris"))
        assertEquals(listOf("Тихий дом" to 97), requests(catalog, "lena"))
        assertEquals(emptyList<Pair<String, Int>>(), requests(catalog, "nina"))
        assertEquals(emptyList<Pair<String, Int>>(), requests(catalog, "kostya"))
        assertEquals(listOf("Glass Harbor" to 100), requests(catalog, "john"))
    }

    @Test
    fun waitingMatchHidesPairAndDeclinedDoesNot() {
        val waiting = miniCatalog(matches = listOf(StoredMatch("m1", "req_north", "ira", "ожидает")))
        assertEquals(listOf("Борис" to 52), pairs(waiting, "anna"))
        val declined = miniCatalog(matches = listOf(StoredMatch("m2", "req_north", "ira", "отклонён")))
        assertEquals(listOf("Ира" to 100, "Борис" to 52), pairs(declined, "anna"))
    }

    @Test
    fun sameScorePutsMoreExperiencedReaderFirst() {
        val ira = reader("ira", "Нет опыта", "Русский", 100_000, true, "бесплатно", listOf("fantasy", "detective"), listOf("plot", "characters"))
        val vera = reader("vera", "Больше 3 лет", "Русский", 100_000, true, "бесплатно", listOf("fantasy", "detective"), listOf("plot", "characters"))
        val catalog = miniCatalog(readers = listOf(ira, vera))
        assertEquals(listOf("Вера" to 100, "Ира" to 100), pairs(catalog, "anna"))
    }

    @Test
    fun paidOnlyMatchesPaidAndFreeDoesNotTakeBarter() {
        val catalog = miniCatalog()
        val house = catalog.works.first { it.id == "house" }
        val paidRequest = catalog.requests.first { it.id == "req_house" }
        val barterRequest = catalog.requests.first { it.id == "req_glass" }.copy(id = "req_barter_ru", workId = "north")
        val north = catalog.works.first { it.id == "north" }
        assertNull(MatchRules.percent(house, paidRequest, catalog.readers.first { it.userId == "ira" }, emptyList(), TODAY))
        assertNull(MatchRules.percent(north, barterRequest, catalog.readers.first { it.userId == "ira" }, emptyList(), TODAY))
    }

    @Test
    fun emptyGenreListsDropThatWeight() {
        val work = WorkCard("w", "writer", "Текст", 10_000, "Русский", emptyList())
        val request = RequestCard("r", "w", "бесплатно", "Открыт", "2026-12-20", emptyList())
        val reader = reader("r1", "Нет опыта", "Русский", 100_000, true, "бесплатно", emptyList(), emptyList())
        assertEquals(100, MatchRules.percent(work, request, reader, emptyList(), TODAY))
    }

    private fun pairs(catalog: MatchCatalog, userId: String): List<Pair<String, Int>> =
        MatchBoardBuilder.build(catalog, userId, TODAY).writerLines.map { it.counterpart to it.score }

    private fun requests(catalog: MatchCatalog, userId: String): List<Pair<String, Int>> =
        MatchBoardBuilder.build(catalog, userId, TODAY).readerLines.map { it.title to it.score }

    private fun miniCatalog(
        readers: List<ReaderCard> = defaultReaders(),
        matches: List<StoredMatch> = emptyList()
    ): MatchCatalog = MatchCatalog(
        readers = readers,
        works = listOf(
            WorkCard("north", "anna", "Северный свет", 40_000, "Русский", listOf("fantasy", "detective")),
            WorkCard("house", "oleg", "Тихий дом", 90_000, "Русский", listOf("romance")),
            WorkCard("glass", "mia", "Glass Harbor", 20_000, "English", listOf("scifi"))
        ),
        requests = listOf(
            RequestCard("req_north", "north", "бесплатно", "Открыт", "2026-12-20", listOf("plot", "characters")),
            RequestCard("req_closed", "north", "бесплатно", "Закрыт", "2026-12-20", listOf("plot", "characters")),
            RequestCard("req_old", "north", "бесплатно", "Открыт", "2026-10-01", listOf("plot", "characters")),
            RequestCard("req_house", "house", "платно", "Открыт", "2026-12-20", listOf("style")),
            RequestCard("req_glass", "glass", "бартер", "Открыт", "2026-12-20", listOf("plot"))
        ),
        matches = matches,
        names = mapOf(
            "anna" to "Анна",
            "oleg" to "Олег",
            "mia" to "Мия",
            "ira" to "Ира",
            "boris" to "Борис",
            "lena" to "Лена",
            "nina" to "Нина",
            "kostya" to "Костя",
            "john" to "Джон",
            "vera" to "Вера"
        ),
        genreNames = mapOf(
            "fantasy" to "Фэнтези",
            "detective" to "Детектив",
            "romance" to "Роман",
            "scifi" to "Научная фантастика"
        ),
        feedbackNames = mapOf(
            "plot" to "Сюжет",
            "characters" to "Персонажи",
            "style" to "Стиль"
        )
    )

    private fun defaultReaders(): List<ReaderCard> = listOf(
        reader("ira", "Больше 3 лет", "Русский", 100_000, true, "бесплатно", listOf("fantasy", "detective"), listOf("plot", "characters")),
        reader("boris", "До 1 года", "Русский", 50_000, true, "бартер", listOf("fantasy"), listOf("plot")),
        reader("lena", "1–3 года", "Русский", 120_000, true, "платно", listOf("romance"), listOf("style")),
        reader("nina", "Больше 3 лет", "Русский", 100_000, false, "бесплатно", listOf("fantasy", "detective"), listOf("plot", "characters")),
        reader("kostya", "1–3 года", "Русский", 10_000, true, "бесплатно", listOf("fantasy", "detective"), listOf("plot", "characters")),
        reader("john", "1–3 года", "English", 50_000, true, "бартер", listOf("scifi"), listOf("plot"))
    )

    private fun reader(
        id: String,
        experience: String,
        language: String,
        maxWords: Int,
        available: Boolean,
        cooperation: String,
        genres: List<String>,
        feedback: List<String>
    ) = ReaderCard(id, experience, language, maxWords, available, cooperation, genres, feedback)

    private companion object {
        const val TODAY = "2026-11-22"
    }
}
