package com.betafy.feature.chats.domain

import com.betafy.feature.chats.domain.model.ChatMessage
import com.betafy.feature.chats.domain.model.ChatPerson
import com.betafy.feature.chats.domain.model.ChatSummary
import com.betafy.feature.chats.domain.model.MatchLink
import com.betafy.feature.chats.domain.model.StoredChat

object ChatBoard {
    fun rows(
        userId: String,
        chats: List<StoredChat>,
        matches: List<MatchLink>,
        people: Map<String, ChatPerson>,
        previews: Map<String, ChatMessage> = emptyMap(),
        unread: Map<String, Int> = emptyMap()
    ): List<ChatSummary> {
        if (userId.isBlank()) return emptyList()
        return matches.mapNotNull { match ->
            row(userId, match, chats, people, previews[match.id], unread[match.id] ?: 0)
        }
            .distinctBy { it.matchId }
            .sortedWith(
                compareByDescending<ChatSummary> { it.lastMessage?.sentAt ?: it.createdAt }
                    .thenBy { it.peerName }
            )
    }

    private fun row(
        userId: String,
        match: MatchLink,
        chats: List<StoredChat>,
        people: Map<String, ChatPerson>,
        lastMessage: ChatMessage?,
        unread: Int
    ): ChatSummary? {
        if (match.id.isBlank() || declined(match.status)) return null
        val peerId = when (userId) {
            match.readerId -> match.initiatorId
            match.initiatorId -> match.readerId
            else -> return null
        }
        if (peerId.isBlank() || peerId == userId) return null
        val stored = chats.firstOrNull { it.matchId == match.id || it.id == match.id }
        val person = people[peerId]
        val username = person?.username?.trim().orEmpty()
        val name = person?.fullName?.trim().orEmpty().ifBlank { username }.ifBlank { "Пользователь" }
        val createdAt = stored?.createdAt?.takeIf { it > 0 } ?: match.createdAt
        return ChatSummary(
            id = stored?.id?.takeIf { it.isNotBlank() } ?: match.id,
            matchId = match.id,
            peerId = peerId,
            peerName = name,
            peerUsername = username,
            createdAt = createdAt,
            lastMessage = lastMessage,
            unread = unread.coerceAtLeast(0)
        )
    }

    private fun declined(status: String): Boolean {
        val value = status.trim().lowercase()
        return value.contains("отклон") || value == "declined" || value == "rejected"
    }
}
