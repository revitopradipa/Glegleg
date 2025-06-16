package com.project.glegleg.ui.main

import androidx.lifecycle.*
import com.project.glegleg.data.local.db.entity.IntakeLogRecord
import com.project.glegleg.data.repository.GleglegRepository
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(private val repository: GleglegRepository) : ViewModel() {

    private val _dateTrigger = MutableLiveData<Long>()

    val todaysTotalIntake: LiveData<Int?> = _dateTrigger.switchMap { todayStartMillis ->
        val endOfDay = todayStartMillis + 24 * 60 * 60 * 1000 - 1
        repository.getTodaysTotalIntake(todayStartMillis, endOfDay)
    }

    val dailyGoal: LiveData<Int> = repository.getDailyGoal().asLiveData()

    val intakeProgressPercentage: LiveData<Int> = MediatorLiveData<Int>().apply {
        addSource(todaysTotalIntake) { intake ->
            value = calculateProgress(intake, dailyGoal.value)
        }
        addSource(dailyGoal) { goal ->
            value = calculateProgress(todaysTotalIntake.value, goal)
        }
    }

    init {
        refreshDate()
    }

    fun logNewIntake(amount: Int) {
        viewModelScope.launch {
            val newRecord = IntakeLogRecord(amountMl = amount)
            repository.logIntake(newRecord)
        }
    }

    fun refreshDate() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        _dateTrigger.value = calendar.timeInMillis
    }

    private fun calculateProgress(currentIntake: Int?, dailyGoal: Int?): Int {
        if (currentIntake == null || dailyGoal == null || dailyGoal == 0) return 0
        return ((currentIntake.toDouble() / dailyGoal.toDouble()) * 100).toInt().coerceIn(0, 100)
    }
}