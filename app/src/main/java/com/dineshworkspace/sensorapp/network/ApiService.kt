package com.dineshworkspace.sensorapp.network

import com.dineshworkspace.sensorapp.AppConstants
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET(AppConstants.END_POINT_SENSOR_NAMES)
    suspend fun getEmployees(): Response<ArrayList<String>>


}