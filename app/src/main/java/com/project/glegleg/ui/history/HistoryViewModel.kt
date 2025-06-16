package com.project.glegleg.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.project.glegleg.data.local.db.entity.IntakeLogRecord
import com.project.glegleg.data.repository.GleglegRepository

class HistoryViewModel(private val repository: GleglegRepository) : ViewModel() {

    // Ambil semua data log dari repository.
    val allIntakeLogs: LiveData<List<IntakeLogRecord>> = repository.getAllLogs()
}