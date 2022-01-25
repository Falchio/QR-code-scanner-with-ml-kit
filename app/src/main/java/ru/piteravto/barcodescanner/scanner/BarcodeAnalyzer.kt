package ru.piteravto.barcodescanner.scanner

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

private const val TAG = "BarcodeAnalyzer"

/** Класс для обработки захваченного ImageAnalysis изображения
 * все данные постыпают в analyze где их и можно обрабатывать */
class BarcodeAnalyzer : ImageAnalysis.Analyzer {
    private val mutableValue: MutableLiveData<String> = MutableLiveData()
    val value: LiveData<String> get() = mutableValue

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_AZTEC,
            Barcode.FORMAT_DATA_MATRIX
        )
        .build()
    private val scanner = BarcodeScanning.getClient(options)

    override fun analyze(image: ImageProxy) {
        val rotationDegrees = image.imageInfo.rotationDegrees
        val barcodeImage = InputImage.fromMediaImage(image.image, rotationDegrees)

        scanner.process(barcodeImage)
            .addOnSuccessListener { barcodes ->
                barcodes.forEach { barcode ->
                    if (barcode.valueType == Barcode.TYPE_TEXT) {
                        barcode.rawValue?.let { value -> mutableValue.postValue(value) } //тут получено значение текстового QR кода
                    }
                }
            }
            .addOnCompleteListener {
                image.close()
            }
    }

    /* Если вдруг окажется, что сканер работает через чур быстро,
    то таким нехитрым способом можно срезать его скорость вдвое.


    private var isLocked = true

    override fun analyze(image: ImageProxy) {
        isLocked = !isLocked
        if (isLocked) {
            image.close()
            return
        }
     */
}