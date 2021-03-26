package com.dineshworkspace.sensorapp.repository

import com.dineshworkspace.sensorapp.dataModels.Sensor
import com.dineshworkspace.sensorapp.network.ApiHelper
import retrofit2.Response
import javax.inject.Inject

class AppRepository @Inject constructor(private val apiHelper: ApiHelper) {

    suspend fun fetchAllSensors() = apiHelper.getSensors()

}