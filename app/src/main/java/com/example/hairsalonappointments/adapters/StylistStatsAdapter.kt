package com.example.hairsalonappointments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hairsalonappointments.data.StylistStats
import com.example.hairsalonappointments.data.StylistSummaryStats
import com.example.hairsalonappointments.databinding.ItemStylistStatsBinding
import com.example.hairsalonappointments.databinding.ItemStylistSummaryBinding

sealed class StylistPerformanceItem {
    data class StylistStatsItem(val stylistStats: StylistStats) : StylistPerformanceItem()
    data class StylistSummaryItem(val summaryStats: StylistSummaryStats) : StylistPerformanceItem()
}

class StylistStatsAdapter : ListAdapter<StylistPerformanceItem, RecyclerView.ViewHolder>(StylistPerformanceDiffCallback()) {

    private val TYPE_SUMMARY = 0
    private val TYPE_STYLIST_STATS = 1

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is StylistPerformanceItem.StylistSummaryItem -> TYPE_SUMMARY
            is StylistPerformanceItem.StylistStatsItem -> TYPE_STYLIST_STATS
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_SUMMARY -> {
                val binding = ItemStylistSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                StylistSummaryViewHolder(binding)
            }
            TYPE_STYLIST_STATS -> {
                val binding = ItemStylistStatsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                StylistStatsViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StylistSummaryViewHolder -> holder.bind((getItem(position) as StylistPerformanceItem.StylistSummaryItem).summaryStats)
            is StylistStatsViewHolder -> holder.bind((getItem(position) as StylistPerformanceItem.StylistStatsItem).stylistStats)
        }
    }

    class StylistStatsViewHolder(private val binding: ItemStylistStatsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(stylistStats: StylistStats) {
            binding.textViewStylistName.text = stylistStats.stylistName
            binding.textViewAppointmentCount.text = "Appointments: ${stylistStats.appointmentCount}"
            binding.textViewTotalRevenue.text = "Revenue: $%.2f".format(stylistStats.totalRevenue)
            binding.textViewAverageRating.text = "Rating: %.1f".format(stylistStats.averageRating)
            binding.textViewSpecialties.text = "Specialties: ${stylistStats.specialties.joinToString { it.name }}"
        }
    }

    class StylistSummaryViewHolder(private val binding: ItemStylistSummaryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(summaryStats: StylistSummaryStats) {
            binding.textViewTotalStylists.text = "Total Stylists: ${summaryStats.totalStylists}"
            binding.textViewTotalAppointments.text = "Total Appointments: ${summaryStats.totalAppointments}"
            binding.textViewTotalRevenueSummary.text = "Total Revenue: $%.2f".format(summaryStats.totalRevenue)
            binding.textViewAverageRatingOverall.text = "Overall Average Rating: %.1f".format(summaryStats.averageRatingOverall)
        }
    }

    private class StylistPerformanceDiffCallback : DiffUtil.ItemCallback<StylistPerformanceItem>() {
        override fun areItemsTheSame(oldItem: StylistPerformanceItem, newItem: StylistPerformanceItem): Boolean {
            return when {
                oldItem is StylistPerformanceItem.StylistStatsItem && newItem is StylistPerformanceItem.StylistStatsItem ->
                    oldItem.stylistStats.stylistName == newItem.stylistStats.stylistName
                oldItem is StylistPerformanceItem.StylistSummaryItem && newItem is StylistPerformanceItem.StylistSummaryItem ->
                    true // Only one summary item
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: StylistPerformanceItem, newItem: StylistPerformanceItem): Boolean {
            return oldItem == newItem
        }
    }
}
