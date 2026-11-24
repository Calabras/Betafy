package com.betafy.feature.match.data

import com.betafy.feature.match.domain.model.MatchCatalog
import com.betafy.feature.match.domain.model.PersonName
import com.betafy.feature.match.domain.model.ReaderCard
import com.betafy.feature.match.domain.model.RequestCard
import com.betafy.feature.match.domain.model.StoredMatch
import com.betafy.feature.match.domain.model.WorkCard

object MatchSnapshotParser {
    fun parse(
        betaReaders: Map<String, Any?>,
        appReaders: Map<String, Any?>,
        readerGenres: Map<String, Any?>,
        readerFeedback: Map<String, Any?>,
        works: Map<String, Any?>,
        workGenres: Map<String, Any?>,
        requests: Map<String, Any?>,
        requestFeedback: Map<String, Any?>,
        matches: Map<String, Any?>,
        users: Map<String, Any?>,
        genres: Map<String, Any?>,
        feedbackTypes: Map<String, Any?>
    ): MatchCatalog {
        val ids = (betaReaders.keys + appReaders.keys).filter { it.isNotBlank() }
        val readers = ids.mapNotNull { id ->
            val schema = betaReaders[id].asRecord()
            val app = appReaders[id].asRecord()
            val raw = schema ?: app ?: return@mapNotNull null
            val fallback = if (schema != null) app else null
            ReaderCard(
                userId = raw["user_id"]?.toString().takeUnless { it.isNullOrBlank() } ?: id,
                experienceLevel = text(raw, fallback, "experience_level"),
                preferredLanguage = text(raw, fallback, "preferred_language"),
                maxWordCount = raw["max_word_count"].asInt().takeIf { it > 0 } ?: fallback?.get("max_word_count").asInt(),
                isAvailable = when (raw["is_available"]) {
                    null -> fallback?.get("is_available").asBoolean()
                    else -> raw["is_available"].asBoolean()
                },
                cooperationType = text(raw, fallback, "cooperation_type"),
                genreIds = links(readerGenres, id).ifEmpty { raw["genre_ids"].asStringList().ifEmpty { fallback?.get("genre_ids").asStringList() } },
                feedbackTypeIds = links(readerFeedback, id).ifEmpty {
                    raw["feedback_type_ids"].asStringList().ifEmpty { fallback?.get("feedback_type_ids").asStringList() }
                }
            )
        }
        val workCards = works.mapNotNull { (id, value) ->
            val raw = value.asRecord() ?: return@mapNotNull null
            WorkCard(
                id = id,
                writerId = raw["writer_id"]?.toString().orEmpty(),
                title = raw["title"]?.toString().orEmpty(),
                wordCount = raw["word_count"].asInt(),
                language = raw["language"]?.toString().orEmpty(),
                genreIds = links(workGenres, id).ifEmpty { raw["genre_ids"].asStringList() }
            )
        }
        val requestCards = requests.mapNotNull { (id, value) ->
            val raw = value.asRecord() ?: return@mapNotNull null
            RequestCard(
                id = id,
                workId = raw["work_id"]?.toString().orEmpty(),
                cooperationType = raw["cooperation_type"]?.toString().orEmpty(),
                status = raw["status"]?.toString().orEmpty(),
                deadline = raw["deadline"]?.toString().orEmpty(),
                feedbackTypeIds = links(requestFeedback, id).ifEmpty { raw["feedback_type_ids"].asStringList() }
            )
        }
        val stored = matches.mapNotNull { (id, value) ->
            val raw = value.asRecord() ?: return@mapNotNull null
            StoredMatch(
                id = id,
                requestId = raw["request_id"]?.toString().orEmpty(),
                readerId = raw["reader_id"]?.toString().orEmpty(),
                status = raw["status"]?.toString().orEmpty(),
                initiatorId = raw["initiator_id"]?.toString().orEmpty(),
                createdAt = raw["created_at"].asLong()
            )
        }
        return MatchCatalog(
            readers = readers,
            works = workCards,
            requests = requestCards,
            matches = stored,
            names = names(users),
            people = people(users),
            genreNames = catalogNames(genres),
            feedbackNames = catalogNames(feedbackTypes)
        )
    }

