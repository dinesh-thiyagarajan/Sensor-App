package com.dineshworkspace.sensorapp.helpers

import com.dineshworkspace.sensorapp.AppConstants
import com.dineshworkspace.sensorapp.dataModels.SocketResponse

object SensorDataHelper {

    fun getEntriesForInitResponse(response: SocketResponse): HashMap<String, LinkedHashMap<Double, Double>> {
        val initBaseHash = HashMap<String, LinkedHashMap<Double, Double>>()
        val recentEntries = LinkedHashMap<Double, Double>()
        val minuteEntries = LinkedHashMap<Double, Double>()
        response.let {
            response.recent.let {
                for (data in response.recent) {
                    recentEntries.put(data.key, data.value)
                }
            }
            response.minute.let {
                for (data in response.minute) {
                    minuteEntries.put(data.key, data.value)
                }
            }
        }
        initBaseHash.put(AppConstants.DATA_VARIANT_RECENT, recentEntries)
        initBaseHash.put(AppConstants.DATA_VARIANT_MINUTE, minuteEntries)
        return initBaseHash
    }

    fun updateEntriesInHash(
        response: SocketResponse,
        rawHashEntries: LinkedHashMap<String, HashMap<String, LinkedHashMap<Double, Double>>>
    ): LinkedHashMap<String, HashMap<String, LinkedHashMap<Double, Double>>> {
        response.let {
            rawHashEntries.get(response.sensor)
                ?.get(response.scale)
                ?.put(response.key, response.value)
        }
        return rawHashEntries
    }

    fun deleteEntriesInHash(
        response: SocketResponse,
        rawHashEntries: LinkedHashMap<String, HashMap<String, LinkedHashMap<Double, Double>>>
    ): LinkedHashMap<String, HashMap<String, LinkedHashMap<Double, Double>>> {
        response.let {
            rawHashEntries.get(response.sensor)
                ?.get(response.scale)
                ?.remove(response.key)
        }
        return rawHashEntries
    }

}