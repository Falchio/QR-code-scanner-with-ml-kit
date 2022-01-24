package ru.piteravto.barcodescanner.scanner

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

private const val TAG = "BarcodeAnalyzer"

/** Класс для обработки захваченного ImageAnalysis изображения
 * все данные постыпают в analyze где их и можно обрабатывать */
class BarcodeAnalyzer : ImageAnalysis.Analyzer {


    override fun analyze(image: ImageProxy) {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC,
                Barcode.FORMAT_DATA_MATRIX
            )
            .build()
        val scanner = BarcodeScanning.getClient(options)

        val rotationDegrees = image.imageInfo.rotationDegrees

        val barcodeImage = InputImage.fromMediaImage(image.image, rotationDegrees)

        val result = scanner.process(barcodeImage)
            .addOnSuccessListener { barcodes ->
                barcodes.forEach {
                    if (it.valueType == Barcode.TYPE_TEXT) {
                        Log.e(TAG, "analyze: ${it.displayValue}")
                    }
                }
            }
        // after done, release the ImageProxy object
        image.close()
    }
}