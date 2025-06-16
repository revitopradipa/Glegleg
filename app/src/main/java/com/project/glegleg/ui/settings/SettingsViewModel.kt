// Lokasi file: app/src/main/java/com/project/glegleg/ui/settings/SettingsViewModel.kt
package com.project.glegleg.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.glegleg.data.repository.GleglegRepository
import com.project.glegleg.features.reminders.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

// State class tetap sama
data class SettingsUiState(
    val dailyTarget: Int = 2000,
    val reminderEnabled: Boolean = false
)

// Ganti ViewModel menjadi AndroidViewModel untuk mendapatkan akses ke Context
class SettingsViewModel(
    private val repository: GleglegRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        // Muat data awal dari repository
        viewModelScope.launch {
            repository.getDailyGoal().collect { goal ->
                // --- PERBAIKAN DI SINI ---
                // Ambil state saat ini, buat salinan dengan nilai baru, lalu update
                val currentState = _uiState.value
                _uiState.value = currentState.copy(dailyTarget = goal)
            }
        }
        viewModelScope.launch {
            repository.areRemindersEnabled().collect { isEnabled ->
                // --- PERBAIKAN DI SINI ---
                val currentState = _uiState.value
                _uiState.value = currentState.copy(reminderEnabled = isEnabled)
            }
        }
    }

    fun setDailyTarget(value: Int) {
        viewModelScope.launch {
            repository.saveDailyGoal(value)
        }
    }

    fun setReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setRemindersEnabled(enabled)

            // Hubungkan ke ReminderScheduler
            val context = getApplication<Application>().applicationContext
            if (enabled) {
                ReminderScheduler.scheduleReminder(context)
            } else {
                ReminderScheduler.cancelReminder(context)
            }
        }
    }
}