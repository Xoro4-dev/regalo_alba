package com.hugodev.horasconamor.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface OvertimeDao {
    @Query(
        """
        SELECT * FROM overtime_entries
        WHERE date BETWEEN :startDate AND :endDate
        ORDER BY date ASC
        """,
    )
    fun observeBetween(startDate: String, endDate: String): Flow<List<OvertimeEntryEntity>>

    @Query("SELECT * FROM overtime_entries WHERE date = :date LIMIT 1")
    suspend fun findByDate(date: String): OvertimeEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: OvertimeEntryEntity)

    @Transaction
    suspend fun adjustMinutes(date: String, delta: Int, updatedAt: Long): MinuteAdjustment {
        val previousMinutes = findByDate(date)?.minutes ?: 0
        val currentMinutes = OvertimeRules.adjustedMinutes(previousMinutes, delta)
        upsert(
            OvertimeEntryEntity(
                date = date,
                minutes = currentMinutes,
                updatedAtEpochMillis = updatedAt,
            ),
        )
        return MinuteAdjustment(previousMinutes, currentMinutes)
    }
}

data class MinuteAdjustment(
    val previousMinutes: Int,
    val currentMinutes: Int,
)
