package com.betafy.feature.chats.domain

import com.betafy.feature.chats.domain.model.ChatPerson
import com.betafy.feature.chats.domain.model.MatchLink
import com.betafy.feature.chats.domain.model.StoredChat
import org.junit.Assert.assertEquals
import org.junit.Test

class ChatBoardTest {
    @Test
    fun readerSeesInitiatorAndWriterSeesReader() {
        val rows = ChatBoard.rows("reader", chats(), matches(), people())
        assertEquals(listOf("writer"), rows.map { it.peerId })
        assertEquals("Анна Иванова", rows.first().peerName)
        assertEquals("anna", rows.first().peerUsername)

        val writerRows = ChatBoard.rows("writer", chats(), matches(), people())
        assertEquals("reader", writerRows.first().peerId)
        assertEquals("gleb", writerRows.first().peerName)
    }

    @Test
    fun hidesForeignDeclinedAndSelf() {
        val matches = listOf(
            link("foreign", "someone", "else", "ожидает", 10),
            link("declined", "reader", "writer", "отклонён", 20),
            link("self", "reader", "reader", "принят", 30),
            link("ok", "reader", "writer", "принят", 5)
        )
        val rows = ChatBoard.rows("reader", emptyList(), matches, people())
        assertEquals(listOf("ok"), rows.map { it.matchId })
    }

    @Test
    fun storedChatKeepsItsIdAndNewerMatchComesFirst() {
        val matches = listOf(
            link("old", "reader", "writer", "ожидает", 10),
            link("new", "reader", "other", "ожидает", 50)
        )
        val people = people() + ("other" to ChatPerson("", "other"))
        val chats = listOf(StoredChat(id = "chat-old", matchId = "old", createdAt = 40))
        val rows = ChatBoard.rows("reader", chats, matches, people)
        assertEquals(listOf("new", "old"), rows.map { it.matchId })
        assertEquals("chat-old", rows.first { it.matchId == "old" }.id)
        assertEquals(40L, rows.first { it.matchId == "old" }.createdAt)
        assertEquals("other", rows.first { it.matchId == "new" }.peerName)
    }

    private fun chats() = listOf(StoredChat("chat-1", "match-1", 15))

    private fun matches() = listOf(link("match-1", "reader", "writer", "ожидает", 10))

    private fun people() = mapOf(
        "writer" to ChatPerson("Анна Иванова", "anna"),
        "reader" to ChatPerson("", "gleb")
    )

    private fun link(id: String, reader: String, initiator: String, status: String, createdAt: Long) =
        MatchLink(id, reader, initiator, status, createdAt)
}
