package kr.rabbito.homefit.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import kr.rabbito.homefit.R
import kr.rabbito.homefit.client.HomeFitClient
import kr.rabbito.homefit.databinding.ActivityDcameraBinding
import kr.rabbito.homefit.utils.calc.Converter
import kr.rabbito.homefit.utils.calc.PermissionChecker
import java.util.concurrent.ExecutorService

class DCameraActivity : AppCompatActivity() {
    private var mBinding: ActivityDcameraBinding? = null
    private val binding get() = mBinding!!

    // 통신
    private var client: HomeFitClient? = null

    // 카메라
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    private lateinit var cameraAnimationListener: Animation.AnimationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDcameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 권한 확인
        permissionCheck()

        // 서버 연결
        client = HomeFitClient()

        // 연결, 사용자명 전송
        Thread {
            try {
                client!!.sendRequest()
                client!!.sendUserName("User")
                client!!.getData()
            } catch (e: NullPointerException) {
                Log.d("connection", "socket not initialized")
            }
        }.start()

        // 촬영 및 사진 전송
        binding.dcameraBtnShot.setOnClickListener {
            takeAndSendPhoto()
        }

        // 임시
        binding.dcameraBtnCancel.setOnClickListener {
//            Thread {
//                val image = BitmapFactory.decodeResource(this.resources, R.drawable.connection_test)
//                client!!.sendImage(image)
//            }.start()
//            startActivity(Intent(this, DReportActivity::class.java))
        }
    }

    private fun permissionCheck() {
        val permissionList = listOf(Manifest.permission.CAMERA)

        if (!PermissionChecker.checkPermission(this, permissionList)) {
            PermissionChecker.requestPermission(this, permissionList)
        } else {
            openCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        }
    }

    // 카메라 실행
    private fun openCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.dcameraPvCameraView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (_: Exception) {
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takeAndSendPhoto() {
        imageCapture = imageCapture ?: return

        imageCapture?.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)

                    val bitmap = Converter.imageProxyToBitmap(image)

                    Thread {
                        client!!.sendImage(bitmap)
                    }.start()
                }
            })
    }

    override fun onDestroy() {
        client?.closeSocket()
        super.onDestroy()
    }
}