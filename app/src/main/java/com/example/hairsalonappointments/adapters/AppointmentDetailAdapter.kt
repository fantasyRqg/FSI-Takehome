package com.example.hairsalonappointments.adapters

import android.view.View
import android.widget.TextView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hairsalonappointments.R
import com.example.hairsalonappointments.data.Appointment
import com.example.hairsalonappointments.data.AppointmentDetailItem
import com.example.hairsalonappointments.databinding.ItemAppointmentSummaryBinding
import com.example.hairsalonappointments.databinding.ItemClientDetailsBinding
import com.example.hairsalonappointments.databinding.ItemServiceDetailsBinding
import com.example.hairsalonappointments.databinding.ItemAppointmentTimeDetailsBinding
import com.example.hairsalonappointments.databinding.ItemNotesDetailsBinding
import com.example.hairsalonappointments.databinding.ItemPreviousVisitsHeaderBinding
import com.example.hairsalonappointments.databinding.ItemClientHistoryBinding
import com.example.hairsalonappointments.databinding.ItemNoPreviousVisitsBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AppointmentDetailAdapter : ListAdapter<AppointmentDetailItem, RecyclerView.ViewHolder>(AppointmentDetailDiffCallback()) {

    private val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    private val dateFormatter = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())

    override fun getItemViewType(position: Int): Int {
        return when (super.getItem(position)) {
            is AppointmentDetailItem.AppointmentSummary -> VIEW_TYPE_APPOINTMENT_SUMMARY
            is AppointmentDetailItem.ClientDetails -> VIEW_TYPE_CLIENT_DETAILS
            is AppointmentDetailItem.ServiceDetails -> VIEW_TYPE_SERVICE_DETAILS
            is AppointmentDetailItem.AppointmentTimeDetails -> VIEW_TYPE_APPOINTMENT_TIME_DETAILS
            is AppointmentDetailItem.NotesDetails -> VIEW_TYPE_NOTES_DETAILS
            is AppointmentDetailItem.PreviousVisitsHeader -> VIEW_TYPE_PREVIOUS_VISITS_HEADER
            is AppointmentDetailItem.ClientHistory -> VIEW_TYPE_CLIENT_HISTORY
            is AppointmentDetailItem.NoPreviousVisits -> VIEW_TYPE_NO_PREVIOUS_VISITS
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_APPOINTMENT_SUMMARY -> {
                val binding = ItemAppointmentSummaryBinding.inflate(inflater, parent, false)
                AppointmentSummaryViewHolder(binding)
            }
            VIEW_TYPE_CLIENT_DETAILS -> {
                val binding = ItemClientDetailsBinding.inflate(inflater, parent, false)
                ClientDetailsViewHolder(binding)
            }
            VIEW_TYPE_SERVICE_DETAILS -> {
                val binding = ItemServiceDetailsBinding.inflate(inflater, parent, false)
                ServiceDetailsViewHolder(binding)
            }
            VIEW_TYPE_APPOINTMENT_TIME_DETAILS -> {
                val binding = ItemAppointmentTimeDetailsBinding.inflate(inflater, parent, false)
                AppointmentTimeDetailsViewHolder(binding)
            }
            VIEW_TYPE_NOTES_DETAILS -> {
                val binding = ItemNotesDetailsBinding.inflate(inflater, parent, false)
                NotesDetailsViewHolder(binding)
            }
            VIEW_TYPE_PREVIOUS_VISITS_HEADER -> {
                val binding = ItemPreviousVisitsHeaderBinding.inflate(inflater, parent, false)
                PreviousVisitsHeaderViewHolder(binding)
            }
            VIEW_TYPE_CLIENT_HISTORY -> {
                val binding = ItemClientHistoryBinding.inflate(inflater, parent, false)
                ClientHistoryViewHolder(binding)
            }
            VIEW_TYPE_NO_PREVIOUS_VISITS -> {
                val binding = ItemNoPreviousVisitsBinding.inflate(inflater, parent, false)
                NoPreviousVisitsViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is AppointmentDetailItem.AppointmentSummary -> (holder as AppointmentSummaryViewHolder).bind(item)
            is AppointmentDetailItem.ClientDetails -> (holder as ClientDetailsViewHolder).bind(item)
            is AppointmentDetailItem.ServiceDetails -> (holder as ServiceDetailsViewHolder).bind(item)
            is AppointmentDetailItem.AppointmentTimeDetails -> (holder as AppointmentTimeDetailsViewHolder).bind(item)
            is AppointmentDetailItem.NotesDetails -> (holder as NotesDetailsViewHolder).bind(item)
            is AppointmentDetailItem.PreviousVisitsHeader -> (holder as PreviousVisitsHeaderViewHolder).bind(item)
            is AppointmentDetailItem.ClientHistory -> (holder as ClientHistoryViewHolder).bind(item.clientHistory)
            is AppointmentDetailItem.NoPreviousVisits -> (holder as NoPreviousVisitsViewHolder).bind(item)
        }
    }

    inner class AppointmentSummaryViewHolder(private val binding: ItemAppointmentSummaryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AppointmentDetailItem.AppointmentSummary) {
            binding.textViewDetailTitle.text = item.title
        }
    }

    inner class ClientDetailsViewHolder(private val binding: ItemClientDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AppointmentDetailItem.ClientDetails) {
            binding.textViewClientName.text = item.clientName
            binding.textViewClientPhone.text = item.clientPhone
        }
    }

    inner class ServiceDetailsViewHolder(private val binding: ItemServiceDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AppointmentDetailItem.ServiceDetails) {
            binding.textViewService.text = item.serviceType.displayName
            binding.textViewStylist.text = item.stylistName
            binding.textViewDuration.text = binding.root.context.getString(R.string.service_duration_minutes, item.serviceType.duration)
            binding.textViewPrice.text = String.format("$%.2f", item.serviceType.price)
        }
    }

    inner class AppointmentTimeDetailsViewHolder(private val binding: ItemAppointmentTimeDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AppointmentDetailItem.AppointmentTimeDetails) {
            binding.textViewDate.text = dateFormatter.format(item.appointment.appointmentTime)
            binding.textViewTime.text = timeFormatter.format(item.appointment.appointmentTime)
            binding.textViewStatus.text = item.appointment.status.name.replace("_", " ")
        }
    }

    inner class NotesDetailsViewHolder(private val binding: ItemNotesDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AppointmentDetailItem.NotesDetails) {
            if (item.notes.isNullOrEmpty()) {
                binding.textViewNotesLabel.visibility = View.GONE
                binding.textViewNotes.visibility = View.GONE
            } else {
                binding.textViewNotesLabel.visibility = View.VISIBLE
                binding.textViewNotes.visibility = View.VISIBLE
                binding.textViewNotes.text = item.notes
            }
        }
    }

    inner class PreviousVisitsHeaderViewHolder(private val binding: ItemPreviousVisitsHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AppointmentDetailItem.PreviousVisitsHeader) {
            binding.root.findViewById<TextView>(R.id.previous_visits_header_text_view).text = item.title
        }
    }

    inner class ClientHistoryViewHolder(private val binding: ItemClientHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Appointment) {
            binding.textViewHistoryService.text = item.serviceType.displayName
            binding.textViewHistoryDate.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(item.appointmentTime)
            binding.textViewHistoryPrice.text = String.format("$%.2f", item.serviceType.price)
            binding.textViewHistoryStylist.text = item.stylistName
            binding.textViewHistoryTime.text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(item.appointmentTime)
            binding.textViewHistoryStatus.text = item.status.name.replace("_", " ")
            binding.textViewHistoryFeedback.text = item.notes ?: "No feedback"
        }
    }

    inner class NoPreviousVisitsViewHolder(private val binding: ItemNoPreviousVisitsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AppointmentDetailItem.NoPreviousVisits) {
            binding.textViewNoPreviousVisits.text = item.message
        }
    }

    companion object {
        private const val VIEW_TYPE_APPOINTMENT_SUMMARY = 0
        private const val VIEW_TYPE_CLIENT_DETAILS = 1
        private const val VIEW_TYPE_SERVICE_DETAILS = 2
        private const val VIEW_TYPE_APPOINTMENT_TIME_DETAILS = 3
        private const val VIEW_TYPE_NOTES_DETAILS = 4
        private const val VIEW_TYPE_PREVIOUS_VISITS_HEADER = 5
        private const val VIEW_TYPE_CLIENT_HISTORY = 6
        private const val VIEW_TYPE_NO_PREVIOUS_VISITS = 7
    }

    private class AppointmentDetailDiffCallback : DiffUtil.ItemCallback<AppointmentDetailItem>() {
        override fun areItemsTheSame(oldItem: AppointmentDetailItem, newItem: AppointmentDetailItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AppointmentDetailItem, newItem: AppointmentDetailItem): Boolean {
            return oldItem == newItem
        }
    }
}