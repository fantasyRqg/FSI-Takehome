package com.example.hairsalonappointments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hairsalonappointments.databinding.ItemAvailableSlotBinding

class AvailableSlotAdapter(private val onBookClick: (String) -> Unit) :
    ListAdapter<String, AvailableSlotAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAvailableSlotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val slot = getItem(position)
        holder.bind(slot, onBookClick)
    }

    class ViewHolder(private val binding: ItemAvailableSlotBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(slot: String, onBookClick: (String) -> Unit) {
            binding.textViewSlotTime.text = slot
            binding.buttonBookSlot.setOnClickListener { onBookClick(slot) }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}