package com.dineshworkspace.sensorapp.viewModels

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dineshworkspace.sensorapp.dataModels.BaseResponse
import com.dineshworkspace.sensorapp.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SensorViewModel @Inject constructor(private val appRepository: AppRepository) : ViewModel(),
    LifecycleObserver {

    var sensorsList: MutableLiveData<BaseResponse<ArrayList<String>>> = MutableLiveData()

    init {
        getSensors()
    }

    private fun getSensors() = viewModelScope.launch {
        sensorsList.postValue(BaseResponse.loading(null))
//        appRepository.fetchAllSensors().let {
//            if (it.isSuccessful) {
//                sensorsList.postValue(BaseResponse.success(it.body()))
//            } else {
//                sensorsList.postValue(BaseResponse.error(it.errorBody().toString(), null))
//            }
//        }
    }


}