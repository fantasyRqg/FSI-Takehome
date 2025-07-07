package com.example.hairsalonappointments.ui.appointments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hairsalonappointments.R
import com.example.hairsalonappointments.adapters.AppointmentAdapter
import com.example.hairsalonappointments.data.Appointment
import com.example.hairsalonappointments.data.AppointmentStatus
import com.example.hairsalonappointments.data.FilterCriteria
import com.example.hairsalonappointments.data.MockApiService
import com.example.hairsalonappointments.databinding.FragmentAppointmentListBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

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
import android.app.AlertDialog

class AppointmentListFragment : Fragment(), AppointmentAdapter.OnAppointmentActionListener {
    companion object {
        private val TAG = AppointmentListFragment::class.simpleName
    }

    private var _binding: FragmentAppointmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AppointmentAdapter
    private val apiService by lazy { MockApiService() }
    private var allAppointments = emptyList<Appointment>()
    private var currentFilterCriteria: FilterCriteria = FilterCriteria()
    private var searchQuery = ""
    private val disposables = CompositeDisposable()
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
        setupSearch()
        loadAppointments()
        setupFab()
        setupFilterButton()
    }

    private fun setupRecyclerView() {
        adapter = AppointmentAdapter(
            onAppointmentActionListener = this
        )

        binding.recyclerViewAppointments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AppointmentListFragment.adapter
        }
    }

    private fun setupFilterToggle() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            val statusFilter = when (checkedIds.firstOrNull()) {
                R.id.chipAll -> null
                R.id.chipAvailable -> "Available" // Assuming "Available" is a status
                else -> null
            }
            currentFilterCriteria = currentFilterCriteria.copy(status = statusFilter)
            updateAppointmentList()
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchQuery = query.orEmpty()
                updateAppointmentList()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery = newText.orEmpty()
                updateAppointmentList()
                return true
            }
        })
    }


    /**
     *
     * Load appointments from the MockApiService.
     * - Initialize the apiService
     * - Call getTodaysAppointments()
     * - Update the UI based on results
     * - Show empty state if no appointments
     * - Handle any errors gracefully
     */
    private fun loadAppointments(filterCriteria: FilterCriteria = currentFilterCriteria) {
        Observable.fromCallable {
            apiService.getAppointments(filterCriteria)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    allAppointments = it
                    updateAppointmentList()
                },
                onError = { error ->
                    Log.e(TAG, "loadAppointments: $error", error)
                    Toast.makeText(
                        requireContext(),
                        "Error loading appointments. Err: $error",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateEmptyState(true)
                }
            )
            .addTo(disposables)
    }

    /**
     *
     * Handle appointment item clicks.
     * - Navigate to AppointmentDetailFragment
     * - Pass the appointment ID as an argument
     * - Use Navigation component
     *
     * @param appointment The clicked appointment
     */
    override fun onAppointmentClick(appointment: Appointment) {
        // Use findNavController() and navigate with appointment ID

        val action = AppointmentListFragmentDirections.actionAppointmentListFragmentToAppointmentDetailFragment(appointment.id)
        findNavController().navigate(action)
    }

    /**
     * Update the displayed appointment list based on current filter
     */
    private fun updateAppointmentList() {
        val appointmentsToShow = allAppointments.filter { appointment ->
            val matchesSearch = if (searchQuery.isEmpty()) {
                true
            } else {
                appointment.clientName.contains(searchQuery, true) ||
                        appointment.stylistName.contains(searchQuery, true)
            }

            val matchesStylist = if (currentFilterCriteria.stylistId.isNullOrEmpty()) {
                true
            } else {
                appointment.stylistName.equals(currentFilterCriteria.stylistId, true)
            }

            val matchesServiceType = if (currentFilterCriteria.serviceTypeId.isNullOrEmpty()) {
                true
            } else {
                appointment.serviceType.name.equals(currentFilterCriteria.serviceTypeId, true)
            }

            val matchesStatus = if (currentFilterCriteria.status.isNullOrEmpty()) {
                true
            } else {
                appointment.status.name.equals(currentFilterCriteria.status, ignoreCase = true)
            }

            matchesSearch && matchesStylist && matchesServiceType && matchesStatus
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
            binding.textViewEmpty.text = when {
                searchQuery.isNotEmpty() -> "No appointments found for \"$searchQuery\""
                currentFilterCriteria.stylistId != null || currentFilterCriteria.serviceTypeId != null || currentFilterCriteria.status != null -> "No appointments found with the selected filters"
                else -> "No appointments scheduled for today"
            }
        }
    }


    private var isFabMenuOpen = false

    private fun setupFilterButton() {
        binding.filterButton.setOnClickListener {
            val filterBottomSheet = FilterBottomSheetFragment.newInstance(currentFilterCriteria)
            filterBottomSheet.onApplyFilter = { criteria ->
                currentFilterCriteria = criteria
                loadAppointments(currentFilterCriteria)
            }
            filterBottomSheet.show(parentFragmentManager, FilterBottomSheetFragment.TAG)
        }
    }

    private fun setupFab() {
        binding.fabMenu.setOnClickListener {
            if (isFabMenuOpen) {
                hideFabMenu()
            } else {
                showFabMenu()
            }
        }

        binding.fabAvailableSlots.setOnClickListener {
            findNavController().navigate(R.id.action_appointmentListFragment_to_availableSlotsFragment)
            hideFabMenu()
        }

        binding.fabPerformanceMetrics.setOnClickListener {
            findNavController().navigate(R.id.action_appointmentListFragment_to_stylistPerformanceFragment)
            hideFabMenu()
        }

        binding.fabTopServiceDashboard.setOnClickListener {
            findNavController().navigate(R.id.action_appointmentListFragment_to_serviceStatsFragment)
            hideFabMenu()
        }

        binding.fabDailyRevenueSummary.setOnClickListener {
            findNavController().navigate(R.id.action_appointmentListFragment_to_dailyRevenueFragment)
            hideFabMenu()
        }
    }

    private fun showFabMenu() {
        isFabMenuOpen = true
        binding.fabDailyRevenueSummary.visibility = View.VISIBLE
        binding.fabAvailableSlots.visibility = View.VISIBLE
        binding.fabPerformanceMetrics.visibility = View.VISIBLE
        binding.fabTopServiceDashboard.visibility = View.VISIBLE
        binding.fabMenu.setImageResource(R.drawable.ic_close) // Change icon to close
    }

    private fun hideFabMenu() {
        isFabMenuOpen = false
        binding.fabDailyRevenueSummary.visibility = View.GONE
        binding.fabAvailableSlots.visibility = View.GONE
        binding.fabPerformanceMetrics.visibility = View.GONE
        binding.fabTopServiceDashboard.visibility = View.GONE
        binding.fabMenu.setImageResource(R.drawable.ic_add) // Change icon back to add
    }

    override fun onConfirmClick(appointment: Appointment) {
        showConfirmationDialog(appointment, AppointmentStatus.CONFIRMED, "Confirm")
    }

    override fun onCancelClick(appointment: Appointment) {
        showConfirmationDialog(appointment, AppointmentStatus.CANCELLED, "Cancel")
    }

    override fun onCompleteClick(appointment: Appointment) {
        showConfirmationDialog(appointment, AppointmentStatus.COMPLETED, "Complete")
    }

    private fun showConfirmationDialog(appointment: Appointment, newStatus: AppointmentStatus, action: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("$action Appointment")
            .setMessage("Are you sure you want to $action ${appointment.clientName}'s appointment?")
            .setPositiveButton("Yes") { _, _ ->
                updateAppointmentStatus(appointment.id, newStatus)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun updateAppointmentStatus(appointmentId: Int, newStatus: AppointmentStatus) {
        Observable.fromCallable {
            apiService.updateAppointmentStatus(appointmentId, newStatus) ?: throw Exception("Failed to update appointment status.")
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { updatedAppointment: Appointment? ->
                    updatedAppointment?.let { updated ->
                        Toast.makeText(requireContext(), "Appointment status updated to ${updated.status.name}", Toast.LENGTH_SHORT).show()
                        loadAppointments() // Refresh the list
                    } ?: run {
                        Toast.makeText(requireContext(), "Failed to update appointment status.", Toast.LENGTH_SHORT).show()
                    }
                },
                onError = { error ->
                    Log.e(TAG, "updateAppointmentStatus: $error", error)
                    Toast.makeText(requireContext(), "Error updating appointment status. Err: $error", Toast.LENGTH_SHORT).show()
                }
            )
            .addTo(disposables)
    }

    override fun onDestroyView() {
        disposables.clear()
        super.onDestroyView()
        _binding = null
    }
}