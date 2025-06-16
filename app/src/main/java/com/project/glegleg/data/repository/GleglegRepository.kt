package com.project.glegleg.data.repository

import androidx.lifecycle.LiveData
import com.project.glegleg.data.local.db.entity.IntakeLogRecord

interface GleglegRepository {

    // --- Fungsi untuk Log Asupan (dari Database Room) ---
    suspend fun logIntake(record: IntakeLogRecord)
    fun getTodaysTotalIntake(startOfDayMillis: Long, endOfDayMillis: Long): LiveData<Int?>
    fun getAllLogs(): LiveData<List<IntakeLogRecord>> // Untuk riwayat
    suspend fun clearAllLogs()

    // --- Fungsi untuk Pengaturan (dari SharedPreferences/DataStore) ---
    fun getDailyGoal(): Int
    suspend fun saveDailyGoal(goal: Int)
    fun areRemindersEnabled(): Boolean
    suspend fun setRemindersEnabled(enabled: Boolean)
}