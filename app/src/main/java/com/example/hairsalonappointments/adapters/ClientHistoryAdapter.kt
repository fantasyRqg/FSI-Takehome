package com.example.hairsalonappointments.adapters

import android.view.View

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hairsalonappointments.data.ClientHistoryItem
import com.example.hairsalonappointments.databinding.ItemClientHistoryBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ClientHistoryAdapter : ListAdapter<ClientHistoryItem, ClientHistoryAdapter.ClientHistoryViewHolder>(ClientHistoryDiffCallback()) {

    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientHistoryViewHolder {
        val binding = ItemClientHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClientHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClientHistoryViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, dateFormatter, timeFormatter)
    }

    class ClientHistoryViewHolder(private val binding: ItemClientHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ClientHistoryItem, dateFormatter: SimpleDateFormat, timeFormatter: SimpleDateFormat) {
            binding.textViewHistoryDate.text = dateFormatter.format(item.appointment.appointmentTime)
            binding.textViewHistoryService.text = item.appointment.serviceType.displayName
            binding.textViewHistoryStylist.text = item.appointment.stylistName
            binding.textViewHistoryTime.text = timeFormatter.format(item.appointment.appointmentTime)
            binding.textViewHistoryPrice.text = "$%.2f".format(item.appointment.serviceType.price)
            binding.textViewHistoryStatus.text = item.appointment.status.name.replace("_", " ")

            if (!item.feedback.isNullOrEmpty()) {
                binding.textViewHistoryFeedback.visibility = View.VISIBLE
                binding.textViewHistoryFeedback.text = "Feedback: ${item.feedback}"
            } else {
                binding.textViewHistoryFeedback.visibility = View.GONE
            }
        }
    }

    private class ClientHistoryDiffCallback : DiffUtil.ItemCallback<ClientHistoryItem>() {
        override fun areItemsTheSame(oldItem: ClientHistoryItem, newItem: ClientHistoryItem): Boolean {
            return oldItem.appointment.id == newItem.appointment.id
        }

        override fun areContentsTheSame(oldItem: ClientHistoryItem, newItem: ClientHistoryItem): Boolean {
            return oldItem == newItem
        }
    }
}
