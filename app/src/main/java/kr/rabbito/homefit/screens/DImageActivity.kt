package kr.rabbito.homefit.screens

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kr.rabbito.homefit.client.HomeFitClient
import kr.rabbito.homefit.databinding.ActivityDimageBinding
import kr.rabbito.homefit.utils.calc.Converter
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.atan

class DImageActivity : AppCompatActivity() {
    private var mBinding: ActivityDimageBinding? = null
    private val binding get() = mBinding!!

    // 통신
    private var client: HomeFitClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDimageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Thread {
            client = HomeFitClient()
            client!!.sendRequest()
            client!!.sendUserName("User")

            getAndSendPhoto()
        }.start()

        binding.dimageBtnCancel.setOnClickListener {
            finish()
        }
    }

    private fun getAndSendPhoto() {
        Thread {
            val cameraInfo = getCameraInfo()
            Log.d("cameraInfo", cameraInfo)
            val imageUriString = intent.getStringExtra("SELECTED_IMAGE")
            if (imageUriString != null) {
                val imageUri = Uri.parse(imageUriString)
                Log.d("selected_image", imageUriString)
                runOnUiThread {
                    binding.dimageIvBackground.setImageURI(imageUri)
                }

                val bitmap = Converter.imageUriToBitmap(contentResolver, imageUri)

                val startTime = System.currentTimeMillis()
                client!!.sendCameraInfo(cameraInfo)
                client!!.sendImage(this@DImageActivity, bitmap)
                val data: String? = client!!.getData(this@DImageActivity)

                val endTime = System.currentTimeMillis()
                Log.d("time gap", (endTime - startTime).toString())

                if (client == null || data == null) {
                    runOnUiThread {
                        Toast.makeText(this, "서버 연결에 실패했습니다.\n연결 상태를 확인해주세요.", Toast.LENGTH_SHORT)
                            .show()
                    }
                    finish()
                }

                if (data != null) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("VIEW_PAGER_INDEX", 1)
                    intent.putExtra("DATA", data)
                    intent.putExtra("DATE", LocalDate.now().toString())
                    intent.putExtra("TIME", LocalDateTime.now().toString())
                    startActivity(intent)
                    finish()
                }
            }
        }.start()
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