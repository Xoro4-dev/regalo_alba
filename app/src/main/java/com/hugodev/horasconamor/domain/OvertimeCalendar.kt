package com.hugodev.horasconamor.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

object OvertimeCalendar {
    fun weekStart(date: LocalDate): LocalDate =
        date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    fun weekEnd(date: LocalDate): LocalDate = weekStart(date).plusDays(6)

    fun daysOfWeek(date: LocalDate): List<LocalDate> {
        val start = weekStart(date)
        return (0..6).map { start.plusDays(it.toLong()) }
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
