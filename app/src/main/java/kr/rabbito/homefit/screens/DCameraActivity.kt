package kr.rabbito.homefit.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import kr.rabbito.homefit.client.HomeFitClient
import kr.rabbito.homefit.databinding.ActivityDcameraBinding
import kr.rabbito.homefit.utils.calc.Converter
import kr.rabbito.homefit.utils.calc.PermissionChecker
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.ExecutorService
import kotlin.math.atan

class DCameraActivity : AppCompatActivity() {
    private var mBinding: ActivityDcameraBinding? = null
    private val binding get() = mBinding!!

    // 통신
    private var client: HomeFitClient? = null

    // 카메라
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    private lateinit var cameraAnimationListener: Animation.AnimationListener

    private var dX = 0F
    private var dY = 0F

    @SuppressLint("ClickableViewAccessibility")
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
            client!!.sendRequest()
            client!!.sendUserName("User")
        }.start()

        // 안내화면
        binding.dcameraClInfo.visibility = View.VISIBLE
        removeInfo()

        binding.dcameraBtnInfoBack.setOnClickListener {
            initView()
        }

        binding.dcameraIvSpoonFrame.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    dX = motionEvent.rawX - view.x
                    dY = motionEvent.rawY - view.y
                }
                MotionEvent.ACTION_MOVE -> {
                    view.x = motionEvent.rawX - dX
                    view.y = motionEvent.rawY - dY
                }
            }
            true
        }

        // 촬영 및 사진 전송
        binding.dcameraBtnShot.setOnClickListener {
            takeAndSendPhoto()
        }

        binding.dcameraBtnCancel.setOnClickListener {
            finish()
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
                    val cameraInfo = getCameraInfo()
                    Log.d("cameraInfo", cameraInfo)
                    Thread {
                        runOnUiThread {
                            binding.dcameraClLoading.visibility = View.VISIBLE
                            binding.dcameraIvSpoonFrame.visibility = View.INVISIBLE
                            binding.dcameraBtnShot.isEnabled = false
                            binding.dcameraBtnShot.alpha = 0.5f
                        }

                        val startTime = System.currentTimeMillis()
                        client!!.sendCameraInfo(cameraInfo)
                        client!!.sendImage(this@DCameraActivity, bitmap)
                        val data: String? = client!!.getData(this@DCameraActivity)

                        val endTime = System.currentTimeMillis()
                        Log.d("time gap", (endTime - startTime).toString())

                        if (client == null || data == null) {
                            runOnUiThread {
                                Toast.makeText(this@DCameraActivity, "서버 연결에 실패했습니다.\n연결 상태를 확인해주세요.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            finish()
                        }

                        runOnUiThread {
                            binding.dcameraClLoading.visibility = View.INVISIBLE
                        }

                        if (data != null) {
                            val intent = Intent(this@DCameraActivity, MainActivity::class.java)
                            intent.putExtra("VIEW_PAGER_INDEX", 1)
                            intent.putExtra("DATA", data)
                            intent.putExtra("DATE", LocalDate.now().toString())
                            intent.putExtra("TIME", LocalDateTime.now().toString())
                            startActivity(intent)
                            finish()
                        }
                    }.start()
                }
            })
    }

    private fun removeInfo() {
        val handler = Handler()
        handler.postDelayed({
            initView()
        }, 6000)
    }

    private fun initView() {
        binding.dcameraBtnShot.alpha = 1.0f
        binding.dcameraBtnShot.isEnabled = true
        binding.dcameraClInfo.visibility = View.INVISIBLE
        binding.dcameraIvSpoonFrame.visibility = View.VISIBLE
    }
    private fun getCameraInfo(): String {
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraIdList = cameraManager.cameraIdList
        var cameraInfo = ""
        for (cameraId in cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
            if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                // 후면 카메라
                val focalLength =
                    characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS) // 렌즈 초점 거리
                val physicalSize =
                    characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE)!! // 카메라 센서의 물리적인 크기
                val pixelArraySize =
                    characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE)!! // 센서에서 사용되는 픽셀 배열의 크기
                val horizontalAngle = 2 * atan(physicalSize.width / (2 * focalLength!![0]))
                val verticalAngle =
                    2 * atan(physicalSize.height / (2 * focalLength[0])) * pixelArraySize.width / pixelArraySize.height
                val spoonSize = (296f * resources.displayMetrics.density + 0.5f).toInt()
                Log.e(
                    "camera",
                    "카메라\nid : ${cameraId}\n렌즈 초점 거리 : ${focalLength[0]}\n센서 크기 : $physicalSize\n 카메라 픽셀 사이즈 : $pixelArraySize\n 수직 화각 : $verticalAngle\n 수평 화각 : $horizontalAngle\n 숟가락 사이즈 : $spoonSize"
                )
                cameraInfo = "${focalLength[0]} ${physicalSize.width} ${pixelArraySize.width} ${pixelArraySize.height} $verticalAngle $horizontalAngle $spoonSize"
                return cameraInfo
            }
        }
        return cameraInfo
    }

    override fun onDestroy() {
        client?.closeSocket()
        super.onDestroy()
    }
}