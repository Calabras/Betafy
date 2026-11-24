package com.betafy.feature.match.domain

interface SwipeLog {
    fun remember(userId: String, key: String, atMillis: Long)
    fun hidden(userId: String, nowMillis: Long): Set<String>
}
