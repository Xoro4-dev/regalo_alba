package com.hugodev.horasconamor.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hugodev.horasconamor.data.local.OvertimeDatabase
import com.hugodev.horasconamor.data.local.OvertimeEntryEntity
import com.hugodev.horasconamor.data.repository.OvertimeRepository
import com.hugodev.horasconamor.domain.OvertimeCalendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicLong

data class TodaySummary(
    val date: LocalDate,
    val todayMinutes: Int = 0,
    val weekMinutes: Int = 0,
)

data class DailyOvertime(
    val date: LocalDate,
    val minutes: Int,
)

data class OvertimeChange(
    val date: LocalDate,
    val previousMinutes: Int,
    val token: Long,
)

@OptIn(ExperimentalCoroutinesApi::class)
class OvertimeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = OvertimeRepository(
        OvertimeDatabase.getInstance(application).overtimeDao(),
    )
    private val preferences = application.getSharedPreferences(PREFERENCES_FILE, 0)
    private val today = MutableStateFlow(LocalDate.now())
    private val historyWeekStart = MutableStateFlow(OvertimeCalendar.weekStart(today.value))
    private val changeToken = AtomicLong()
    private val activeUndoToken = MutableStateFlow<Long?>(null)

    val incrementMinutes = MutableStateFlow(
        preferences.getInt(INCREMENT_MINUTES_KEY, DEFAULT_INCREMENT_MINUTES)
            .takeIf { it in INCREMENT_OPTIONS }
            ?: DEFAULT_INCREMENT_MINUTES,
    )
    val historyStartDate: StateFlow<LocalDate> = historyWeekStart

    val todaySummary: StateFlow<TodaySummary> = today
        .flatMapLatest { date ->
            val start = OvertimeCalendar.weekStart(date)
            val end = OvertimeCalendar.weekEnd(date)
            repository.observeBetween(start, end).map { entries ->
                val minutesByDate = entries.associate { it.date to it.minutes }
                TodaySummary(
                    date = date,
                    todayMinutes = minutesByDate[date.toString()] ?: 0,
                    weekMinutes = entries.sumOf(OvertimeEntryEntity::minutes),
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TodaySummary(today.value),
        )

    val historyDays: StateFlow<List<DailyOvertime>> = historyWeekStart
        .flatMapLatest { start ->
            repository.observeBetween(start, start.plusDays(6)).map { entries ->
                val minutesByDate = entries.associate { it.date to it.minutes }
                (0..6).map { offset ->
                    val date = start.plusDays(offset.toLong())
                    DailyOvertime(date, minutesByDate[date.toString()] ?: 0)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = OvertimeCalendar.daysOfWeek(today.value).map { DailyOvertime(it, 0) },
        )

    fun setIncrementMinutes(minutes: Int) {
        require(minutes in INCREMENT_OPTIONS) { "Unsupported increment: $minutes" }
        preferences.edit().putInt(INCREMENT_MINUTES_KEY, minutes).apply()
        incrementMinutes.value = minutes
    }

    fun moveHistoryWeek(weeks: Long) {
        val nextStart = historyWeekStart.value.plusWeeks(weeks)
        val currentWeekStart = OvertimeCalendar.weekStart(LocalDate.now())
        if (nextStart <= currentWeekStart) {
            historyWeekStart.value = nextStart
        }
    }

    fun refreshToday() {
        val previousDate = today.value
        val currentDate = LocalDate.now()
        if (currentDate == previousDate) return

        today.value = currentDate
        if (historyWeekStart.value == OvertimeCalendar.weekStart(previousDate)) {
            historyWeekStart.value = OvertimeCalendar.weekStart(currentDate)
        }
    }

    suspend fun adjustMinutes(date: LocalDate, delta: Int): OvertimeChange? =
        withContext(Dispatchers.IO) {
            val adjustment = repository.adjustMinutes(date, delta)
            if (adjustment.currentMinutes == adjustment.previousMinutes) {
                null
            } else {
                OvertimeChange(
                    date = date,
                    previousMinutes = adjustment.previousMinutes,
                    token = changeToken.incrementAndGet(),
                ).also { activeUndoToken.value = it.token }
            }
        }

    suspend fun setMinutes(date: LocalDate, minutes: Int) {
        withContext(Dispatchers.IO) {
            repository.setMinutes(date, minutes)
        }
    }

    suspend fun undo(change: OvertimeChange) {
        if (activeUndoToken.value != change.token) return
        withContext(Dispatchers.IO) {
            repository.setMinutes(change.date, change.previousMinutes)
        }
        activeUndoToken.value = null
    }

    companion object {
        val INCREMENT_OPTIONS = listOf(15, 30, 60)
        const val DEFAULT_INCREMENT_MINUTES = 30

        private const val PREFERENCES_FILE = "horas_con_amor_preferences"
        private const val INCREMENT_MINUTES_KEY = "increment_minutes"
    }
}
