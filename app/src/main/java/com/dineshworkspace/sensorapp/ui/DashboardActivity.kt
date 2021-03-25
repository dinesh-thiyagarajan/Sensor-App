package com.dineshworkspace.sensorapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import com.dineshworkspace.sensorapp.R
import com.dineshworkspace.sensorapp.dataModels.BaseResponse
import com.dineshworkspace.sensorapp.dataModels.SocketResponse
import com.dineshworkspace.sensorapp.dataModels.Status
import com.dineshworkspace.sensorapp.viewModels.SensorViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject


@AndroidEntryPoint
class DashboardActivity : BaseActivity(layoutId = R.layout.activity_dashboard) {

    private val sensorViewModel: SensorViewModel by viewModels()

    @Inject
    lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorViewModel.sensorsList.observe(this, {
            updateUiForSensorListResponse(it)
        })

        baseSocket.emit("subscribe", "temperature0")

        baseSocket.on("data") {
            val json = it[0] as JSONObject
            val socketResponse = gson.fromJson(json.toString(), SocketResponse::class.java)
            updateUiForSocketResponse(socketResponse)
        }
    }

    private fun updateUiForSocketResponse(socketResponse: SocketResponse?) {

    }


    private fun updateUiForSensorListResponse(baseResponse: BaseResponse<ArrayList<String>>) {
        when (baseResponse.status) {
            Status.LOADING -> showLoadingScreen()
            Status.ERROR -> showErrorScreen(baseResponse.message)
            Status.SUCCESS -> showSensorFiltersInUi(baseResponse.data)
        }
    }

    private fun showSensorFiltersInUi(data: java.util.ArrayList<String>?) {

    }

    private fun showErrorScreen(message: String?) {
        showSnackBar(message)
    }


    private fun showLoadingScreen() {

    }

}