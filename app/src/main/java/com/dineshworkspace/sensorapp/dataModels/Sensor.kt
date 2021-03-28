package com.dineshworkspace.sensorapp.dataModels

data class Sensor(
    val sensorName: String,
    var subscriptionStatus: SubscriptionStatus,
    var min: Int,
    var max: Int,
    var hasConfigFetched: Boolean
)

enum class SubscriptionStatus {
    NOT_YET,
    TO_BE_SUBSCRIBED,
    SUBSCRIBED,
    UN_SUBSCRIBED
}

enum class SelectedConfig {
    ALL,
    MINUTE,
    RECENT
}