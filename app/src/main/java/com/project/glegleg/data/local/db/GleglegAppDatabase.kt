
package com.project.glegleg.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.project.glegleg.data.local.db.dao.IntakeLogDao
import com.project.glegleg.data.local.db.entity.IntakeLogRecord

@Database(entities = [IntakeLogRecord::class], version = 1, exportSchema = false)
abstract class GleglegAppDatabase : RoomDatabase() {
    abstract fun intakeLogDao(): IntakeLogDao

    companion object {
        @Volatile
        private var INSTANCE: GleglegAppDatabase? = null

        fun getDatabase(context: Context): GleglegAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GleglegAppDatabase::class.java,
                    "glegleg_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
