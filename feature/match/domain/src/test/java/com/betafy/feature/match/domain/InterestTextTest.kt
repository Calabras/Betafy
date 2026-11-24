package com.betafy.feature.match.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class InterestTextTest {
    @Test
    fun namesThePersonAndTheNick() {
        assertEquals("Тобой заинтересовался Миа (@Миа)", interestText("Миа", "Миа"))
        assertEquals("Тобой заинтересовался Анна (@anna)", interestText("Анна", "anna"))
        assertEquals("Тобой заинтересовался Пользователь", interestText("", ""))
    }
}
