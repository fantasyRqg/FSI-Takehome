package com.example.hairsalonappointments.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hairsalonappointments.R
import com.example.hairsalonappointments.adapters.AppointmentDetailAdapter
import com.example.hairsalonappointments.data.Appointment
import com.example.hairsalonappointments.data.AppointmentDetailItem
import com.example.hairsalonappointments.data.MockApiService
import com.example.hairsalonappointments.databinding.FragmentAppointmentDetailBinding
import com.example.hairsalonappointments.utils.StickyHeaderItemDecoration
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Fragment for displaying appointment details
 *
 * TASK FOR CANDIDATE:
 * Implement the functionality to display appointment details.
 *
 * Requirements:
 * - Receive appointment ID from navigation arguments
 * - Load appointment details from MockApiService
 * - Display all appointment information
 * - Format data appropriately (time, price, duration)
 * - Handle case when appointment is not found
 */
class AppointmentDetailFragment : Fragment() {
    companion object {
        private val TAG = AppointmentDetailFragment::class.simpleName
    }

    private var _binding: FragmentAppointmentDetailBinding? = null
    private val binding get() = _binding!!

    private val args: AppointmentDetailFragmentArgs by navArgs()
    private val apiService by lazy { MockApiService() }


    private val disposables = CompositeDisposable()
    private lateinit var appointmentDetailAdapter: AppointmentDetailAdapter
    private val adapterItems = mutableListOf<AppointmentDetailItem>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appointmentDetailAdapter = AppointmentDetailAdapter()
        binding.recyclerViewAppointmentDetails.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = appointmentDetailAdapter
            addItemDecoration(StickyHeaderItemDecoration(this, appointmentDetailAdapter))
        }
        loadAppointmentDetails()
    }

    /**
     *
     * Load and display appointment details.
     * - Initialize MockApiService
     * - Get appointment by ID (from args.appointmentId)
     * - Display all appointment information in the UI
     * - Format time, date, price, and duration appropriately
     * - Show error state if appointment not found
     *
     * UI elements to update:
     * - textViewClientName
     * - textViewClientPhone
     * - textViewService
     * - textViewStylist
     * - textViewTime
     * - textViewDate
     * - textViewDuration
     * - textViewPrice
     * - textViewStatus
     * - textViewNotes (hide if no notes)
     */
    private fun loadAppointmentDetails() {
        Observable.fromCallable {
            apiService.getAppointmentById(args.appointmentId)
                ?: throw Exception("Appointment not found")
        }
            .subscribeOn(Schedulers.io())
            .map {
                val items = mutableListOf<AppointmentDetailItem>()
                items.add(AppointmentDetailItem.AppointmentSummary(getString(R.string.appointment_details_title)))
                items.add(AppointmentDetailItem.ClientDetails(it.clientName, it.clientPhone))
                items.add(AppointmentDetailItem.ServiceDetails(it.serviceType, it.stylistName))
                items.add(AppointmentDetailItem.AppointmentTimeDetails(it))
                if (it.notes.isNullOrEmpty()) {
                    items.add(AppointmentDetailItem.NotesDetails(it.notes))
                }

                loadPreviousVisits(it.clientPhone)

                items
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ items ->
                adapterItems.addAll(items)
                appointmentDetailAdapter.submitList(adapterItems)
            }, {
                Log.e(TAG, "loadAppointmentDetails: ", it)
                // Handle error state, e.g., show a message or navigate back
            })
            .addTo(disposables)
    }

    private fun loadPreviousVisits(clientPhone: String) {
        Observable.fromCallable {
            apiService.getClientHistory(clientPhone)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ clientHistory ->
                val items = adapterItems
                val insertPos = items.size
                if (clientHistory.isNotEmpty()) {
                    items.add(AppointmentDetailItem.PreviousVisitsHeader(getString(R.string.previous_visits)))
                    clientHistory.forEach { history ->
                        items.add(AppointmentDetailItem.ClientHistory(history))
                    }
                } else {
                    items.add(AppointmentDetailItem.NoPreviousVisits(getString(R.string.no_previous_visits_found_for_this_client)))
                }

                appointmentDetailAdapter.notifyItemRangeInserted(insertPos, items.size - insertPos)
            }, {
                Log.e(TAG, "loadPreviousVisits: ", it)
            })
            .addTo(disposables)
    }

    override fun onDestroyView() {
        disposables.clear()
        super.onDestroyView()
        _binding = null
    }
}