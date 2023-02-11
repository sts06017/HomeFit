package kr.rabbito.homefit.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kr.rabbito.homefit.client.HomeFitClient
import kr.rabbito.homefit.databinding.ActivityDcameraBinding

class DCameraActivity : AppCompatActivity() {
    private var mBinding: ActivityDcameraBinding? = null
    private val binding get() = mBinding!!

    private var client: HomeFitClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDcameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 서버 연결
        client = HomeFitClient()

        Thread {
            try {
                client!!.sendRequest()
            } catch (e: NullPointerException) {
                Log.d("connection", "socket not initialized")
            }
        }.start()

        // 임시
        binding.dcameraBtnShot.setOnClickListener {
            Thread {
                client!!.sendMessage("connection test")
                client!!.getMessage()
            }.start()
        }

        // 임시
        binding.dcameraBtnCancel.setOnClickListener {
            startActivity(Intent(this, DReportActivity::class.java))
        }
    }

    override fun onDestroy() {
        client?.closeSocket()
        super.onDestroy()
    }
}