package com.dineshworkspace.sensorapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.dineshworkspace.sensorapp.R
import com.dineshworkspace.sensorapp.viewModels.SensorViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_filter_bottom_sheet.*

class FilterBottomSheetFragment : BottomSheetDialogFragment() {

    private val sensorViewModel: SensorViewModel by activityViewModels()
    lateinit var filterOptionsAdapter: FilterOptionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.layout_filter_bottom_sheet, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        sensorViewModel.sensorsList.observe(viewLifecycleOwner, {
            it.let {
                filterOptionsAdapter.submitList(it.data)
            }
        })
    }

    private fun initAdapter() {
        filterOptionsAdapter = FilterOptionsAdapter()
        val mLayoutManager = LinearLayoutManager(context)
        rv_filter_options.setLayoutManager(mLayoutManager)
        rv_filter_options.setItemAnimator(DefaultItemAnimator())
        rv_filter_options.setAdapter(filterOptionsAdapter)
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }
}