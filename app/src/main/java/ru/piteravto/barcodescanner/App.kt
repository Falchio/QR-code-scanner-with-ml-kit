package ru.piteravto.barcodescanner

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
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

fun Context.showToast(message: String, isShort: Boolean = true) {
    val handler = Handler(Looper.getMainLooper())
    handler.post {
        val duration = if (isShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
        Toast.makeText(this, message, duration).show()
    }
}