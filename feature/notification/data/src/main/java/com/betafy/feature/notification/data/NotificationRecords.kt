package com.betafy.feature.notification.data

import com.betafy.feature.notification.domain.notificationText
import com.betafy.feature.notification.domain.model.InboxNotification

internal object NotificationRecords {
    fun parse(userId: String, rows: List<Pair<String, Map<*, *>>>): List<InboxNotification> {
        return rows.mapNotNull { (id, raw) ->
            if (id.isBlank()) return@mapNotNull null
            val owner = raw["user_id"]?.toString().orEmpty()
            if (owner.isNotBlank() && owner != userId) return@mapNotNull null
            val text = raw["text"]?.toString()?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
            InboxNotification(
                id = id,
                userId = owner.ifBlank { userId },
                type = raw["type"]?.toString()?.takeIf { it.isNotBlank() } ?: "notice",
                relatedEntityId = raw["related_entity_id"]?.toString()?.takeIf { it.isNotBlank() },
                text = text,
                isRead = flag(raw["is_read"])
            )
        }.sortedByDescending { it.id }
    }

    fun values(
        userId: String,
        type: String,
        text: String,
        relatedEntityId: String?
    ): Map<String, Any> {
        val stored = linkedMapOf<String, Any>(
            "user_id" to userId,
            "type" to type.take(TYPE_LIMIT),
            "text" to notificationText(text),
            "is_read" to false
        )
        if (!relatedEntityId.isNullOrBlank()) stored["related_entity_id"] = relatedEntityId
        return stored
    }

    private fun flag(value: Any?): Boolean = when (value) {
        is Boolean -> value
        is Number -> value.toInt() != 0
        is String -> value.equals("true", ignoreCase = true) || value == "1"
        else -> false
    }

    private const val TYPE_LIMIT = 30
}
