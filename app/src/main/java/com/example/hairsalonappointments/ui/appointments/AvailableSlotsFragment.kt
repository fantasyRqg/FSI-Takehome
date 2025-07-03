package com.example.hairsalonappointments.ui.appointments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hairsalonappointments.adapters.AvailableSlotAdapter
import com.example.hairsalonappointments.data.MockApiService
import com.example.hairsalonappointments.databinding.FragmentAvailableSlotsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class AvailableSlotsFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAvailableSlotsBinding? = null
    private val binding get() = _binding!!

    private lateinit var availableSlotAdapter: AvailableSlotAdapter
    private val apiService by lazy { MockApiService() }
    private val disposables = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAvailableSlotsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadAvailableSlots()
    }

    private fun setupRecyclerView() {
        availableSlotAdapter = AvailableSlotAdapter { slot ->
            // For now, just show a toast message
            Toast.makeText(requireContext(), "Slot booked: $slot", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        binding.recyclerViewAvailableSlots.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AvailableSlotsFragment.availableSlotAdapter
        }
    }

    private fun loadAvailableSlots() {
        Observable.fromCallable {
            apiService.getAvailableSlots()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    availableSlotAdapter.submitList(it)
                },
                onError = {
                    Log.e("AvailableSlotsFragment", "loadAvailableSlots: $it", it)
                    Toast.makeText(requireContext(), "Error loading available slots. Err: $it", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            )
            .addTo(disposables)
    }

    override fun onDestroyView() {
        disposables.clear()
        super.onDestroyView()
        _binding = null
    }
}