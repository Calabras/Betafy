package com.betafy.feature.chats.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatRecordsTest {
    @Test
    fun readsCamelAndSnakeMatchFields() {
        val chats = mapOf(
            "chat-1" to mapOf("matchId" to "north", "createdAt" to 20)
        )
        val matches = mapOf(
            "north" to mapOf(
                "reader_id" to "reader",
                "initiator_id" to "writer",
                "status" to "ожидает",
                "created_at" to 10
            )
        )
        val users = mapOf(
            "writer" to mapOf("fullName" to "Анна Иванова", "username" to "anna")
        )
        val rows = ChatRecords.summaries("reader", chats, matches, users)
        assertEquals(1, rows.size)
        assertEquals("chat-1", rows.first().id)
        assertEquals("Анна Иванова", rows.first().peerName)
        assertEquals("anna", rows.first().peerUsername)
        assertEquals(20L, rows.first().createdAt)
        assertEquals(null, rows.first().lastMessage)
        assertEquals(0, rows.first().unread)
    }

    @Test
    fun summaryKeepsLatestMessageTimeAndReadState() {
        val matches = mapOf(
            "north" to mapOf(
                "readerId" to "reader",
                "initiatorId" to "writer",
                "status" to "ожидает",
                "messages" to mapOf(
                    "early" to mapOf("senderId" to "writer", "text" to "Первое", "sentAt" to 5, "isRead" to true),
                    "late" to mapOf("senderId" to "reader", "text" to "Позже", "sentAt" to 9, "isRead" to false)
                )
            )
        )
        val users = mapOf("writer" to mapOf("username" to "anna"))
        val row = ChatRecords.summaries("reader", emptyMap(), matches, users).single()
        assertEquals("Позже", row.lastMessage?.text)
        assertEquals(9L, row.lastMessage?.sentAt)
        assertEquals(true, row.lastMessage?.mine)
        assertEquals(false, row.lastMessage?.isRead)
        assertEquals(0, row.unread)
    }

    @Test
    fun summaryCountsIncomingUnread() {
        val matches = mapOf(
            "north" to mapOf(
                "readerId" to "reader",
                "initiatorId" to "writer",
                "status" to "ожидает",
                "messages" to mapOf(
                    "a" to mapOf("senderId" to "writer", "text" to "Раз", "sentAt" to 1, "isRead" to false),
                    "b" to mapOf("senderId" to "writer", "text" to "Два", "sentAt" to 2, "isRead" to true),
                    "c" to mapOf("senderId" to "reader", "text" to "Три", "sentAt" to 3, "isRead" to false)
                )
            )
        )
        val row = ChatRecords.summaries("reader", emptyMap(), matches, emptyMap()).single()
        assertEquals(1, row.unread)
        assertEquals("Три", row.lastMessage?.text)
    }

    @Test
    fun messageKeepsOwnAndIncomingApart() {
        val rows = ChatRecords.messages(
            "reader",
            "chat-1",
            listOf(
                "m1" to mapOf("senderId" to "writer", "text" to "Привет", "sentAt" to 2, "isRead" to false),
                "m2" to mapOf("sender_id" to "reader", "text" to "И тебе", "sent_at" to 1, "is_read" to true)
            )
        )
        assertEquals(listOf("m2", "m1"), rows.map { it.id })
        assertTrue(rows.first().mine)
        assertEquals(false, rows.last().isRead)
        assertEquals(false, rows.last().mine)
    }
}
