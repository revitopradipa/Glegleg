package com.project.glegleg.data.repository

import androidx.lifecycle.LiveData
import com.project.glegleg.data.local.db.entity.IntakeLogRecord
import kotlinx.coroutines.flow.Flow

interface GleglegRepository {

    suspend fun logIntake(record: IntakeLogRecord)
    fun getTodaysTotalIntake(startOfDayMillis: Long, endOfDayMillis: Long): LiveData<Int?>
    fun getAllLogs(): LiveData<List<IntakeLogRecord>>
    suspend fun clearAllLogs()


    fun getDailyGoal(): Flow<Int>
    suspend fun saveDailyGoal(goal: Int)
    fun areRemindersEnabled(): Flow<Boolean>
    suspend fun setRemindersEnabled(enabled: Boolean)
}