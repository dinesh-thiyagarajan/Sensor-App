package com.dineshworkspace.sensorapp.helpers

import com.dineshworkspace.sensorapp.dataModels.SocketResponse
import com.github.mikephil.charting.data.Entry

object SensorDataParser {

    fun getEntriesForInitResponse(response: SocketResponse): ArrayList<Entry> {
        val entries = ArrayList<Entry>()
        response.let {
            response.recent.let {
                for (data in response.recent) {
                    entries.add(Entry(data.value.toFloat(), data.key.toFloat()))
                }
            }

            response.minute.let {
                for (data in response.minute) {
                    entries.add(Entry(data.value.toFloat(), data.key.toFloat()))
                }
            }
        }
        return entries
    }

}