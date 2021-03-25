package com.dineshworkspace.sensorapp.dataModels


data class BaseResponse<out T>(
    val status: Status,
    val data: T?,
    val message: String?
) {
    companion object {

        fun <T> success(data: T?): BaseResponse<T> {
            return BaseResponse(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): BaseResponse<T> {
            return BaseResponse(Status.ERROR, data, msg)
        }

        fun <T> loading(data: T?): BaseResponse<T> {
            return BaseResponse(Status.LOADING, data, null)
        }

    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}