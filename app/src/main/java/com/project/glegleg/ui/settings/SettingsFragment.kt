package com.project.glegleg.ui.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.project.glegleg.data.repository.GleglegRepositoryImpl
import com.project.glegleg.databinding.FragmentSettingBinding
import com.project.glegleg.utils.PermissionUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SettingsViewModel

    // Launcher untuk menangani hasil dari dialog permintaan izin.
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Jika pengguna memberikan izin, kita set reminder enabled di ViewModel.
            // ViewModel kemudian akan menjadwalkan alarm.
            viewModel.setReminderEnabled(true)
            Snackbar.make(binding.root, "Pengingat berhasil diaktifkan!", Snackbar.LENGTH_SHORT).show()
        } else {
            // Jika pengguna menolak, kembalikan switch ke posisi off dan beri tahu mereka.
            binding.switchReminder.isChecked = false
            Snackbar.make(binding.root, "Izin notifikasi ditolak. Pengingat tidak dapat diaktifkan.", Snackbar.LENGTH_LONG).show()
        }
    }

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                if (binding.inputDailyTarget.text.toString() != state.dailyTarget.toString()) {
                    binding.inputDailyTarget.setText(state.dailyTarget.toString())
                }
                if (binding.switchReminder.isChecked != state.reminderEnabled) {
                    binding.switchReminder.isChecked = state.reminderEnabled
                }
            }
        }

        binding.inputDailyTarget.addTextChangedListener(object : TextWatcher {
            // Metode ini bisa kita biarkan kosong
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            // Metode ini juga bisa kita biarkan kosong
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            // Kita letakkan logika di sini, yang akan dieksekusi setelah teks berubah
            override fun afterTextChanged(s: Editable?) {
                // 1. Ambil teks yang baru sebagai String
                val valueString = s?.toString()

                // 2. Ubah String menjadi Angka (Int).
                //    Gunakan toIntOrNull() agar aman jika teks kosong atau tidak valid.
                //    Jika kosong/tidak valid, anggap nilainya 0.
                val value = valueString?.toIntOrNull() ?: 0

                // 3. Panggil fungsi di ViewModel untuk menyimpan nilai baru,
                //    tapi hanya jika nilainya berbeda dengan yang sudah ada untuk menghindari loop.
                if (value != viewModel.uiState.value.dailyTarget) {
                    viewModel.setDailyTarget(value)
                }
            }
        })

        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            // Hanya jalankan logika jika status switch benar-benar berubah oleh aksi pengguna
            if (isChecked != viewModel.uiState.value.reminderEnabled) {
                handleReminderSwitch(isChecked)
            }
        }
    }

    private fun handleReminderSwitch(isChecked: Boolean) {
        if (isChecked) {
            // Jika pengguna ingin mengaktifkan, cek izin terlebih dahulu.
            if (PermissionUtils.hasNotificationPermission(requireContext())) {
                // Jika izin sudah ada, langsung aktifkan.
                viewModel.setReminderEnabled(true)
            } else {
                // Jika izin belum ada, minta izin.
                PermissionUtils.requestNotificationPermission(permissionLauncher)
            }
        } else {
            // Jika pengguna ingin menonaktifkan, langsung saja batalkan.
            viewModel.setReminderEnabled(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}