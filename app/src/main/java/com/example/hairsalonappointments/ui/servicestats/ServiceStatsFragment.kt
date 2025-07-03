package com.example.hairsalonappointments.ui.servicestats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hairsalonappointments.adapters.ServiceStatsAdapter
import com.example.hairsalonappointments.data.MockApiService
import com.example.hairsalonappointments.databinding.FragmentServiceStatsBinding
import kotlinx.coroutines.launch

class ServiceStatsFragment : Fragment() {

    private var _binding: FragmentServiceStatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var serviceStatsAdapter: ServiceStatsAdapter
    private val apiService = MockApiService() // Use your actual API service

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServiceStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadServiceStats()
    }

    private fun setupRecyclerView() {
        serviceStatsAdapter = ServiceStatsAdapter()
        binding.recyclerViewServiceStats.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = serviceStatsAdapter
        }
    }

    private fun loadServiceStats() {
        lifecycleScope.launch {
            try {
                val serviceStats = apiService.getServiceStats()
                serviceStatsAdapter.submitList(serviceStats)
            } catch (e: Exception) {
                // Handle error, e.g., show a Toast or error message
                // For now, just log
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
