package ru.piteravto.barcodescanner.scanner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import ru.piteravto.barcodescanner.databinding.FragmentScannerBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val TAG = "ScannerFragment"

class ScannerFragment : Fragment() {
    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!

    private lateinit var executor: ExecutorService

    private val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        binding.scan.setOnClickListener { startScan() }
        return binding.root
    }

    private fun startScan() {
        Log.e(TAG, "startScan: ")
    }

    override fun onStart() {
        super.onStart()
        executor = Executors.newSingleThreadExecutor()
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = getPreview()

            try {
                // Select back camera as a default
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview
                )


                val imageAnalysis = barcodeImageAnalysis()
                cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview)

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
                exc.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    /** Preview - это отображение того, что камера снимает в данный момент
     * в layout fragment'а */
    private fun getPreview(): Preview {
        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }
        return preview
    }

    /** ImageAnalysis - при добавлении через ProcessCameraProvider
     * к жизненному циклу приложения начинает с очень большой периодичностью
     * захватывать изображение с камеры и передавать его в класс ImageAnalysis.Analyzer*/
    private fun barcodeImageAnalysis(): ImageAnalysis {
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(executor, BarcodeAnalyzer())
        return imageAnalysis
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }
}