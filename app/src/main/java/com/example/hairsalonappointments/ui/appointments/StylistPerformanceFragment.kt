package com.example.hairsalonappointments.ui.appointments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hairsalonappointments.adapters.StylistStatsAdapter
import com.example.hairsalonappointments.data.MockApiService
import com.example.hairsalonappointments.data.StylistStats
import com.example.hairsalonappointments.data.StylistSummaryStats
import com.example.hairsalonappointments.databinding.FragmentStylistPerformanceBinding
import com.example.hairsalonappointments.adapters.StylistPerformanceItem
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class StylistPerformanceFragment : Fragment() {

    private var _binding: FragmentStylistPerformanceBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: StylistStatsAdapter
    private val apiService by lazy { MockApiService() }
    private val disposables = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStylistPerformanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadStylistStats()
    }

    private fun setupRecyclerView() {
        adapter = StylistStatsAdapter()
        binding.recyclerViewStylistStats.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@StylistPerformanceFragment.adapter
        }
    }

    private fun loadStylistStats() {
        Observable.fromCallable {
            apiService.getStylistStats()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    val items = mutableListOf<StylistPerformanceItem>()

                    // Calculate summary stats
                    val totalStylists = it.size
                    val totalAppointments = it.sumOf { it.appointmentCount }
                    val totalRevenue = it.sumOf { it.totalRevenue }
                    val averageRatingOverall = if (it.isNotEmpty()) it.map { it.averageRating }.average().toFloat() else 0.0f

                    items.add(StylistPerformanceItem.StylistSummaryItem(
                        StylistSummaryStats(totalStylists, totalAppointments, totalRevenue, averageRatingOverall)
                    ))

                    // Add individual stylist stats
                    items.addAll(it.map { StylistPerformanceItem.StylistStatsItem(it) })

                    adapter.submitList(items)
                    binding.textViewEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
                    binding.recyclerViewStylistStats.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
                },
                onError = {
                    Log.e(TAG, "Error loading stylist stats: $it", it)
                    Toast.makeText(requireContext(), "Error loading stylist stats", Toast.LENGTH_SHORT).show()
                    binding.textViewEmpty.visibility = View.VISIBLE
                    binding.recyclerViewStylistStats.visibility = View.GONE
                }
            )
            .addTo(disposables)
    }

    override fun onDestroyView() {
        disposables.clear()
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val TAG = StylistPerformanceFragment::class.simpleName
    }
}
