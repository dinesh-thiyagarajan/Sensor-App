package com.dineshworkspace.sensorapp.network


import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(
    private val apiService: ApiService
) : ApiHelper {

    override fun getSensors(): Call<ArrayList<String>> = apiService.getSensors()
    override fun getSensorConfig(): Call<JsonObject> = apiService.getSensorConfig()

}