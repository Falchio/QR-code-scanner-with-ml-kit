package ru.piteravto.barcodescanner.scanner;

import android.content.Context;
import android.os.Handler;

import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.CameraXConfig;
import androidx.camera.lifecycle.ProcessCameraProvider;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;

class AppCameraProvider {

    private static boolean configured = false;

    static ListenableFuture<ProcessCameraProvider> getInstance(Context context, Executor executor, Handler schedulerHandler) {
        synchronized (AppCameraProvider.class) {
            if (!configured) {
                configured = true;
                ProcessCameraProvider.configureInstance(
                        CameraXConfig.Builder.fromConfig(Camera2Config.defaultConfig())
                                .setCameraExecutor(executor)
                                .setSchedulerHandler(schedulerHandler)
                                .build());
            }
        }
        return ProcessCameraProvider.getInstance(context);
    }
}