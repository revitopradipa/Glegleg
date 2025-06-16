package com.project.glegleg.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.glegleg.R
import com.project.glegleg.data.repository.GleglegRepositoryImpl
import com.project.glegleg.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = GleglegRepositoryImpl(requireActivity().application)
        val viewModelFactory = MainViewModelFactory(repository)
        mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnAdd200ml.setOnClickListener { mainViewModel.logNewIntake(200) }
        binding.btnAdd500ml.setOnClickListener { mainViewModel.logNewIntake(500) }
        binding.btnGoToSettings.setOnClickListener {
             findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
        }
    }

    private fun setupObservers() {
        mainViewModel.todaysTotalIntake.observe(viewLifecycleOwner) { intake ->
            val amount = intake ?: 0
            binding.tvCurrentIntakeValue.text = "$amount ml"
            updateProgressBar()
        }
        mainViewModel.dailyGoal.observe(viewLifecycleOwner) { goal ->
            binding.tvTargetValue.text = "/ $goal ml"
            updateProgressBar()
        }
    }

    private fun updateProgressBar() {
        val current = mainViewModel.todaysTotalIntake.value ?: 0
        val goal = mainViewModel.dailyGoal.value ?: 2000 // Default goal
        if (goal > 0) {
            binding.progressBarIntake.progress = (current * 100 / goal).coerceIn(0, 100)
        } else {
            binding.progressBarIntake.progress = 0
        }
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.refreshDate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}