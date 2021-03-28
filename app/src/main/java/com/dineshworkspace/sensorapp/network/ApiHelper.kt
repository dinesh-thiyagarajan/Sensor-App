package com.dineshworkspace.sensorapp.network

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response

interface ApiHelper {
    fun getSensors(): Call<ArrayList<String>>
    fun getSensorConfig(): Call<JsonObject>
}