package com.hugodev.horasconamor.data.repository

import com.hugodev.horasconamor.data.local.OvertimeDao
import com.hugodev.horasconamor.data.local.OvertimeEntryEntity
import com.hugodev.horasconamor.data.local.MinuteAdjustment
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class OvertimeRepository(
    private val overtimeDao: OvertimeDao,
) {
    fun observeBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<OvertimeEntryEntity>> =
        overtimeDao.observeBetween(startDate.toString(), endDate.toString())

    suspend fun adjustMinutes(date: LocalDate, delta: Int): MinuteAdjustment =
        overtimeDao.adjustMinutes(
            date = date.toString(),
            delta = delta,
            updatedAt = System.currentTimeMillis(),
        )

    suspend fun setMinutes(date: LocalDate, minutes: Int) {
        overtimeDao.upsert(
            OvertimeEntryEntity(
                date = date.toString(),
                minutes = minutes.coerceAtLeast(0),
                updatedAtEpochMillis = System.currentTimeMillis(),
            ),
        )
    }
}
