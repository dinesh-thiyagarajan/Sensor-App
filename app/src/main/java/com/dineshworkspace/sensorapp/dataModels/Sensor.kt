package com.dineshworkspace.sensorapp.dataModels

data class Sensor(
    val sensorName: String,
    var subscriptionStatus: SubscriptionStatus
)

enum class SubscriptionStatus {
    NOT_YET,
    TO_BE_SUBSCRIBED,
    SUBSCRIBED,
    UN_SUBSCRIBED
}