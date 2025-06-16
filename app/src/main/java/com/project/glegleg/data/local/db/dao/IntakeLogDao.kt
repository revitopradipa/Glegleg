package com.project.glegleg.data.local.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.project.glegleg.data.local.db.entity.IntakeLogRecord

@Dao
interface IntakeLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: IntakeLogRecord)

    @Query("SELECT * FROM intake_log ORDER BY timestamp DESC")
    fun getAllLogs(): LiveData<List<IntakeLogRecord>>

    @Query("SELECT * FROM intake_log WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp DESC")
    fun getLogsInTimeRange(start: Long, end: Long): LiveData<List<IntakeLogRecord>>

    @Query("SELECT SUM(amountMl) FROM intake_log WHERE timestamp BETWEEN :start AND :end")
    fun getTodaysTotalIntake(start: Long, end: Long): LiveData<Int?>

    @Query("DELETE FROM intake_log")
    suspend fun clearLogs()
}