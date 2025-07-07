package com.example.hairsalonappointments.ui.appointments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.example.hairsalonappointments.data.AppointmentStatus
import com.example.hairsalonappointments.data.FilterCriteria
import com.example.hairsalonappointments.data.MockApiService
import com.example.hairsalonappointments.databinding.FragmentFilterBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class FilterBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentFilterBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val apiService by lazy { MockApiService() }
    private val disposables = CompositeDisposable()

    var onApplyFilter: ((FilterCriteria) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val initialFilter = arguments?.getParcelable<FilterCriteria>(ARG_FILTER_CRITERIA)
        setupDropdowns(initialFilter)

        binding.applyFilterButton.setOnClickListener {
            val stylist = binding.stylistFilterAutoComplete.text.toString().takeIf { it.isNotEmpty() }
            val serviceType = binding.serviceTypeFilterAutoComplete.text.toString().takeIf { it.isNotEmpty() }
            val status = binding.statusFilterAutoComplete.text.toString().takeIf { it.isNotEmpty() }

            val filterCriteria = FilterCriteria(stylist, serviceType, status)
            onApplyFilter?.invoke(filterCriteria)
            dismiss()
        }

        binding.clearFilterButton.setOnClickListener {
            binding.stylistFilterAutoComplete.setText("", false)
            binding.serviceTypeFilterAutoComplete.setText("", false)
            binding.statusFilterAutoComplete.setText("", false)
            onApplyFilter?.invoke(FilterCriteria())
            dismiss()
        }
    }

    private fun setupDropdowns(initialFilter: FilterCriteria?) {
        // Fetch stylists
        Observable.fromCallable { apiService.getStylists() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    val stylistNames = it.map { stylist -> stylist.name }.toTypedArray()
                    val stylistAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, stylistNames)
                    (binding.stylistFilterLayout.editText as? AutoCompleteTextView)?.setAdapter(stylistAdapter)
                    initialFilter?.stylistId?.let { id ->
                        binding.stylistFilterAutoComplete.setText(id, false)
                    }
                },
                onError = { Log.e(TAG, "Error fetching stylists: $it") }
            )
            .addTo(disposables)

        // Fetch service types
        Observable.fromCallable { apiService.getServiceTypes() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    val serviceTypeNames = it.map { serviceType -> serviceType.name }.toTypedArray()
                    val serviceTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, serviceTypeNames)
                    (binding.serviceTypeFilterLayout.editText as? AutoCompleteTextView)?.setAdapter(serviceTypeAdapter)
                    initialFilter?.serviceTypeId?.let { id ->
                        binding.serviceTypeFilterAutoComplete.setText(id, false)
                    }
                },
                onError = { Log.e(TAG, "Error fetching service types: $it") }
            )
            .addTo(disposables)

        val statuses = AppointmentStatus.entries.map { it.name }.toTypedArray()
        val statusAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, statuses)
        (binding.statusFilterLayout.editText as? AutoCompleteTextView)?.setAdapter(statusAdapter)
        initialFilter?.status?.let { status ->
            binding.statusFilterAutoComplete.setText(status, false)
        }
    }

    override fun onDestroyView() {
        disposables.clear()
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "FilterBottomSheetFragment"
        private const val ARG_FILTER_CRITERIA = "filter_criteria"

        fun newInstance(filterCriteria: FilterCriteria): FilterBottomSheetFragment {
            val fragment = FilterBottomSheetFragment()
            val args = Bundle()
            args.putParcelable(ARG_FILTER_CRITERIA, filterCriteria)
            fragment.arguments = args
            return fragment
        }
    }
}
