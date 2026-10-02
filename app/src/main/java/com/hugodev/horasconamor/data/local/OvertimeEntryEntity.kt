package com.hugodev.horasconamor.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "overtime_entries")
data class OvertimeEntryEntity(
    @PrimaryKey
    val date: String,
    val minutes: Int,
    val updatedAtEpochMillis: Long,
    val note: String? = null,
)
