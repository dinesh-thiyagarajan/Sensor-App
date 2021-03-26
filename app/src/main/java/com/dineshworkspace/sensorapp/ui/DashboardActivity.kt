package com.dineshworkspace.sensorapp.ui

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import com.dineshworkspace.sensorapp.AppConstants
import com.dineshworkspace.sensorapp.R
import com.dineshworkspace.sensorapp.dataModels.Sensor
import com.dineshworkspace.sensorapp.dataModels.SocketResponse
import com.dineshworkspace.sensorapp.dataModels.SubscriptionStatus
import com.dineshworkspace.sensorapp.viewModels.SensorViewModel
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_dashboard.*
import org.json.JSONObject
import javax.inject.Inject
import kotlin.random.Random


@AndroidEntryPoint
class DashboardActivity : BaseActivity(layoutId = R.layout.activity_dashboard) {

    private val sensorViewModel: SensorViewModel by viewModels()
    private val linkedChartHashMap: LinkedHashMap<String, ArrayList<Entry>> = LinkedHashMap()

    @Inject
    lateinit var gson: Gson
    private lateinit var lastSubscribedSensor: String

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

        initChartCustomizations()

        sensorViewModel.chartDataHash.observe(this, {
            linkedChartHashMap.put(lastSubscribedSensor, it.get(lastSubscribedSensor)!!)

            val dataSets = java.util.ArrayList<ILineDataSet>()

            linkedChartHashMap.forEach { (key, value) ->
                val lineDataSet = generateLineDataSet(key, value)
                dataSets.add(lineDataSet)
            }
            val lineData = LineData(dataSets)
            chart_sensor_data.data = lineData
            chart_sensor_data.invalidate()
        })

        iv_filter.setOnClickListener {
            iv_filter.isClickable = false
            showBottomSheetDialog()
        }
    }

    private fun generateLineDataSet(key: String, value: java.util.ArrayList<Entry>): LineDataSet {
        val lineDataSet = LineDataSet(value, key)
        val rnd = Random.Default
        val color: Int = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        lineDataSet.color = color
        lineDataSet.valueTextColor = color
        return lineDataSet
    }

    private fun initChartCustomizations() {
        chart_sensor_data.setDrawGridBackground(false)
        chart_sensor_data.getDescription().setEnabled(false)
        chart_sensor_data.setDrawBorders(false)

        chart_sensor_data.getAxisLeft().setEnabled(false)
        chart_sensor_data.getAxisRight().setDrawAxisLine(false)
        chart_sensor_data.getAxisRight().setDrawGridLines(false)
        chart_sensor_data.getXAxis().setDrawAxisLine(false)
        chart_sensor_data.getXAxis().setDrawGridLines(false)
        // enable touch gestures
        chart_sensor_data.setTouchEnabled(true)
        // enable scaling and dragging
        chart_sensor_data.setDragEnabled(true)
        chart_sensor_data.setScaleEnabled(true)

        val l: Legend = chart_sensor_data.getLegend()
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
    }

    private fun unsubscribeSensor(sensor: Sensor) {
        baseSocket.emit("unsubscribe", sensor.sensorName)
        sensor.subscriptionStatus = SubscriptionStatus.UN_SUBSCRIBED
    }

    private fun subscribeSensor(sensor: Sensor) {
        baseSocket.emit("subscribe", sensor.sensorName)
        lastSubscribedSensor = sensor.sensorName
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
                AppConstants.RES_TYPE_INIT -> sensorViewModel.onInitDataReceived(
                    response,
                    lastSubscribedSensor
                )
                AppConstants.RES_TYPE_UPDATE -> sensorViewModel.onUpdateDataReceived(response)
                AppConstants.RES_TYPE_DELETE -> sensorViewModel.onDeleteDataReceived(response)
                else -> showSnackBarWithAutoClosure(getString(R.string.something_went_wrong))
            }
        }
    }

    fun showBottomSheetDialog() {
        val filterBottomSheetFragment = FilterBottomSheetFragment()
        filterBottomSheetFragment.show(supportFragmentManager, "")
        iv_filter.isClickable = true
    }

}