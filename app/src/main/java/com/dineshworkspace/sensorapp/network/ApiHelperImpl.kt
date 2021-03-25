package com.dineshworkspace.sensorapp.network


import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(
    private val apiService: ApiService
) : ApiHelper {

    override suspend fun getSensors(): Response<ArrayList<String>> = apiService.getEmployees()

}