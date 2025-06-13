package com.example.hairsalonappointments.ui.appointments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hairsalonappointments.R
import com.example.hairsalonappointments.adapters.AppointmentAdapter
import com.example.hairsalonappointments.data.Appointment
import com.example.hairsalonappointments.data.AppointmentStatus
import com.example.hairsalonappointments.data.MockApiService
import com.example.hairsalonappointments.databinding.FragmentAppointmentListBinding

/**
 * Fragment for displaying the list of appointments
 * 
 * TASK FOR CANDIDATE:
 * Implement the core functionality for this fragment.
 * 
 * Requirements:
 * - Load appointments from MockApiService
 * - Display them in a RecyclerView
 * - Handle empty states
 * - Implement filter toggle (All vs Available)
 * - Navigate to detail screen on item click
 * - Handle loading and error states appropriately
 */
class AppointmentListFragment : Fragment() {
    
    private var _binding: FragmentAppointmentListBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var adapter: AppointmentAdapter
    private lateinit var apiService: MockApiService
    private var allAppointments = emptyList<Appointment>()
    private var showingAllAppointments = true
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupFilterToggle()
        loadAppointments()
    }
    
    private fun setupRecyclerView() {
        adapter = AppointmentAdapter { appointment ->
            onAppointmentClick(appointment)
        }
        
        binding.recyclerViewAppointments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AppointmentListFragment.adapter
        }
    }
    
    private fun setupFilterToggle() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            when (checkedIds.firstOrNull()) {
                R.id.chipAll -> {
                    showingAllAppointments = true
                    updateAppointmentList()
                }
                R.id.chipAvailable -> {
                    showingAllAppointments = false
                    updateAppointmentList()
                }
            }
        }
    }
    
    /**
     * TODO: Implement this function
     * 
     * Load appointments from the MockApiService.
     * - Initialize the apiService
     * - Call getTodaysAppointments()
     * - Update the UI based on results
     * - Show empty state if no appointments
     * - Handle any errors gracefully
     */
    private fun loadAppointments() {
        // TODO: Implement appointment loading
        // 1. Initialize MockApiService
        // 2. Get today's appointments
        // 3. Store in allAppointments
        // 4. Update the RecyclerView
        // 5. Handle empty state
        
        throw NotImplementedError("Candidate needs to implement loadAppointments()")
    }
    
    /**
     * TODO: Implement this function
     * 
     * Handle appointment item clicks.
     * - Navigate to AppointmentDetailFragment
     * - Pass the appointment ID as an argument
     * - Use Navigation component
     * 
     * @param appointment The clicked appointment
     */
    private fun onAppointmentClick(appointment: Appointment) {
        // TODO: Implement navigation to detail screen
        // Use findNavController() and navigate with appointment ID
        
        throw NotImplementedError("Candidate needs to implement onAppointmentClick()")
    }
    
    /**
     * Update the displayed appointment list based on current filter
     */
    private fun updateAppointmentList() {
        val appointmentsToShow = if (showingAllAppointments) {
            allAppointments
        } else {
            // Show only pending and confirmed appointments (available for service)
            allAppointments.filter { 
                it.status == AppointmentStatus.PENDING || 
                it.status == AppointmentStatus.CONFIRMED 
            }
        }
        
        adapter.submitList(appointmentsToShow)
        updateEmptyState(appointmentsToShow.isEmpty())
    }
    
    /**
     * Update the empty state visibility
     * 
     * @param isEmpty Whether the list is empty
     */
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.textViewEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.recyclerViewAppointments.visibility = if (isEmpty) View.GONE else View.VISIBLE
        
        if (isEmpty) {
            binding.textViewEmpty.text = if (showingAllAppointments) {
                "No appointments scheduled for today"
            } else {
                "No available appointments"
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}