package com.dineshworkspace.sensorapp.network


import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(
    private val apiService: ApiService
) : ApiHelper {

    override suspend fun getSensors(): Response<ArrayList<String>> = apiService.getSensors()
    override suspend fun getSensorConfig(): Response<JsonObject> = apiService.getSensorConfig()

}