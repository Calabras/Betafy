package com.betafy.feature.match.domain

import com.betafy.feature.match.domain.model.ReaderCard
import com.betafy.feature.match.domain.model.RequestCard
import com.betafy.feature.match.domain.model.StoredMatch
import com.betafy.feature.match.domain.model.WorkCard
import kotlin.math.round

object MatchRules {
    fun percent(
        work: WorkCard,
        request: RequestCard,
        reader: ReaderCard,
        matches: List<StoredMatch>,
        today: String
    ): Int? {
        val raw = rawPercent(work, request, reader, matches, today) ?: return null
        return round(raw).toInt().coerceIn(0, 100)
    }

    fun isOpen(request: RequestCard, today: String): Boolean {
        if (!request.status.trim().equals(OPEN, ignoreCase = true)) return false
        return !deadlinePassed(request.deadline, today)
    }

    fun experienceRank(level: String): Int {
        val text = level.trim().lowercase()
        return when {
            text.contains("больше") -> 3
            text.contains("1") && text.contains("3") -> 2
            text.contains("1") -> 1
            else -> 0
        }
    }

    private fun rawPercent(
        work: WorkCard,
        request: RequestCard,
        reader: ReaderCard,
        matches: List<StoredMatch>,
        today: String
    ): Double? {
        if (request.workId != work.id) return null
        if (!isOpen(request, today)) return null
        if (!reader.isAvailable) return null
        if (reader.userId == work.writerId) return null
        if (blocked(matches, request.id, reader.userId)) return null
        if (!sameLanguage(work.language, reader.preferredLanguage)) return null
        val volume = volumeFactor(work.wordCount, reader.maxWordCount)
        if (volume is Factor.Reject) return null
        val genres = coverage(work.genreIds, reader.genreIds)
        if (genres is Factor.Reject) return null
        val feedback = coverage(request.feedbackTypeIds, reader.feedbackTypeIds)
        if (feedback is Factor.Reject) return null
        val cooperation = cooperationFactor(request.cooperationType, reader.cooperationType)
        if (cooperation is Factor.Reject) return null
        var weighted = 0.0
        var used = 0.0
        fun take(weight: Double, factor: Factor) {
            if (factor is Factor.Value) {
                weighted += weight * factor.score
                used += weight
            }
        }
        take(GENRE_WEIGHT, genres)
        take(FEEDBACK_WEIGHT, feedback)
        take(COOPERATION_WEIGHT, cooperation)
        take(VOLUME_WEIGHT, volume)
        if (used == 0.0) return null
        return weighted / used * 100.0
    }

    private fun blocked(matches: List<StoredMatch>, requestId: String, readerId: String): Boolean =
        matches.any { match ->
            match.requestId == requestId &&
                match.readerId == readerId &&
                blocks(match.status)
        }

    private fun blocks(status: String): Boolean {
        val value = status.trim().lowercase()
        return value == WAITING || value == ACCEPTED
    }

    private fun sameLanguage(workLanguage: String, readerLanguage: String): Boolean {
        val left = workLanguage.trim()
        val right = readerLanguage.trim()
        if (left.isEmpty() || right.isEmpty()) return false
        return left.equals(right, ignoreCase = true)
    }

    private fun deadlinePassed(deadline: String, today: String): Boolean {
        val day = deadline.trim()
        if (day.length < 10 || day[4] != '-' || day[7] != '-') return false
        return day.substring(0, 10) < today
    }

    private fun coverage(required: List<String>, offered: List<String>): Factor {
        val need = required.map { it.trim() }.filter { it.isNotEmpty() }.toSet()
        val have = offered.map { it.trim() }.filter { it.isNotEmpty() }.toSet()
        if (need.isEmpty() || have.isEmpty()) return Factor.Skip
        val hit = need.intersect(have).size
        if (hit == 0) return Factor.Reject
        return Factor.Value(hit.toDouble() / need.size)
    }

    private fun cooperationFactor(requestType: String, readerType: String): Factor {
        val request = cooperation(requestType)
        val reader = cooperation(readerType)
        if (request.isEmpty() || reader.isEmpty()) return Factor.Skip
        if (request == PAID || reader == PAID) {
            return if (request == reader) Factor.Value(1.0) else Factor.Reject
        }
        if (request == reader) return Factor.Value(1.0)
        if (reader == BARTER && request == FREE) return Factor.Value(0.5)
        return Factor.Reject
    }

    private fun volumeFactor(wordCount: Int, maxWordCount: Int): Factor {
        if (wordCount <= 0 || maxWordCount <= 0) return Factor.Skip
        if (wordCount > maxWordCount) return Factor.Reject
        val ratio = wordCount.toDouble() / maxWordCount
        val fit = when {
            ratio <= 0.5 -> 1.0
            ratio <= 0.8 -> 0.7
            else -> 0.4
        }
        return Factor.Value(fit)
    }

    private fun cooperation(value: String): String = when (value.trim().lowercase()) {
        BARTER, "barter" -> BARTER
        FREE, "free" -> FREE
        PAID, "paid" -> PAID
        else -> ""
    }

    private sealed interface Factor {
        data object Skip : Factor
        data object Reject : Factor
        data class Value(val score: Double) : Factor
    }

    private const val GENRE_WEIGHT = 45.0
    private const val FEEDBACK_WEIGHT = 30.0
    private const val COOPERATION_WEIGHT = 15.0
    private const val VOLUME_WEIGHT = 10.0
    private const val OPEN = "открыт"
    private const val WAITING = "ожидает"
    private const val ACCEPTED = "принят"
    private const val BARTER = "бартер"
    private const val FREE = "бесплатно"
    private const val PAID = "платно"
}
