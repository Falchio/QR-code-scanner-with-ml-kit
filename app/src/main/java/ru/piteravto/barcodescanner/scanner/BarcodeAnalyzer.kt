package ru.piteravto.barcodescanner.scanner

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

private const val TAG = "BarcodeAnalyzer"

/** Класс для обработки захваченного ImageAnalysis изображения
 * все данные постыпают в analyze где их и можно обрабатывать */
class BarcodeAnalyzer : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient()

    override fun analyze(image: ImageProxy) {
        val rotationDegrees = image.imageInfo.rotationDegrees
        Log.e(TAG, "analyze: $rotationDegrees ")

        val barcodeImage = InputImage.fromMediaImage(image.image, image.imageInfo.rotationDegrees)

        val result = scanner.process(barcodeImage)
            .addOnSuccessListener { Log.e(TAG, "success") }
            .addOnFailureListener { Log.e(TAG, "failure") }
        // after done, release the ImageProxy object
        image.close()
    }
}