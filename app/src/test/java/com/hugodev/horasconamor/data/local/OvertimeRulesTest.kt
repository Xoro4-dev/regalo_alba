package com.hugodev.horasconamor.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

class OvertimeRulesTest {
    @Test
    fun incrementAddsMinutes() {
        assertEquals(90, OvertimeRules.adjustedMinutes(currentMinutes = 60, delta = 30))
    }

    @Test
    fun decrementNeverProducesNegativeMinutes() {
        assertEquals(0, OvertimeRules.adjustedMinutes(currentMinutes = 15, delta = -30))
        assertEquals(0, OvertimeRules.adjustedMinutes(currentMinutes = 0, delta = -15))
    }

    @Test
    fun adjustmentDoesNotOverflowTheStoredMinuteRange() {
        assertEquals(
            Int.MAX_VALUE,
            OvertimeRules.adjustedMinutes(currentMinutes = Int.MAX_VALUE, delta = 1),
        )
    }
}
