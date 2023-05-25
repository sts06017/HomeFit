package kr.rabbito.homefit.screens

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kr.rabbito.homefit.client.HomeFitClient
import kr.rabbito.homefit.databinding.ActivityDimageBinding
import kr.rabbito.homefit.utils.calc.Converter

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

            try {
                client!!.sendRequest()
                client!!.sendUserName("User")
            } catch (e: NullPointerException) {
                Log.d("connection", "socket not initialized")
            }

            val imageUriString = intent.getStringExtra("SELECTED_IMAGE")
            val imageUri = Uri.parse(imageUriString)

            val bitmap = Converter.imageUriToBitmap(contentResolver, imageUri)
            runOnUiThread {
                binding.dimageIvBackground.setImageURI(imageUri)
            }

            client!!.sendImage(this, bitmap)
            val data = client!!.getData(this@DImageActivity)!!

            val intent = Intent(this@DImageActivity, MainActivity::class.java)
            intent.putExtra("VIEW_PAGER_INDEX", 1)
            intent.putExtra("DATA", data)
            startActivity(intent)
        }.start()
    }

    override fun onDestroy() {
        client?.closeSocket()
        super.onDestroy()
    }
}