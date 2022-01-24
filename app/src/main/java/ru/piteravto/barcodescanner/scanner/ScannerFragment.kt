package ru.piteravto.barcodescanner.scanner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ru.piteravto.barcodescanner.databinding.FragmentScannerBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val TAG = "ScannerFragment"

class ScannerFragment : Fragment() {
    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!
    private lateinit var boxView: Box
    private val analyzer = BarcodeAnalyzer()

    private lateinit var executor: ExecutorService

    private var torchIsOn = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        boxView = Box(requireContext())
        executor = Executors.newSingleThreadExecutor()
        startCamera()
        analyzer.value.observe(viewLifecycleOwner, {
            binding.root.removeView(boxView) // здесь можно считанный QR code, а так же убрать зеленый квадрат

        })
    }

    override fun onResume() {
        super.onResume()
        binding.root.addView(
            boxView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
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
                val camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, imageAnalysis, preview
                )

                with(camera) {
                    if (cameraInfo.hasFlashUnit()) {
                        binding.torch.setOnClickListener {
                            cameraControl.enableTorch(!torchIsOn)
                        }
                        cameraInfo.torchState.observe(this@ScannerFragment, { torchState ->
                            manageTorchState(torchState)
                        })
                    }
                }

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
                exc.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun manageTorchState(torchState: Int) {
        when (torchState) {
            TorchState.ON -> {
                torchIsOn = true
                binding.torch.text = "Выкл. вспышку"
            }
            TorchState.OFF -> {
                torchIsOn = false
                binding.torch.text = "Вкл. вспышку"
            }
        }
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
        imageAnalysis.setAnalyzer(executor, analyzer)
        return imageAnalysis
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }
}