package com.example.hairsalonappointments.ui.dailyrevenue

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.hairsalonappointments.R
import com.example.hairsalonappointments.data.MockApiService
import com.example.hairsalonappointments.databinding.FragmentDailyRevenueBinding
import kotlinx.coroutines.launch

class DailyRevenueFragment : Fragment() {

    private var _binding: FragmentDailyRevenueBinding? = null
    private val binding get() = _binding!!

    private val apiService = MockApiService() // Use your actual API service

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDailyRevenueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadDailyRevenue()
    }

    private fun loadDailyRevenue() {
        lifecycleScope.launch {
            try {
                val dailyRevenue = apiService.getDailyRevenue()
                binding.textViewTotalRevenue.text =
                    getString(R.string.total_revenue_2f).format(dailyRevenue.totalRevenue)
                binding.textViewAppointmentCount.text =
                    getString(R.string.appointment_count, dailyRevenue.appointmentCount)

                // Populate Revenue by Service table
                binding.tableLayoutRevenueByService.removeAllViews() // Clear existing views
                // Add header row
                val serviceHeaderRow = TableRow(context)
                val serviceNameHeader = TextView(context).apply { text = "Service"; setTypeface(null, android.graphics.Typeface.BOLD) }
                val serviceRevenueHeader = TextView(context).apply { text = "Revenue"; setTypeface(null, android.graphics.Typeface.BOLD); gravity = Gravity.END }
                serviceHeaderRow.addView(serviceNameHeader, TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f))
                serviceHeaderRow.addView(serviceRevenueHeader, TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f))
                binding.tableLayoutRevenueByService.addView(serviceHeaderRow)

                dailyRevenue.revenueByService.entries.sortedByDescending { it.value }.forEach {
                    val row = TableRow(context)
                    val serviceName = TextView(context).apply { text = it.key.name.replace("_", " ") }
                    val serviceRevenue = TextView(context).apply { text = "$%.2f".format(it.value); gravity = Gravity.END }
                    row.addView(serviceName, TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f))
                    row.addView(serviceRevenue, TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f))
                    binding.tableLayoutRevenueByService.addView(row)
                }

                // Populate Revenue by Stylist table
                binding.tableLayoutRevenueByStylist.removeAllViews() // Clear existing views
                // Add header row
                val stylistHeaderRow = TableRow(context)
                val stylistNameHeader = TextView(context).apply { text = "Stylist"; setTypeface(null, android.graphics.Typeface.BOLD) }
                val stylistRevenueHeader = TextView(context).apply { text = "Revenue"; setTypeface(null, android.graphics.Typeface.BOLD); gravity = Gravity.END }
                stylistHeaderRow.addView(stylistNameHeader, TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f))
                stylistHeaderRow.addView(stylistRevenueHeader, TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f))
                binding.tableLayoutRevenueByStylist.addView(stylistHeaderRow)

                dailyRevenue.revenueByStylist.entries.sortedByDescending { it.value }.forEach {
                    val row = TableRow(context)
                    val stylistName = TextView(context).apply { text = it.key }
                    val stylistRevenue = TextView(context).apply { text = "$%.2f".format(it.value); gravity = Gravity.END }
                    row.addView(stylistName, TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f))
                    row.addView(stylistRevenue, TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f))
                    binding.tableLayoutRevenueByStylist.addView(row)
                }

            } catch (e: Exception) {
                binding.textViewTotalRevenue.text =
                    getString(R.string.error_loading_daily_revenue, e.message)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}