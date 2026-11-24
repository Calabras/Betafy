package com.betafy.feature.notification.domain

fun unreadBadge(count: Int): String? = when {
    count <= 0 -> null
    count > 99 -> "99+"
    else -> count.toString()
}

fun notificationText(raw: String): String = raw.replace(". ", ", ").trim().trimEnd('.')
