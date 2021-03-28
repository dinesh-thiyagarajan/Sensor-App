package com.dineshworkspace.sensorapp.network

import com.dineshworkspace.sensorapp.helpers.AppConstants
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET(AppConstants.END_POINT_SENSOR_NAMES)
    fun getSensors(): Call<ArrayList<String>>

    @GET(AppConstants.END_POINT_SENSOR_CONFIG)
    fun getSensorConfig(): Call<JsonObject>
}