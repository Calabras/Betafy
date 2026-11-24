package com.betafy.feature.match.domain

const val SWIPE_WINDOW_MILLIS = 48L * 60L * 60L * 1000L

fun swipeStillHidden(atMillis: Long, nowMillis: Long): Boolean =
    nowMillis - atMillis < SWIPE_WINDOW_MILLIS
