package com.example.hairsalonappointments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hairsalonappointments.data.Appointment
import com.example.hairsalonappointments.databinding.ItemAppointmentBinding
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * RecyclerView Adapter for displaying appointments
 * 
 * TASK FOR CANDIDATE:
 * Implement the required RecyclerView adapter methods.
 * 
 * Requirements:
 * - Properly inflate the item layout (item_appointment.xml)
 * - Bind appointment data to views
 * - Format time appropriately (e.g., "10:30 AM")
 * - Show client name, service type, stylist, and status
 * - Handle click events to navigate to detail view
 * - Use view binding (already set up in gradle)
 */
class AppointmentAdapter(
    private val onAppointmentClick: (Appointment) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {
    
    private var appointments = emptyList<Appointment>()
    private val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    
    /**
     * TODO: Implement this function
     * 
     * Create and return a ViewHolder.
     * - Inflate the item_appointment layout using view binding
     * - Return a new AppointmentViewHolder instance
     * 
     * @param parent The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * @return A new ViewHolder that holds a View for an appointment item
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        // TODO: Implement view holder creation
        // Hint: Use ItemAppointmentBinding.inflate(...)
        
        throw NotImplementedError("Candidate needs to implement onCreateViewHolder()")
    }
    
    /**
     * TODO: Implement this function
     * 
     * Bind appointment data to the views.
     * - Display client name
     * - Display service type (use displayName from ServiceType)
     * - Display stylist name
     * - Display time (format using timeFormatter)
     * - Display status with appropriate styling
     * - Set click listener to invoke onAppointmentClick
     * 
     * @param holder The ViewHolder which should be updated
     * @param position The position of the item within the adapter's data set
     */
    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        // TODO: Implement data binding
        // Get the appointment at this position
        // Bind all the data to the views
        // Set click listener
        
        throw NotImplementedError("Candidate needs to implement onBindViewHolder()")
    }
    
    override fun getItemCount(): Int = appointments.size
    
    /**
     * Update the adapter's data set
     * 
     * @param newAppointments The new list of appointments to display
     */
    fun submitList(newAppointments: List<Appointment>) {
        appointments = newAppointments
        notifyDataSetChanged()
    }
    
    /**
     * ViewHolder class for appointment items
     * 
     * @param binding The view binding for the item layout
     */
    inner class AppointmentViewHolder(
        val binding: ItemAppointmentBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        /**
         * Bind an appointment to this view holder
         * 
         * @param appointment The appointment to display
         * @param onClickListener The click listener for this item
         */
        fun bind(appointment: Appointment, onClickListener: (Appointment) -> Unit) {
            // This method can be used in onBindViewHolder
            // to keep the binding logic organized
        }
    }
}