package com.dineshworkspace.sensorapp.dataModels

import com.google.gson.annotations.SerializedName

data class SocketResponse(
    val type: String,
    val recent: ArrayList<Recent>,
    val minute: ArrayList<Minute>,
    val key: Double,
    @SerializedName("val") val value: Double,
    val sensor: String,
    val scale: String,
)

data class Recent(val key: Double, @SerializedName("val") val value: Double)
data class Minute(val key: Double, @SerializedName("val") val value: Double)