package com.example.hairsalonappointments.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.hairsalonappointments.data.Appointment
import com.example.hairsalonappointments.data.AppointmentStatus
import com.example.hairsalonappointments.data.MockApiService
import com.example.hairsalonappointments.databinding.FragmentAppointmentDetailBinding
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

    private val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    private val dateFormatter = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())

    private val disposables = CompositeDisposable()

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
        // 1. Initialize MockApiService
        // 2. Get appointment by ID from args
        // 3. Update all UI elements with appointment data
        // 4. Handle null case (appointment not found)

        Observable.fromCallable {
            apiService.getAppointmentById(args.appointmentId) ?: throw Exception("Appointment not found")
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ appointment ->
                displayAppointment(appointment)
            }, {
                Log.e(TAG, "loadAppointmentDetails: ", it)
                showErrorState()
            })
            .addTo(disposables)
    }
    
    /**
     * Helper function to display appointment data
     * 
     * @param appointment The appointment to display
     */
    private fun displayAppointment(appointment: Appointment) {
        binding.apply {
            textViewClientName.text = appointment.clientName
            textViewClientPhone.text = appointment.clientPhone
            textViewService.text = appointment.serviceType.displayName
            textViewStylist.text = appointment.stylistName
            textViewTime.text = timeFormatter.format(appointment.appointmentTime)
            textViewDate.text = dateFormatter.format(appointment.appointmentTime)
            textViewDuration.text = "${appointment.serviceType.duration} minutes"
            textViewPrice.text = "$${String.format("%.2f", appointment.serviceType.price)}"
            
            // Status with appropriate styling
            textViewStatus.text = appointment.status.name.replace("_", " ")
            
            // Notes
            if (appointment.notes.isNullOrEmpty()) {
                textViewNotesLabel.visibility = View.GONE
                textViewNotes.visibility = View.GONE
            } else {
                textViewNotesLabel.visibility = View.VISIBLE
                textViewNotes.visibility = View.VISIBLE
                textViewNotes.text = appointment.notes
            }
        }
    }
    
    /**
     * Show error state when appointment is not found
     */
    private fun showErrorState() {
        // Hide all content and show error message
        binding.scrollView.visibility = View.GONE
        // You might want to add an error TextView in the layout
        // For now, we'll just hide the content
    }
    
    override fun onDestroyView() {
        disposables.clear()
        super.onDestroyView()
        _binding = null
    }
}