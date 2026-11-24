package com.betafy.feature.match.domain

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SwipeWindowTest {
    @Test
    fun swipeStaysHiddenForTwoDays() {
        val at = 1_700_000_000_000L
        assertTrue(swipeStillHidden(at, at + SWIPE_WINDOW_MILLIS - 1))
        assertFalse(swipeStillHidden(at, at + SWIPE_WINDOW_MILLIS))
    }
}
