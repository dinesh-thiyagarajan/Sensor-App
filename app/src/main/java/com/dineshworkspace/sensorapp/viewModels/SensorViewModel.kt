package com.dineshworkspace.sensorapp.viewModels

import android.graphics.Color
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dineshworkspace.sensorapp.AppConstants
import com.dineshworkspace.sensorapp.dataModels.BaseResponse
import com.dineshworkspace.sensorapp.dataModels.Sensor
import com.dineshworkspace.sensorapp.dataModels.SocketResponse
import com.dineshworkspace.sensorapp.dataModels.SubscriptionStatus
import com.dineshworkspace.sensorapp.helpers.SensorDataHelper
import com.dineshworkspace.sensorapp.repository.AppRepository
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SensorViewModel @Inject constructor(private val appRepository: AppRepository) : ViewModel(),
    LifecycleObserver {

    var sensorsList: MutableLiveData<BaseResponse<ArrayList<Sensor>>> = MutableLiveData()
    var selectedSensors: MutableLiveData<ArrayList<Sensor>> = MutableLiveData()
    var rawHashEntries: LinkedHashMap<String, HashMap<String, LinkedHashMap<Double, Double>>> =
        LinkedHashMap()
    var dataSetList: MutableLiveData<ArrayList<ILineDataSet>> = MutableLiveData()

    init {
        getSensors()
    }

    private fun getSensors() = viewModelScope.launch {
        sensorsList.postValue(BaseResponse.loading(null))
        appRepository.fetchAllSensors().let {
            if (it.isSuccessful) {
                sensorsList.postValue(BaseResponse.success(parseSensorListData(it)))
            } else {
                sensorsList.postValue(BaseResponse.error(it.errorBody().toString(), null))
            }
        }
    }

    private fun parseSensorListData(sensors: Response<ArrayList<String>>): ArrayList<Sensor> {
        val sensorList = ArrayList<Sensor>()
        sensors.let {
            it.body().let { data ->
                for (sensor in data!!) {
                    sensorList.add(
                        Sensor(
                            sensorName = sensor,
                            subscriptionStatus = SubscriptionStatus.NOT_YET
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
                    val recentData =
                        sensorData.get(AppConstants.DATA_VARIANT_RECENT) as LinkedHashMap<Double, Double>
                    recentData.putAll(sensorData.get(AppConstants.DATA_VARIANT_MINUTE) as LinkedHashMap<Double, Double>)
                    val entrySets = ArrayList<Entry>()
                    recentData.forEach { (doubleKey, doubleValue) ->
                        entrySets.add(Entry(doubleKey.toFloat(), doubleKey.toFloat()))
                    }
                    val lineData = generateLineDataSet(key = sensorKey, value = entrySets)
                    lineDataList.add(lineData)
                }
            }
        }
        dataSetList.postValue(lineDataList)
    }

    private fun generateLineDataSet(key: String, value: java.util.ArrayList<Entry>): LineDataSet {
        val lineDataSet = LineDataSet(value, key)
        val rnd = Random.Default
        val color: Int = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        lineDataSet.color = color
        lineDataSet.valueTextColor = color
        return lineDataSet
    }

}