package kr.rabbito.homefit.screens

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kr.rabbito.homefit.client.HomeFitClient
import kr.rabbito.homefit.databinding.ActivityDimageBinding
import kr.rabbito.homefit.utils.calc.Converter
import java.time.LocalDate

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
            val imageUriString = intent.getStringExtra("SELECTED_IMAGE")
            if (imageUriString != null) {
                val imageUri = Uri.parse(imageUriString)
                Log.d("selected_image", imageUriString)
                runOnUiThread {
                    binding.dimageIvBackground.setImageURI(imageUri)
                }

                val bitmap = Converter.imageUriToBitmap(contentResolver, imageUri)

                val startTime = System.currentTimeMillis()

                client!!.sendImage(this@DImageActivity, bitmap)
                val data: String? = client!!.getData(this@DImageActivity)

                val endTime = System.currentTimeMillis()
                Log.d("time gap", (endTime - startTime).toString())

                if (client == null) {
                    Toast.makeText(this, "서버 연결에 실패했습니다.\n연결 상태를 확인해주세요.", Toast.LENGTH_SHORT)
                        .show()
                }

                if (data != null) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("VIEW_PAGER_INDEX", 1)
                    intent.putExtra("DATA", data)
                    intent.putExtra("DATE", LocalDate.now().toString())
                    startActivity(intent)
                }
            }
        }.start()
    }

    override fun onDestroy() {
        client?.closeSocket()
        super.onDestroy()
    }
}