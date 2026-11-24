package com.betafy.feature.notification.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationRecordsTest {
    @Test
    fun keepsOwnRowsAndReadsFlag() {
        val rows = listOf(
            "-a" to mapOf(
                "user_id" to "me",
                "type" to "profile",
                "text" to "Профиль создан",
                "is_read" to false
            ),
            "-b" to mapOf(
                "user_id" to "other",
                "type" to "profile",
                "text" to "Чужое",
                "is_read" to false
            ),
            "-c" to mapOf(
                "user_id" to "me",
                "type" to "match",
                "related_entity_id" to "req",
                "text" to "Отклик отправлен",
                "is_read" to true
            )
        )
        val items = NotificationRecords.parse("me", rows)
        assertEquals(listOf("-c", "-a"), items.map { it.id })
        assertTrue(items.first().isRead)
        assertFalse(items.last().isRead)
        assertEquals("req", items.first().relatedEntityId)
    }

    @Test
    fun storedMapUsesSchemaKeys() {
        val values = NotificationRecords.values("me", "profile", "Готово. Спасибо.", null)
        assertEquals("me", values["user_id"])
        assertEquals("profile", values["type"])
        assertEquals("Готово, Спасибо", values["text"])
        assertEquals(false, values["is_read"])
        assertNull(values["related_entity_id"])
    }
}
