package com.betafy.feature.match.domain

import com.betafy.feature.match.domain.model.MatchBoard
import com.betafy.feature.match.domain.model.MatchCatalog
import com.betafy.feature.match.domain.model.MatchLine
import com.betafy.feature.match.domain.model.MatchTrait
import com.betafy.feature.match.domain.model.ReaderCard
import com.betafy.feature.match.domain.model.RequestCard
import com.betafy.feature.match.domain.model.WorkCard

object MatchBoardBuilder {
    fun build(catalog: MatchCatalog, userId: String, today: String, now: Long = 0L): MatchBoard {
        val reader = catalog.readers.firstOrNull { it.userId == userId }
        val ownWorkIds = catalog.works.filter { it.writerId == userId }.map { it.id }.toSet()
        val openOwn = catalog.requests.filter { it.workId in ownWorkIds && MatchRules.isOpen(it, today) }
        val writerLines = openOwn.flatMap { request ->
            val work = catalog.works.firstOrNull { it.id == request.workId } ?: return@flatMap emptyList()
            catalog.readers.mapNotNull { candidate ->
                line(catalog, work, request, candidate, today, now, writerSide = true)
            }
        }.sortedWith(writerOrder(catalog))
        val readerLines = if (reader == null) {
            emptyList()
        } else {
            catalog.requests.mapNotNull { request ->
                val work = catalog.works.firstOrNull { it.id == request.workId } ?: return@mapNotNull null
                if (work.writerId == userId) return@mapNotNull null
                line(catalog, work, request, reader, today, now, writerSide = false)
            }.sortedWith(compareByDescending<MatchLine> { it.score }.thenBy { it.title })
        }
        return MatchBoard(
            hasReaderProfile = reader != null,
            hasOpenRequests = openOwn.isNotEmpty(),
            writerLines = writerLines,
            readerLines = readerLines
        )
    }

    private fun line(
        catalog: MatchCatalog,
        work: WorkCard,
        request: RequestCard,
        reader: ReaderCard,
        today: String,
        now: Long,
        writerSide: Boolean
    ): MatchLine? {
        val score = MatchRules.percent(work, request, reader, catalog.matches, today, now) ?: return null
        val counterpartId = if (writerSide) reader.userId else work.writerId
        val person = catalog.people[counterpartId]
        val username = person?.username?.takeIf { it.isNotBlank() }
            ?: catalog.names[counterpartId].orEmpty()
        val firstName = person?.firstName.orEmpty()
        val lastName = person?.lastName.orEmpty()
        val visible = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")
            .ifBlank { username }
            .ifBlank { "Пользователь" }
        return MatchLine(
            requestId = request.id,
            readerId = reader.userId,
            title = work.title.ifBlank { "Произведение" },
            counterpart = visible,
            score = score,
            caption = caption(catalog, work, request),
            writerSide = writerSide,
            username = username,
            firstName = firstName,
            lastName = lastName,
            wordCount = work.wordCount,
            traits = if (writerSide) traits(catalog, work, request, reader) else emptyList()
        )
    }

    private fun traits(
        catalog: MatchCatalog,
        work: WorkCard,
        request: RequestCard,
        reader: ReaderCard
    ): List<MatchTrait> {
        val workGenres = work.genreIds.map { it.trim() }.filter { it.isNotEmpty() }.toSet()
        val readerGenres = reader.genreIds.map { it.trim() }.filter { it.isNotEmpty() }
        val requestFeedback = request.feedbackTypeIds.map { it.trim() }.filter { it.isNotEmpty() }.toSet()
        val readerFeedback = reader.feedbackTypeIds.map { it.trim() }.filter { it.isNotEmpty() }
        val shared = mutableListOf<MatchTrait>()
        val rest = mutableListOf<MatchTrait>()
        readerGenres.forEach { id ->
            val name = catalog.genreNames[id]?.takeIf { it.isNotBlank() } ?: id
            val trait = MatchTrait("Жанр: $name", id in workGenres)
            if (trait.shared) shared += trait else rest += trait
        }
        val language = reader.preferredLanguage.trim()
        if (language.isNotEmpty()) {
            val same = language.equals(work.language.trim(), ignoreCase = true)
            val trait = MatchTrait("Язык: $language", same)
            if (trait.shared) shared += trait else rest += trait
        }
        if (reader.maxWordCount > 0) {
            val fits = work.wordCount in 1..reader.maxWordCount
            val trait = MatchTrait("Берёт до ${reader.maxWordCount} слов", fits)
            if (trait.shared) shared += trait else rest += trait
        }
        val cooperation = reader.cooperationType.trim()
        if (cooperation.isNotEmpty()) {
            val same = cooperation.equals(request.cooperationType.trim(), ignoreCase = true)
            val trait = MatchTrait("Формат: $cooperation", same)
            if (trait.shared) shared += trait else rest += trait
        }
        readerFeedback.forEach { id ->
            val name = catalog.feedbackNames[id]?.takeIf { it.isNotBlank() } ?: id
            val trait = MatchTrait("Интерес: $name", id in requestFeedback)
            if (trait.shared) shared += trait else rest += trait
        }
        val experience = reader.experienceLevel.trim()
        if (experience.isNotEmpty()) {
            rest += MatchTrait("Опыт: $experience", false)
        }
        return shared + rest
    }

    private fun caption(catalog: MatchCatalog, work: WorkCard, request: RequestCard): String {
        val genres = work.genreIds.map { catalog.genreNames[it] ?: it }.filter { it.isNotBlank() }
        val feedback = request.feedbackTypeIds.map { catalog.feedbackNames[it] ?: it }.filter { it.isNotBlank() }
        return listOf(
            genres.joinToString(", "),
            request.cooperationType.trim(),
            if (work.wordCount > 0) "${work.wordCount} слов" else "",
            feedback.joinToString(", ")
        ).filter { it.isNotBlank() }.joinToString(" · ")
    }

    private fun writerOrder(catalog: MatchCatalog): Comparator<MatchLine> =
        compareByDescending<MatchLine> { it.score }
            .thenByDescending { line ->
                val reader = catalog.readers.firstOrNull { it.userId == line.readerId }
                MatchRules.experienceRank(reader?.experienceLevel.orEmpty())
            }
            .thenBy { it.counterpart }
}
