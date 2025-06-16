package com.project.glegleg.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import com.project.glegleg.data.local.db.GleglegAppDatabase
import com.project.glegleg.data.local.db.entity.IntakeLogRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

// Definisikan DataStore di top-level agar hanya ada satu instance
private val Context.dataStore by preferencesDataStore(name = "user_settings")

class GleglegRepositoryImpl(private val context: Context) : GleglegRepository {

    private val intakeLogDao = GleglegAppDatabase.getDatabase(context).intakeLogDao()
    private val dataStore = context.dataStore

    // Definisikan Keys untuk DataStore
    private val DAILY_TARGET_KEY = intPreferencesKey("daily_target")
    private val REMINDER_ENABLED_KEY = booleanPreferencesKey("reminder_enabled")

    // --- Implementasi Fungsi Log Asupan (Tetap sama) ---
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

    // --- Implementasi Fungsi Pengaturan (Sekarang menggunakan DataStore) ---
    override fun getDailyGoal(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[DAILY_TARGET_KEY] ?: 2000 // default value 2000
        }
    }

    override suspend fun saveDailyGoal(goal: Int) {
        dataStore.edit { preferences ->
            preferences[DAILY_TARGET_KEY] = goal
        }
    }

    override fun areRemindersEnabled(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[REMINDER_ENABLED_KEY] ?: false // default value false
        }
    }

    override suspend fun setRemindersEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[REMINDER_ENABLED_KEY] = enabled
        }
    }
}