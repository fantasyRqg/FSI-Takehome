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
class AppointmentListFragment : Fragment() {
    companion object {
        private val TAG = AppointmentListFragment::class.simpleName
    }

    private var _binding: FragmentAppointmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AppointmentAdapter
    private val apiService by lazy { MockApiService() }
    private var allAppointments = emptyList<Appointment>()
    private var showingAllAppointments = true
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

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
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
    private fun loadAppointments() {
        // 1. Initialize MockApiService
        // 2. Get today's appointments
        // 3. Store in allAppointments
        // 4. Update the RecyclerView
        // 5. Handle empty state

        Observable.fromCallable {
            apiService.getTodaysAppointments()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    allAppointments = it
                    updateAppointmentList()
                },
                onError = {
                    Log.e(TAG, "loadAppointments: $it", it)
                    Toast.makeText(requireContext(), "Error loading appointments. Err: $it", Toast.LENGTH_SHORT).show()
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
    private fun onAppointmentClick(appointment: Appointment) {
        // Use findNavController() and navigate with appointment ID

        findNavController().navigate(R.id.action_appointmentListFragment_to_appointmentDetailFragment, Bundle().apply {
            putInt("appointmentId", appointment.id)
        })
//        AppointmentListFragmentDirections.actionAppointmentListFragmentToAppointmentDetailFragment(appointment.id)
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
        }.filter {
            if (searchQuery.isEmpty()) {
                true
            } else {
                it.clientName.contains(searchQuery, ignoreCase = true) ||
                        it.stylistName.contains(searchQuery, ignoreCase = true)
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
            binding.textViewEmpty.text = when {
                searchQuery.isNotEmpty() -> "No appointments found for \"$searchQuery\""
                showingAllAppointments -> "No appointments scheduled for today"
                else -> "No available appointments"
            }
        }
    }


    private fun setupFab() {
        binding.fabShowAvailableSlots.setOnClickListener {
            findNavController().navigate(R.id.action_appointmentListFragment_to_availableSlotsFragment)
        }
    }

    override fun onDestroyView() {
        disposables.clear()
        super.onDestroyView()
        _binding = null
    }
}