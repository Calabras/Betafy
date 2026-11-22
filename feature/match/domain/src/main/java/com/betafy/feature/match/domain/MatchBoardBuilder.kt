package com.betafy.feature.match.domain

import com.betafy.feature.match.domain.model.MatchBoard
import com.betafy.feature.match.domain.model.MatchCatalog
import com.betafy.feature.match.domain.model.MatchLine
import com.betafy.feature.match.domain.model.ReaderCard
import com.betafy.feature.match.domain.model.RequestCard
import com.betafy.feature.match.domain.model.WorkCard

object MatchBoardBuilder {
    fun build(catalog: MatchCatalog, userId: String, today: String): MatchBoard {
        val reader = catalog.readers.firstOrNull { it.userId == userId }
        val ownWorkIds = catalog.works.filter { it.writerId == userId }.map { it.id }.toSet()
        val openOwn = catalog.requests.filter { it.workId in ownWorkIds && MatchRules.isOpen(it, today) }
        val writerLines = openOwn.flatMap { request ->
            val work = catalog.works.firstOrNull { it.id == request.workId } ?: return@flatMap emptyList()
            catalog.readers.mapNotNull { candidate ->
                line(catalog, work, request, candidate, today, writerSide = true)
            }
        }.sortedWith(writerOrder(catalog))
        val readerLines = if (reader == null) {
            emptyList()
        } else {
            catalog.requests.mapNotNull { request ->
                val work = catalog.works.firstOrNull { it.id == request.workId } ?: return@mapNotNull null
                if (work.writerId == userId) return@mapNotNull null
                line(catalog, work, request, reader, today, writerSide = false)
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
        writerSide: Boolean
    ): MatchLine? {
        val score = MatchRules.percent(work, request, reader, catalog.matches, today) ?: return null
        val counterpartId = if (writerSide) reader.userId else work.writerId
        return MatchLine(
            requestId = request.id,
            readerId = reader.userId,
            title = work.title.ifBlank { "Произведение" },
            counterpart = catalog.names[counterpartId]?.takeIf { it.isNotBlank() } ?: "Пользователь",
            score = score,
            caption = caption(catalog, work, request),
            writerSide = writerSide
        )
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
