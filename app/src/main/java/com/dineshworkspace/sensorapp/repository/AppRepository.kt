package com.dineshworkspace.sensorapp.repository

import com.dineshworkspace.sensorapp.network.ApiHelper
import javax.inject.Inject

class AppRepository @Inject constructor(private val apiHelper: ApiHelper) {

    fun fetchAllSensors() = apiHelper.getSensors()

    fun fetchSensorConfig() = apiHelper.getSensorConfig()

}