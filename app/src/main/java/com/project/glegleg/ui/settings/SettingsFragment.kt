// Lokasi file: app/src/main/java/com/project/glegleg/ui/settings/SettingsFragment.kt
package com.project.glegleg.ui.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.project.glegleg.data.repository.GleglegRepositoryImpl
import com.project.glegleg.databinding.FragmentSettingBinding // Ganti nama binding jika berbeda
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = GleglegRepositoryImpl(requireActivity().application)
        val factory = SettingsViewModelFactory(repository, requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observasi UI State dari ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                // Update UI hanya jika nilainya berbeda untuk menghindari loop
                if (binding.inputDailyTarget.text.toString() != state.dailyTarget.toString()) {
                    binding.inputDailyTarget.setText(state.dailyTarget.toString())
                }
                if (binding.switchReminder.isChecked != state.reminderEnabled) {
                    binding.switchReminder.isChecked = state.reminderEnabled
                }
            }
        }

        // Simpan perubahan switch pengingat
        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            // Cek lagi untuk memastikan tidak memanggil berulang kali
            if (viewModel.uiState.value.reminderEnabled != isChecked) {
                viewModel.setReminderEnabled(isChecked)
            }
        }

        // Simpan target harian saat teks berubah
        binding.inputDailyTarget.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val value = s?.toString()?.toIntOrNull()
                if (value != null && value != viewModel.uiState.value.dailyTarget) {
                    viewModel.setDailyTarget(value)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}