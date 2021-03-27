package com.dineshworkspace.sensorapp.network

import com.google.gson.JsonObject
import retrofit2.Response

interface ApiHelper {
    suspend fun getSensors(): Response<ArrayList<String>>
    suspend fun getSensorConfig(): Response<JsonObject>
}