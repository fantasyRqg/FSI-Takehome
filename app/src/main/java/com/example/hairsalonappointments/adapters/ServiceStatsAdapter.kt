package com.example.hairsalonappointments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hairsalonappointments.data.ServiceStats
import com.example.hairsalonappointments.databinding.ItemServiceStatsBinding

class ServiceStatsAdapter : ListAdapter<ServiceStats, ServiceStatsAdapter.ServiceStatsViewHolder>(ServiceStatsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceStatsViewHolder {
        val binding = ItemServiceStatsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ServiceStatsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceStatsViewHolder, position: Int) {
        val serviceStats = getItem(position)
        holder.bind(serviceStats)
    }

    class ServiceStatsViewHolder(private val binding: ItemServiceStatsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(serviceStats: ServiceStats) {
            binding.textViewServiceType.text = serviceStats.serviceType.name.replace("_", " ")
            binding.textViewBookingCount.text = "Bookings: ${serviceStats.bookingCount}"
            binding.textViewTotalRevenue.text = "Revenue: $%.2f".format(serviceStats.totalRevenue)
            binding.textViewPopularityRank.text = "Rank: ${serviceStats.popularityRank}"
        }
    }

    private class ServiceStatsDiffCallback : DiffUtil.ItemCallback<ServiceStats>() {
        override fun areItemsTheSame(oldItem: ServiceStats, newItem: ServiceStats): Boolean {
            return oldItem.serviceType == newItem.serviceType
        }

        override fun areContentsTheSame(oldItem: ServiceStats, newItem: ServiceStats): Boolean {
            return oldItem == newItem
        }
    }
}
