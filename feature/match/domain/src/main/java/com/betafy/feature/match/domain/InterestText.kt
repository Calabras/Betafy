package com.betafy.feature.match.domain

fun interestText(name: String, username: String): String {
    val visible = name.trim().ifBlank { username.trim() }
    val nick = username.trim()
    val who = when {
        visible.isNotBlank() && nick.isNotBlank() -> "$visible (@$nick)"
        visible.isNotBlank() -> visible
        nick.isNotBlank() -> "@$nick"
        else -> "Пользователь"
    }
    return "Уважаемый ридер, тобой заинтересовался писатель $who"
}
