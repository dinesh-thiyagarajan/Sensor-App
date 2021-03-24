package com.dineshworkspace.sensorapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class SensorApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        application = this
    }

    companion object {
        lateinit var application: SensorApp
    }
}