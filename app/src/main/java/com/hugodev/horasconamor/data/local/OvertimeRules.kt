package com.hugodev.horasconamor.data.local

object OvertimeRules {
    fun adjustedMinutes(currentMinutes: Int, delta: Int): Int =
        (currentMinutes.toLong() + delta).coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()
}
