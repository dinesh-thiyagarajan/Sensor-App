package com.dineshworkspace.sensorapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import com.dineshworkspace.sensorapp.R
import com.dineshworkspace.sensorapp.dataModels.BaseResponse
import com.dineshworkspace.sensorapp.dataModels.Sensor
import com.dineshworkspace.sensorapp.dataModels.SocketResponse
import com.dineshworkspace.sensorapp.dataModels.Status
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


        baseSocket.emit("subscribe", "temperature0")
        baseSocket.on("data") {
            it.let {
                if (it.isEmpty()) {
                    showSnackBar(getString(R.string.data_parsing_err_msg))
                    return@on
                }
                val json = it[0] as JSONObject
                val socketResponse = gson.fromJson(json.toString(), SocketResponse::class.java)
                updateUiForSocketResponse(socketResponse)
            }
        }

        iv_filter.setOnClickListener {
            showBottomSheetDialog()
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



    private fun showErrorScreen(message: String?) {
        showSnackBar(message)
    }


    private fun showLoadingScreen() {

    }

    fun showBottomSheetDialog() {
        val filterBottomSheetFragment = FilterBottomSheetFragment()
        filterBottomSheetFragment.show(supportFragmentManager, "")
    }

}