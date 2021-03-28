package com.dineshworkspace.sensorapp.helpers

object AppConstants {

    const val BASE_URL = "http://interview.optumsoft.com/"
    const val END_POINT_SENSOR_NAMES = "sensornames"
    const val END_POINT_SENSOR_CONFIG = "config"
    const val ON_SOCKET_CONNECT = "connect"
    const val ON_CONNECT_ERROR = "connect_error"

    //Socket Response Types
    const val RES_TYPE_INIT = "init"
    const val RES_TYPE_UPDATE = "update"
    const val RES_TYPE_DELETE = "delete"

    //Socket Data Variant
    const val DATA_VARIANT_RECENT = "recent"
    const val DATA_VARIANT_MINUTE = "minute"
}