package com.hugodev.horasconamor.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class OvertimeCalendarTest {
    @Test
    fun workweekUsesMondayThroughFridayAndCrossesMonthBoundary() {
        val sunday = LocalDate.of(2026, 11, 1)

        assertEquals(LocalDate.of(2026, 10, 26), OvertimeCalendar.weekStart(sunday))
        assertEquals(LocalDate.of(2026, 10, 30), OvertimeCalendar.weekEnd(sunday))
        assertEquals(
            LocalDate.of(2026, 10, 26),
            OvertimeCalendar.weekStart(LocalDate.of(2026, 10, 26)),
        )
    }

    @Test
    fun workdaysOfWeekReturnsOnlyMondayThroughFriday() {
        val dates = OvertimeCalendar.workdaysOfWeek(LocalDate.of(2026, 10, 28))

        assertEquals(5, dates.size)
        assertEquals(LocalDate.of(2026, 10, 26), dates.first())
        assertEquals(LocalDate.of(2026, 10, 30), dates.last())
        assertTrue(OvertimeCalendar.isWorkday(dates.first()))
        assertEquals(false, OvertimeCalendar.isWorkday(LocalDate.of(2026, 10, 31)))
        assertEquals(false, OvertimeCalendar.isWorkday(LocalDate.of(2026, 11, 1)))
    }

    @Test
    fun weeklyMessageChangesExactlyAtTwoAndFiveHourLimits() {
        assertEquals(WeeklyMessageTier.UNDER_TWO_HOURS, OvertimeCalendar.weeklyMessageTier(119))
        assertEquals(WeeklyMessageTier.TWO_TO_FIVE_HOURS, OvertimeCalendar.weeklyMessageTier(120))
        assertEquals(WeeklyMessageTier.TWO_TO_FIVE_HOURS, OvertimeCalendar.weeklyMessageTier(300))
        assertEquals(WeeklyMessageTier.OVER_FIVE_HOURS, OvertimeCalendar.weeklyMessageTier(301))
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
