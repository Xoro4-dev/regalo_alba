package com.hugodev.horasconamor.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class OvertimeCalendarTest {
    @Test
    fun weekUsesMondayAsFirstDayAndCrossesMonthBoundary() {
        val sunday = LocalDate.of(2026, 11, 1)

        assertEquals(LocalDate.of(2026, 10, 26), OvertimeCalendar.weekStart(sunday))
        assertEquals(LocalDate.of(2026, 11, 1), OvertimeCalendar.weekEnd(sunday))
        assertEquals(
            LocalDate.of(2026, 10, 26),
            OvertimeCalendar.weekStart(LocalDate.of(2026, 10, 26)),
        )
    }

    @Test
    fun daysOfWeekReturnsSevenConsecutiveDates() {
        val dates = OvertimeCalendar.daysOfWeek(LocalDate.of(2026, 10, 28))

        assertEquals(7, dates.size)
        assertEquals(LocalDate.of(2026, 10, 26), dates.first())
        assertEquals(LocalDate.of(2026, 11, 1), dates.last())
    }

    @Test
    fun formatDurationShowsHoursAndRemainingMinutes() {
        assertEquals("0 min", OvertimeCalendar.formatDuration(0))
        assertEquals("45 min", OvertimeCalendar.formatDuration(45))
        assertEquals("1 h", OvertimeCalendar.formatDuration(60))
        assertEquals("2 h 15 min", OvertimeCalendar.formatDuration(135))
        assertEquals("0 min", OvertimeCalendar.formatDuration(-15))
    }
}
