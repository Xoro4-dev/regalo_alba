package com.hugodev.horasconamor.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

object OvertimeCalendar {
    fun weekStart(date: LocalDate): LocalDate =
        date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    fun weekEnd(date: LocalDate): LocalDate = weekStart(date).plusDays(4)

    fun workdaysOfWeek(date: LocalDate): List<LocalDate> {
        val start = weekStart(date)
        return (0..4).map { start.plusDays(it.toLong()) }
    }

    fun isWorkday(date: LocalDate): Boolean =
        date.dayOfWeek != DayOfWeek.SATURDAY && date.dayOfWeek != DayOfWeek.SUNDAY

    fun weeklyMessageTier(minutes: Int): WeeklyMessageTier =
        when {
            minutes < 120 -> WeeklyMessageTier.UNDER_TWO_HOURS
            minutes <= 300 -> WeeklyMessageTier.TWO_TO_FIVE_HOURS
            else -> WeeklyMessageTier.OVER_FIVE_HOURS
        }

    fun formatDuration(minutes: Int): String {
        val safeMinutes = minutes.coerceAtLeast(0)
        val hours = safeMinutes / 60
        val remainingMinutes = safeMinutes % 60
        return when {
            hours == 0 -> "$remainingMinutes min"
            remainingMinutes == 0 -> "$hours h"
            else -> "$hours h $remainingMinutes min"
        }
    }

}

enum class WeeklyMessageTier {
    UNDER_TWO_HOURS,
    TWO_TO_FIVE_HOURS,
    OVER_FIVE_HOURS,
}