    private fun text(primary: Map<String, Any?>, fallback: Map<String, Any?>?, key: String): String =
        primary[key]?.toString()?.takeIf { it.isNotBlank() }
            ?: fallback?.get(key)?.toString().orEmpty()

    private fun links(node: Map<String, Any?>, parentId: String): List<String> {
        val raw = node[parentId] ?: return emptyList()
        return when (raw) {
            is Map<*, *> -> raw.entries.mapNotNull { (key, value) ->
                if (value != true || key == null) null else key.toString()
            }.filter { it.isNotBlank() }
            else -> raw.asStringList()
        }
    }

    private fun names(users: Map<String, Any?>): Map<String, String> =
        people(users).mapValues { (_, person) ->
            person.username.ifBlank {
                listOf(person.firstName, person.lastName).filter { it.isNotBlank() }.joinToString(" ")
            }
        }.filterValues { it.isNotBlank() }

    private fun people(users: Map<String, Any?>): Map<String, PersonName> =
        users.mapNotNull { (id, value) ->
            when (value) {
                is String -> id to PersonName(username = value)
                is Map<*, *> -> {
                    val username = value["username"]?.toString().orEmpty()
                    val full = value["fullName"]?.toString()?.trim().orEmpty()
                        .ifBlank { value["full_name"]?.toString()?.trim().orEmpty() }
                    val parts = full.split(Regex("\\s+")).filter { it.isNotBlank() }
                    val first = value["first_name"]?.toString()?.trim().orEmpty()
                        .ifBlank { value["firstName"]?.toString()?.trim().orEmpty() }
                        .ifBlank { parts.firstOrNull().orEmpty() }
                    val last = value["last_name"]?.toString()?.trim().orEmpty()
                        .ifBlank { value["lastName"]?.toString()?.trim().orEmpty() }
                        .ifBlank { parts.drop(1).joinToString(" ") }
                    if (username.isBlank() && first.isBlank() && last.isBlank()) {
                        null
                    } else {
                        id to PersonName(username = username, firstName = first, lastName = last)
                    }
                }
                else -> null
            }
        }.toMap()

    private fun catalogNames(node: Map<String, Any?>): Map<String, String> =
        node.mapNotNull { (id, value) ->
            val name = when (value) {
                is String -> value
                is Map<*, *> -> value["name"]?.toString()
                else -> null
            }?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
            id to name
        }.toMap()
}

private fun Any?.asRecord(): Map<String, Any?>? {
    val raw = this as? Map<*, *> ?: return null
    return raw.entries.mapNotNull { (key, value) ->
        key?.toString()?.let { it to value }
    }.toMap()
}

internal fun Any?.asNodeMap(): Map<String, Any?> = asRecord().orEmpty()

internal fun Any?.asLong(): Long = when (this) {
    is Long -> this
    is Int -> toLong()
    is Double -> toLong()
    is String -> toLongOrNull() ?: 0L
    else -> 0L
}

internal fun Any?.asInt(): Int = when (this) {
    is Int -> this
    is Long -> toInt()
    is Double -> toInt()
    is String -> toIntOrNull() ?: 0
    else -> 0
}

internal fun Any?.asBoolean(): Boolean = when (this) {
    is Boolean -> this
    is String -> equals("true", ignoreCase = true)
    is Number -> toInt() != 0
    else -> false
}

internal fun Any?.asStringList(): List<String> = when (this) {
    is List<*> -> mapNotNull { it?.toString() }.filter { it.isNotBlank() }
    is Map<*, *> -> entries.sortedBy { it.key?.toString() }.mapNotNull { it.value?.toString() }.filter { it.isNotBlank() }
    else -> emptyList()
}
