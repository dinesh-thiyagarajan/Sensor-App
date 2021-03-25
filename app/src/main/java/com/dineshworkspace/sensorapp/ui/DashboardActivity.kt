package com.dineshworkspace.sensorapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dineshworkspace.sensorapp.R
import com.dineshworkspace.sensorapp.SensorViewModel
import com.dineshworkspace.sensorapp.dataModels.BaseResponse
import com.dineshworkspace.sensorapp.dataModels.Status
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.Socket
import javax.inject.Inject


@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    @Inject
    lateinit var baseSocket: Socket
    private val sensorViewModel: SensorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        sensorViewModel.sensorsList.observe(this, {
            updateUiForSensorListResponse(it)
        })
    }

    private fun updateUiForSensorListResponse(baseResponse: BaseResponse<ArrayList<String>>) {
        when (baseResponse.status) {
            Status.LOADING -> showLoadingScreen()
            Status.ERROR -> showErrorScreen()
            Status.SUCCESS -> showSensorFiltersInUi(baseResponse.data)
        }
    }

    private fun showSensorFiltersInUi(data: java.util.ArrayList<String>?) {

    }

    private fun showErrorScreen() {

    }

    private fun showLoadingScreen() {

    }
}