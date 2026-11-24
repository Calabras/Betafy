package com.betafy.feature.match.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class InterestTextTest {
    @Test
    fun namesThePersonAndTheNick() {
        assertEquals("Уважаемый ридер, тобой заинтересовался писатель Миа (@Миа)", interestText("Миа", "Миа"))
        assertEquals("Уважаемый ридер, тобой заинтересовался писатель Анна (@anna)", interestText("Анна", "anna"))
        assertEquals("Уважаемый ридер, тобой заинтересовался писатель Пользователь", interestText("", ""))
    }
}
