// Lokasi file: app/src/main/java/com/project/glegleg/ui/main/MainFragment.kt
package com.project.glegleg.ui.main

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.project.glegleg.R
import com.project.glegleg.data.repository.GleglegRepositoryImpl
import com.project.glegleg.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    // Properti untuk ViewBinding, untuk mengakses elemen UI dengan aman
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    // Properti untuk ViewModel, yang akan menampung semua logika UI
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi Repository & ViewModel
        val repository = GleglegRepositoryImpl(requireActivity().application)
        val viewModelFactory = MainViewModelFactory(repository)
        mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout menggunakan ViewBinding
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Panggil semua fungsi setup setelah view dibuat
        setupObservers()
        setupClickListeners()
    }

    /**
     * Fungsi untuk mengatur semua event klik dari tombol atau elemen UI lainnya.
     */
    private fun setupClickListeners() {
        // Tombol tambah cepat: +200ml
        binding.btnAdd200ml.setOnClickListener {
            val amount = 200
            mainViewModel.logNewIntake(amount)
            showIntakeConfirmation(amount)
        }

        // Tombol tambah cepat: +500ml
        binding.btnAdd500ml.setOnClickListener {
            val amount = 500
            mainViewModel.logNewIntake(amount)
            showIntakeConfirmation(amount)
        }

        // Tombol "Custom" untuk menampilkan/menyembunyikan area input manual
        binding.btnAddCustom.setOnClickListener {
            if (binding.llCustomInput.visibility == View.VISIBLE) {
                binding.llCustomInput.visibility = View.GONE
            } else {
                binding.llCustomInput.visibility = View.VISIBLE
            }
        }

        // Tombol "Tambahkan Asupan" di dalam area input custom
        binding.btnSubmitCustom.setOnClickListener {
            val customAmountString = binding.etCustomAmount.text.toString()

            if (customAmountString.isNotBlank()) {
                val customAmount = customAmountString.toIntOrNull() ?: 0
                if (customAmount > 0) {
                    mainViewModel.logNewIntake(customAmount)
                    showIntakeConfirmation(customAmount)

                    // Sembunyikan dan bersihkan input setelah berhasil
                    binding.llCustomInput.visibility = View.GONE
                    binding.etCustomAmount.text.clear()
                    hideKeyboard()
                }
            }
        }

        // Tombol untuk navigasi ke Pengaturan
        binding.btnGoToSettings.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
        }

        // Tombol untuk navigasi ke Riwayat
        binding.btnGoToHistory.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_historyFragment)
        }
    }

    /**
     * Fungsi untuk mengamati (observe) data dari ViewModel.
     * UI akan diperbarui secara otomatis setiap kali data di ViewModel berubah.
     */
    private fun setupObservers() {
        // Mengamati perubahan pada total asupan harian
        mainViewModel.todaysTotalIntake.observe(viewLifecycleOwner) { intakeAmount ->
            val amount = intakeAmount ?: 0
            binding.tvCurrentIntakeValue.text = "$amount ml"
        }

        // Mengamati perubahan pada target harian
        mainViewModel.dailyGoal.observe(viewLifecycleOwner) { goal ->
            binding.tvTargetValue.text = "/ $goal ml"
        }

        // Mengamati perubahan pada persentase progres dengan animasi
        mainViewModel.intakeProgressPercentage.observe(viewLifecycleOwner) { progress ->
            // Animasikan perubahan progress agar mulus
            ObjectAnimator.ofInt(binding.progressBarIntake, "progress", binding.progressBarIntake.progress, progress)
                .setDuration(500) // Durasi animasi 0.5 detik
                .start()
        }
    }

    /**
     * Menampilkan Snackbar sebagai feedback bahwa data berhasil ditambahkan.
     */
    private fun showIntakeConfirmation(amount: Int) {
        // Pastikan view masih ada sebelum menampilkan Snackbar
        view?.let {
            Snackbar.make(it, "Asupan $amount ml berhasil ditambahkan", Snackbar.LENGTH_SHORT).show()
        }
    }

    /**
     * Menyembunyikan keyboard secara paksa.
     * Berguna setelah pengguna menekan tombol submit.
     */
    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onResume() {
        super.onResume()
        // Muat ulang data saat fragment kembali ditampilkan untuk memastikan data selalu up-to-date
        mainViewModel.refreshDate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}