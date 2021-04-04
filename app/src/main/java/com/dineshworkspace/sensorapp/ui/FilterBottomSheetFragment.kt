package com.dineshworkspace.sensorapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.dineshworkspace.sensorapp.R
import com.dineshworkspace.sensorapp.dataModels.BaseResponse
import com.dineshworkspace.sensorapp.dataModels.SelectedConfig
import com.dineshworkspace.sensorapp.dataModels.Sensor
import com.dineshworkspace.sensorapp.dataModels.Status
import com.dineshworkspace.sensorapp.viewModels.SensorViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_error.*
import kotlinx.android.synthetic.main.layout_filter_bottom_sheet.*

class FilterBottomSheetFragment : BottomSheetDialogFragment(), SensorSelectedCallback {

    private val sensorViewModel: SensorViewModel by activityViewModels()
    lateinit var filterOptionsAdapter: FilterOptionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_filter_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        sensorViewModel.sensorsList.observe(viewLifecycleOwner, {
            parseSensorData(it)
        })

        iv_close.setOnClickListener {
            dismiss()
        }

        rg_toggle.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_all -> sensorViewModel.onConfigUpdated(SelectedConfig.ALL)
                R.id.rb_minute -> sensorViewModel.onConfigUpdated(SelectedConfig.MINUTE)
                R.id.rb_recent -> sensorViewModel.onConfigUpdated(SelectedConfig.RECENT)
            }
        }

        sensorViewModel.selectedConfig.let {
            when (it) {
                SelectedConfig.ALL -> rg_toggle.check(R.id.rb_all)
                SelectedConfig.MINUTE -> rg_toggle.check(R.id.rb_minute)
                SelectedConfig.RECENT -> rg_toggle.check(R.id.rb_recent)
            }
        }
    }

    private fun parseSensorData(baseResponse: BaseResponse<java.util.ArrayList<Sensor>>) {
        when (baseResponse.status) {
            Status.LOADING -> showLoadingScreen()
            Status.ERROR -> showErrorScreen(baseResponse.message)
            Status.SUCCESS -> showSensorFiltersInUi(baseResponse.data)
        }
    }

    private fun showSensorFiltersInUi(data: java.util.ArrayList<Sensor>?) {
        shimmer_container.visibility = View.GONE
        shimmer_container.stopShimmer()
        rv_filter_options.visibility = View.VISIBLE
        filterOptionsAdapter.submitList(data)
        rg_toggle.visibility = View.VISIBLE
        cl_error_layout.visibility = View.GONE
    }

    private fun showErrorScreen(message: String?) {
        cl_error_layout.visibility = View.VISIBLE
        tv_err_msg.text = message
        shimmer_container.visibility = View.GONE
        shimmer_container.stopShimmer()
        rv_filter_options.visibility = View.GONE
        rg_toggle.visibility = View.GONE
    }

    private fun showLoadingScreen() {
        shimmer_container.visibility = View.VISIBLE
        shimmer_container.startShimmer()
        rv_filter_options.visibility = View.GONE
        cl_error_layout.visibility = View.GONE
        rg_toggle.visibility = View.GONE
    }

    private fun initAdapter() {
        filterOptionsAdapter = FilterOptionsAdapter()
        val mLayoutManager = LinearLayoutManager(context)
        rv_filter_options.setLayoutManager(mLayoutManager)
        rv_filter_options.setItemAnimator(DefaultItemAnimator())
        rv_filter_options.setAdapter(filterOptionsAdapter)
        filterOptionsAdapter.setOnSensorSelectedListener(this)
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    override fun onSensorSelected(sensor: Sensor) {
        var selectedSensors = sensorViewModel.selectedSensors.value
        if (selectedSensors == null) {
            selectedSensors = ArrayList()
        }
        selectedSensors.add(sensor)
        sensorViewModel.selectedSensors.postValue(selectedSensors)
    }
}