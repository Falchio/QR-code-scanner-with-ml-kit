package ru.piteravto.barcodescanner

import android.app.Application
import android.util.Log
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig

class App : Application(), CameraXConfig.Provider {

    companion object {
        private lateinit var INSTANCE: App
        val context get() = INSTANCE.applicationContext
    }

    override fun getCameraXConfig(): CameraXConfig {
        return CameraXConfig.Builder.fromConfig(Camera2Config.defaultConfig())
            .setMinimumLoggingLevel(Log.ERROR).build()
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}