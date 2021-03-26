package com.dineshworkspace.sensorapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import com.dineshworkspace.sensorapp.AppConstants
import com.dineshworkspace.sensorapp.R
import com.dineshworkspace.sensorapp.dataModels.Sensor
import com.dineshworkspace.sensorapp.dataModels.SocketResponse
import com.dineshworkspace.sensorapp.dataModels.SubscriptionStatus
import com.dineshworkspace.sensorapp.viewModels.SensorViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_dashboard.*
import org.json.JSONObject
import javax.inject.Inject


@AndroidEntryPoint
class DashboardActivity : BaseActivity(layoutId = R.layout.activity_dashboard) {

    private val sensorViewModel: SensorViewModel by viewModels()

    @Inject
    lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorViewModel.selectedSensors.observe(this, { it ->
            it.let { sensorList ->
                sensorList.forEach {
                    when (it.subscriptionStatus) {
                        SubscriptionStatus.TO_BE_SUBSCRIBED -> subscribeSensor(it)
                        SubscriptionStatus.UN_SUBSCRIBED -> unsubscribeSensor(it)
                    }
                }
            }
        })

        baseSocket.on("data") {
            parseSensorResponse(it)
        }

        iv_filter.setOnClickListener {
            iv_filter.isClickable = false
            showBottomSheetDialog()
        }
    }

    private fun unsubscribeSensor(sensor: Sensor) {
        baseSocket.emit("unsubscribe", sensor.sensorName)
        sensor.subscriptionStatus = SubscriptionStatus.UN_SUBSCRIBED
    }

    private fun subscribeSensor(sensor: Sensor) {
        baseSocket.emit("subscribe", sensor.sensorName)
        sensor.subscriptionStatus = SubscriptionStatus.SUBSCRIBED
    }

    private fun parseSensorResponse(it: Array<Any>?) {
        it.let { result ->
            result?.get(0).let {
                val jsonObject = it as JSONObject
                val socketResponse =
                    gson.fromJson(jsonObject.toString(), SocketResponse::class.java)
                updateUiForSocketResponse(socketResponse)
            }
        }
    }

    private fun updateUiForSocketResponse(socketResponse: SocketResponse?) {
        socketResponse.let { response ->
            when (response?.type) {
                AppConstants.RES_TYPE_INIT -> onInitDataReceived(response)
                AppConstants.RES_TYPE_UPDATE -> onUpdateDataReceived(response)
                AppConstants.RES_TYPE_DELETE -> onDeleteDataReceived(response)
                else -> showSnackBarWithAutoClosure(getString(R.string.something_went_wrong))
            }
        }
    }

    private fun onDeleteDataReceived(response: SocketResponse) {

    }

    private fun onUpdateDataReceived(response: SocketResponse) {

    }

    private fun onInitDataReceived(response: SocketResponse) {

    }

    fun showBottomSheetDialog() {
        val filterBottomSheetFragment = FilterBottomSheetFragment()
        filterBottomSheetFragment.show(supportFragmentManager, "")
        iv_filter.isClickable = true
    }

}