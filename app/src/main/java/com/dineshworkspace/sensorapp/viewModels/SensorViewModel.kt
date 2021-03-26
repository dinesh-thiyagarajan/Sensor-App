package com.dineshworkspace.sensorapp.viewModels

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dineshworkspace.sensorapp.dataModels.BaseResponse
import com.dineshworkspace.sensorapp.dataModels.Sensor
import com.dineshworkspace.sensorapp.dataModels.SubscriptionStatus
import com.dineshworkspace.sensorapp.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SensorViewModel @Inject constructor(private val appRepository: AppRepository) : ViewModel(),
    LifecycleObserver {

    var sensorsList: MutableLiveData<BaseResponse<ArrayList<Sensor>>> = MutableLiveData()
    var selectedSensors: MutableLiveData<ArrayList<Sensor>> = MutableLiveData()

    init {
        getSensors()
    }

    private fun getSensors() = viewModelScope.launch {
        sensorsList.postValue(BaseResponse.loading(null))
        appRepository.fetchAllSensors().let {
            if (it.isSuccessful) {
                sensorsList.postValue(BaseResponse.success(parseSensorData(it)))
            } else {
                sensorsList.postValue(BaseResponse.error(it.errorBody().toString(), null))
            }
        }
    }

    private fun parseSensorData(sensors: Response<ArrayList<String>>): ArrayList<Sensor> {
        val sensorList = ArrayList<Sensor>()
        sensors.let {
            it.body().let { data ->
                for (sensor in data!!) {
                    sensorList.add(
                        Sensor(
                            sensor,
                            isSelected = false,
                            subscriptionStatus = SubscriptionStatus.NOT_YET
                        )
                    )
                }
            }
        }
        return sensorList
    }

    protected fun listenToSensorData(sensorName: String) {

    }


}