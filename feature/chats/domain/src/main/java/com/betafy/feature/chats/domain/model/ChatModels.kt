package com.betafy.feature.chats.domain.model

data class MatchLink(
    val id: String,
    val readerId: String,
    val initiatorId: String,
    val status: String,
    val createdAt: Long
)

data class StoredChat(
    val id: String,
    val matchId: String,
    val createdAt: Long
)

data class ChatPerson(
    val fullName: String,
    val username: String
)

data class ChatSummary(
    val id: String,
    val matchId: String,
    val peerId: String,
    val peerName: String,
    val peerUsername: String,
    val createdAt: Long,
    val lastMessage: ChatMessage? = null,
    val unread: Int = 0
)

data class ChatMessage(
    val id: String,
    val chatId: String,
    val senderId: String,
    val text: String,
    val sentAt: Long,
    val isRead: Boolean,
    val mine: Boolean
)
