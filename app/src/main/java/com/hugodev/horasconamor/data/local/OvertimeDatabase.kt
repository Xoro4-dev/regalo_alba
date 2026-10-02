package com.hugodev.horasconamor.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [OvertimeEntryEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class OvertimeDatabase : RoomDatabase() {
    abstract fun overtimeDao(): OvertimeDao

    companion object {
        @Volatile
        private var instance: OvertimeDatabase? = null

        fun getInstance(context: Context): OvertimeDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    OvertimeDatabase::class.java,
                    "horas_con_amor.db",
                ).build().also { instance = it }
            }
    }
}
