package com.dineshworkspace.sensorapp.network

import org.json.JSONObject
import retrofit2.Response

interface ApiHelper {
    suspend fun getSensors(): Response<ArrayList<String>>
    suspend fun getSensorConfig(): Response<JSONObject>
}