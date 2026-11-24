package com.betafy.feature.notification.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NotificationTextTest {
    @Test
    fun badgeGrowsUntilNinetyNine() {
        assertNull(unreadBadge(0))
        assertNull(unreadBadge(-1))
        assertEquals("1", unreadBadge(1))
        assertEquals("99", unreadBadge(99))
        assertEquals("99+", unreadBadge(100))
        assertEquals("99+", unreadBadge(240))
    }

    @Test
    fun pushIdKeepsCreationTime() {
        assertEquals(1732467600000L, notificationMillis("-OCU6Pe-"))
        assertNull(notificationMillis("abc"))
        assertNull(notificationMillis(""))
    }

    fun textUsesCommasInsteadOfPeriods() {
        assertEquals(
            "Профиль создан, Можно продолжить",
            notificationText("Профиль создан. Можно продолжить.")
        )
    }
}
