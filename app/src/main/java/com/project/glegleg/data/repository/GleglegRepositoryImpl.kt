package com.project.glegleg.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.project.glegleg.data.local.db.GleglegAppDatabase
import com.project.glegleg.data.local.db.entity.IntakeLogRecord
import com.project.glegleg.data.local.prefs.AppSettingsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

// Implementasi konkret dari GleglegRepository
class GleglegRepositoryImpl(private val context: Context) : GleglegRepository {

    private val intakeLogDao = GleglegAppDatabase.getDatabase(context).intakeLogDao()
    private val appSettings = AppSettingsManager

    // --- Implementasi Fungsi Log Asupan ---
    override suspend fun logIntake(record: IntakeLogRecord) {
        withContext(Dispatchers.IO) {
            intakeLogDao.insertLog(record)
        }
    }

    override fun getTodaysTotalIntake(startOfDayMillis: Long, endOfDayMillis: Long): LiveData<Int?> {
        return intakeLogDao.getTodaysTotalIntake(startOfDayMillis, endOfDayMillis)
    }

    override fun getAllLogs(): LiveData<List<IntakeLogRecord>> {
        return intakeLogDao.getAllLogs()
    }

    override suspend fun clearAllLogs() {
        withContext(Dispatchers.IO) {
            intakeLogDao.clearLogs()
        }
    }

    // --- Implementasi Fungsi Pengaturan ---
    override fun getDailyGoal(): Int {
        return appSettings.getTargetAmount(context)
    }

    override suspend fun saveDailyGoal(goal: Int) {
        withContext(Dispatchers.IO) {
            appSettings.setTargetAmount(context, goal)
        }
    }

    override fun areRemindersEnabled(): Boolean {
        return appSettings.isNotificationEnabled(context) // Nama fungsi di AppSettingsManager mungkin beda
    }

    override suspend fun setRemindersEnabled(enabled: Boolean) {
        withContext(Dispatchers.IO) {
            appSettings.setNotificationEnabled(context, enabled) // Nama fungsi di AppSettingsManager mungkin beda
        }
    }
}