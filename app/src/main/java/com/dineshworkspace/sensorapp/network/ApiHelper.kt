package com.dineshworkspace.sensorapp.network

import retrofit2.Response

interface ApiHelper {
    suspend fun getSensors(): Response<ArrayList<String>>
}