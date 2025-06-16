package com.project.glegleg.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.glegleg.data.repository.GleglegRepositoryImpl
import com.project.glegleg.databinding.FragmentHistoryBinding
import com.project.glegleg.ui.history.adapter.HistoryAdapter

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi ViewModel dengan Factory yang sudah kita buat
        val repository = GleglegRepositoryImpl(requireActivity().application)
        val factory = HistoryViewModelFactory(repository)
        historyViewModel = ViewModelProvider(this, factory)[HistoryViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Panggil fungsi untuk setup RecyclerView
        setupRecyclerView()

        historyViewModel.allIntakeLogs.observe(viewLifecycleOwner) { logs ->
            if (logs.isNullOrEmpty()) {
                // Jika data kosong, tampilkan pesan
                binding.recyclerViewHistory.visibility = View.GONE
                binding.tvEmptyHistory.visibility = View.VISIBLE
            } else {
                // Jika data ada, tampilkan RecyclerView
                binding.recyclerViewHistory.visibility = View.VISIBLE
                binding.tvEmptyHistory.visibility = View.GONE
                historyAdapter.submitList(logs)
            }
        }
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter()
        binding.recyclerViewHistory.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Membersihkan binding untuk mencegah memory leak
        _binding = null
    }
}