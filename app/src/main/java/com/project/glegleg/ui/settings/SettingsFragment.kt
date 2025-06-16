package com.project.glegleg.ui.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.project.glegleg.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()

    private lateinit var inputTarget: EditText
    private lateinit var switchReminder: Switch

    private var isTargetInitialized = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputTarget = view.findViewById(R.id.inputDailyTarget)
        switchReminder = view.findViewById(R.id.switchReminder)

        // Observasi UI State dari ViewModel
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                // Hindari override saat user sedang mengetik
                if (!isTargetInitialized || inputTarget.text.toString() != state.dailyTarget.toString()) {
                    inputTarget.setText(state.dailyTarget.toString())
                }
                switchReminder.isChecked = state.reminderEnabled
                isTargetInitialized = true
            }
        }

        // Simpan perubahan switch pengingat
        switchReminder.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setReminderEnabled(isChecked)
        }

        // Simpan target harian saat teks berubah
        inputTarget.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val value = s.toString().toIntOrNull() ?: 0
                viewModel.setDailyTarget(value)
            }
        })
    }
}
