package com.dineshworkspace.sensorapp.viewModels

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dineshworkspace.sensorapp.dataModels.*
import com.dineshworkspace.sensorapp.helpers.AppConstants
import com.dineshworkspace.sensorapp.helpers.SensorDataHelper
import com.dineshworkspace.sensorapp.repository.AppRepository
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SensorViewModel @Inject constructor(private val appRepository: AppRepository) : ViewModel(),
    LifecycleObserver {

    var sensorsList: MutableLiveData<BaseResponse<ArrayList<Sensor>>> = MutableLiveData()
    var selectedSensors: MutableLiveData<ArrayList<Sensor>> = MutableLiveData()
    var rawHashEntries: LinkedHashMap<String, HashMap<String, LinkedHashMap<Double, Double>>> =
        LinkedHashMap()
    var dataSetList: MutableLiveData<ArrayList<ILineDataSet>> = MutableLiveData()
    var sensorColorHash: HashMap<String, Int> = HashMap()
    private val sensorList = ArrayList<Sensor>()
    var selectedConfig: SelectedConfig = SelectedConfig.ALL

    init {
        getSensors()
    }

    private fun getSensors() = viewModelScope.launch {
        sensorsList.postValue(BaseResponse.loading(null))
        appRepository.fetchAllSensors().enqueue(object : Callback<ArrayList<String>?> {
            override fun onResponse(
                call: Call<ArrayList<String>?>,
                response: Response<ArrayList<String>?>
            ) {
                getSensorsConfig()
                sensorsList.postValue(BaseResponse.success(parseSensorListData(response)))
            }

            override fun onFailure(call: Call<ArrayList<String>?>, t: Throwable) {
                sensorsList.postValue(BaseResponse.error(t.localizedMessage, null))
            }

        })
    }

    private fun getSensorsConfig() = viewModelScope.launch {
        appRepository.fetchSensorConfig().enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                parseAndUpdateSensorConfig(response)
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {

            }
        })
    }

    private fun parseAndUpdateSensorConfig(jsonResponse: Response<JsonObject?>) {
        jsonResponse.let {
            it.body().let { body ->
                for (sensor in sensorList) {
                    val config = body?.get(sensor.sensorName) as JsonObject
                    sensor.min = config.get("min").asInt
                    sensor.max = config.get("max").asInt
                    sensor.hasConfigFetched = true
                }
            }
        }
        sensorsList.postValue(BaseResponse.success(sensorList))
    }

    private fun parseSensorListData(sensors: Response<ArrayList<String>?>): ArrayList<Sensor> {
        sensors.let {
            it.body().let { data ->
                for (sensor in data!!) {
                    sensorList.add(
                        Sensor(
                            sensorName = sensor,
                            subscriptionStatus = SubscriptionStatus.NOT_YET,
                            0,
                            0,
                            false
                        )
                    )
                }
            }
        }
        return sensorList
    }

    fun onDeleteDataReceived(response: SocketResponse) {
        viewModelScope.launch {
            rawHashEntries = SensorDataHelper.deleteEntriesInHash(
                response = response,
                rawHashEntries = rawHashEntries
            )
            convertHashToLineDataSet()
        }
    }

    fun onUpdateDataReceived(response: SocketResponse) {
        viewModelScope.launch {
            rawHashEntries = SensorDataHelper.updateEntriesInHash(
                response = response,
                rawHashEntries = rawHashEntries
            )
            convertHashToLineDataSet()
        }
    }

    fun onInitDataReceived(response: SocketResponse, lastSubscribedSensor: String) {
        viewModelScope.launch {
            rawHashEntries.put(
                lastSubscribedSensor,
                SensorDataHelper.getEntriesForInitResponse(response)
            )
            convertHashToLineDataSet()
        }
    }

    private fun convertHashToLineDataSet() {
        val lineDataList: ArrayList<ILineDataSet> = ArrayList()
        rawHashEntries.let {
            it.forEach { (sensorKey, value) ->
                value.let { sensorData ->
                    //For each sensor loop ex: temperature0,temperature1, etc
                    val colorCode: Int
                    if (sensorColorHash.containsKey(sensorKey)) {
                        colorCode = sensorColorHash.get(sensorKey)!!
                    } else {
                        colorCode = SensorDataHelper.generateRandomColor()
                        sensorColorHash.put(sensorKey, colorCode)
                    }

                    val entrySets = ArrayList<Entry>()
                    val filteredData: LinkedHashMap<Double, Double> = LinkedHashMap()
                    when (selectedConfig) {
                        SelectedConfig.ALL -> {
                            filteredData.putAll(sensorData.get(AppConstants.DATA_VARIANT_RECENT) as LinkedHashMap<Double, Double>)
                            filteredData.putAll(sensorData.get(AppConstants.DATA_VARIANT_MINUTE) as LinkedHashMap<Double, Double>)
                        }

                        SelectedConfig.RECENT -> {
                            filteredData.putAll(sensorData.get(AppConstants.DATA_VARIANT_RECENT) as LinkedHashMap<Double, Double>)
                        }

                        SelectedConfig.MINUTE -> {
                            filteredData.putAll(sensorData.get(AppConstants.DATA_VARIANT_MINUTE) as LinkedHashMap<Double, Double>)
                        }
                    }

                    filteredData.forEach { (doubleKey, doubleValue) ->
                        entrySets.add(Entry(doubleKey.toFloat(), doubleValue.toFloat()))
                    }
                    val lineData = generateLineDataSet(
                        key = sensorKey, value = entrySets,
                        color = colorCode
                    )
                    lineDataList.add(lineData)
                }
            }
        }
        dataSetList.postValue(lineDataList)
    }

    private fun generateLineDataSet(
        key: String,
        value: java.util.ArrayList<Entry>,
        color: Int
    ): LineDataSet {
        val lineDataSet = LineDataSet(value, key)
        lineDataSet.color = color
        lineDataSet.valueTextColor = color
        return lineDataSet
    }

    fun onSocketUnsubscribed(sensorName: String) {
        viewModelScope.launch {
            rawHashEntries.let {
                it.remove(sensorName)
            }
            convertHashToLineDataSet()
        }
    }

    fun onConfigUpdated(config: SelectedConfig) {
        viewModelScope.launch {
            selectedConfig = config
            convertHashToLineDataSet()
        }
    }

}