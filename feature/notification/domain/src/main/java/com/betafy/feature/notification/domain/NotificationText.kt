package com.betafy.feature.notification.domain

fun unreadBadge(count: Int): String? = when {
    count <= 0 -> null
    count > 99 -> "99+"
    else -> count.toString()
}

fun notificationText(raw: String): String = raw.replace(". ", ", ").trim().trimEnd('.')

fun notificationMillis(id: String): Long? {
    if (id.length < 8) return null
    var timestamp = 0L
    for (index in 0 until 8) {
        val digit = PUSH_CHARS.indexOf(id[index])
        if (digit < 0) return null
        timestamp = timestamp * 64 + digit
    }
    if (timestamp < 1_500_000_000_000L) return null
    return timestamp
}

private const val PUSH_CHARS = "-0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz"
