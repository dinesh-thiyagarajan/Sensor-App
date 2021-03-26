package com.dineshworkspace.sensorapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import com.dineshworkspace.sensorapp.R
import com.dineshworkspace.sensorapp.dataModels.Sensor
import com.dineshworkspace.sensorapp.dataModels.SocketResponse
import com.dineshworkspace.sensorapp.dataModels.SubscriptionStatus
import com.dineshworkspace.sensorapp.viewModels.SensorViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
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
            iv_filter.isEnabled = false
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
        var entries = ArrayList<Entry>()
        socketResponse.let {
            it!!.recent.let { recent ->
                if (recent == null) return@let
                for (item in recent) {
                    entries.add(Entry(item.key.toFloat(), item.value.toFloat()))
                }

            }
        }
        if (entries.isEmpty()) return
        val dataSet = LineDataSet(entries, "Label")
        dataSet.setColor(resources.getColor(R.color.black))
        val lineData = LineData(dataSet)
        chart_sensor_data.setData(lineData)
        chart_sensor_data.invalidate()
    }

    fun showBottomSheetDialog() {
        val filterBottomSheetFragment = FilterBottomSheetFragment()
        filterBottomSheetFragment.show(supportFragmentManager, "")
        iv_filter.isEnabled = true
    }

}