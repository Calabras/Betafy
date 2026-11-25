package com.betafy.feature.chats.data

import com.betafy.feature.chats.domain.ChatBoard
import com.betafy.feature.chats.domain.model.ChatMessage
import com.betafy.feature.chats.domain.model.ChatPerson
import com.betafy.feature.chats.domain.model.ChatSummary
import com.betafy.feature.chats.domain.model.MatchLink
import com.betafy.feature.chats.domain.model.StoredChat

internal object ChatRecords {
    fun summaries(
        userId: String,
        chats: Map<String, Map<*, *>>,
        matches: Map<String, Map<*, *>>,
        users: Map<String, Map<*, *>>
    ): List<ChatSummary> {
        val stored = chats.mapNotNull { (id, raw) -> chat(id, raw) }
        val links = matches.mapNotNull { (id, raw) -> match(id, raw) }
        val people = users.mapNotNull { (id, raw) ->
            val person = person(raw) ?: return@mapNotNull null
            id to person
        }.toMap()
        val digest = digest(userId, matches)
        return ChatBoard.rows(userId, stored, links, people, digest.first, digest.second)
    }

    private fun digest(
        userId: String,
        matches: Map<String, Map<*, *>>
    ): Pair<Map<String, ChatMessage>, Map<String, Int>> {
        val latest = mutableMapOf<String, ChatMessage>()
        val unread = mutableMapOf<String, Int>()
        matches.forEach { (matchId, raw) ->
            val nested = raw["messages"] as? Map<*, *> ?: return@forEach
            val rows = nested.mapNotNull { (id, value) ->
                val message = value as? Map<*, *> ?: return@mapNotNull null
                val key = id?.toString()?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                key to message
            }
            val parsed = messages(userId, matchId, rows)
            val last = parsed.maxByOrNull { it.sentAt } ?: return@forEach
            latest[matchId] = last
            val count = parsed.count { !it.mine && !it.isRead }
            if (count > 0) unread[matchId] = count
        }
        return latest to unread
    }

    fun messages(userId: String, chatId: String, rows: List<Pair<String, Map<*, *>>>): List<ChatMessage> =
        rows.mapNotNull { (id, raw) -> message(id, chatId, userId, raw) }
            .sortedBy { it.sentAt }

    fun chatValues(matchId: String, createdAt: Long): Map<String, Any> = mapOf(
        "matchId" to matchId,
        "createdAt" to createdAt
    )

    fun messageValues(chatId: String, senderId: String, text: String, sentAt: Long): Map<String, Any> = mapOf(
        "chatId" to chatId,
        "senderId" to senderId,
        "text" to text,
        "sentAt" to sentAt,
        "isRead" to false
    )

    private fun chat(id: String, raw: Map<*, *>): StoredChat {
        val matchId = text(raw, "matchId", "match_id").ifBlank { id }
        return StoredChat(id = id, matchId = matchId, createdAt = long(raw, "createdAt", "created_at"))
    }

    private fun match(id: String, raw: Map<*, *>): MatchLink? {
        val readerId = text(raw, "readerId", "reader_id")
        val initiatorId = text(raw, "initiatorId", "initiator_id")
        if (readerId.isBlank() && initiatorId.isBlank()) return null
        return MatchLink(
            id = id,
            readerId = readerId,
            initiatorId = initiatorId,
            status = text(raw, "status"),
            createdAt = long(raw, "createdAt", "created_at")
        )
    }

    private fun person(raw: Map<*, *>): ChatPerson? {
        val username = text(raw, "username")
        val fullName = text(raw, "fullName", "full_name").ifBlank {
            listOf(text(raw, "firstName", "first_name"), text(raw, "lastName", "last_name"))
                .filter { it.isNotBlank() }
                .joinToString(" ")
        }
        if (username.isBlank() && fullName.isBlank()) return null
        return ChatPerson(fullName = fullName, username = username)
    }

    private fun message(id: String, chatId: String, userId: String, raw: Map<*, *>): ChatMessage? {
        val body = text(raw, "text")
        val senderId = text(raw, "senderId", "sender_id")
        if (body.isBlank() || senderId.isBlank()) return null
        return ChatMessage(
            id = id,
            chatId = text(raw, "chatId", "chat_id").ifBlank { chatId },
            senderId = senderId,
            text = body,
            sentAt = long(raw, "sentAt", "sent_at"),
            isRead = flag(raw, "isRead", "is_read"),
            mine = senderId == userId
        )
    }

    private fun text(raw: Map<*, *>, vararg keys: String): String =
        keys.firstNotNullOfOrNull { key -> raw[key]?.toString()?.takeIf { it.isNotBlank() } }.orEmpty()

    private fun long(raw: Map<*, *>, vararg keys: String): Long {
        val value = keys.firstNotNullOfOrNull { raw[it] } ?: return 0L
        return when (value) {
            is Long -> value
            is Int -> value.toLong()
            is Double -> value.toLong()
            is String -> value.toLongOrNull() ?: 0L
            else -> 0L
        }
    }

    private fun flag(raw: Map<*, *>, vararg keys: String): Boolean {
        val value = keys.firstNotNullOfOrNull { raw[it] } ?: return false
        return when (value) {
            is Boolean -> value
            is Number -> value.toInt() != 0
            is String -> value.equals("true", ignoreCase = true)
            else -> false
        }
    }
}
