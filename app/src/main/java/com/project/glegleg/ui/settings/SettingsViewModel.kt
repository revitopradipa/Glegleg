package com.project.glegleg.ui.settings

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Inisialisasi DataStore langsung di Context
private val Context.dataStore by preferencesDataStore(name = "user_settings")

data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val dailyTarget: Int = 0,
    val reminderEnabled: Boolean = false
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    // Key-key untuk pengaturan
    private val DARK_MODE = booleanPreferencesKey("dark_mode")
    private val NOTIFICATION = booleanPreferencesKey("notification")
    private val DAILY_TARGET = intPreferencesKey("daily_target")
    private val REMINDER = booleanPreferencesKey("reminder")

    private val dataStore = application.applicationContext.dataStore

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        // Observasi semua setting dari DataStore
        viewModelScope.launch {
            dataStore.data.collect { prefs ->
                _uiState.value = SettingsUiState(
                    isDarkMode = prefs[DARK_MODE] ?: false,
                    notificationsEnabled = prefs[NOTIFICATION] ?: true,
                    dailyTarget = prefs[DAILY_TARGET] ?: 0,
                    reminderEnabled = prefs[REMINDER] ?: false
                )
            }
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[DARK_MODE] = enabled
            }
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[NOTIFICATION] = enabled
            }
        }
    }

    fun setDailyTarget(value: Int) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[DAILY_TARGET] = value
            }
        }
    }

    fun setReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[REMINDER] = enabled
            }
        }
    }
}
