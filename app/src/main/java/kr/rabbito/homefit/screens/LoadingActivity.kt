package kr.rabbito.homefit.screens

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.rabbito.homefit.data.User
import kr.rabbito.homefit.data.UserDB
import kr.rabbito.homefit.databinding.ActivityLoadingBinding

class LoadingActivity : AppCompatActivity() {
    private var mBinding: ActivityLoadingBinding? = null
    private val binding get() = mBinding!!

    private val CAMERA_REQUEST_CODE = 100

    private var userDB: UserDB? = null
    private var userId: Long? = null

    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDB = UserDB.getInstance(this)

        userId = 0
        loadUserById(userId!!)

        checkPermission()
    }

    private fun checkPermission() {
        // 1. 위험권한(Camera) 권한 승인상태 가져오기
        val cameraPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
            // 카메라 권한이 승인된 상태일 경우
            startLoading()
        } else {
            requestPermission()
        }
    }
    // 2. 권한 요청
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    // 권한 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLoading()
                } else {
                    finish()
                }
            }
        }
    }

    private fun startLoading() {
        val handler = Handler()
        handler.postDelayed({
            if (user == null) {
                startActivity(Intent(this, InitActivity::class.java))
            } else {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("VIEW_PAGER_INDEX", 0)
                startActivity(intent)
            }
            finish()
        }, 1000)
    }

    private fun loadUserById(id: Long) {
        if (userDB != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val users = userDB?.userDAO()?.getUserById(id)
                if (users!!.isNotEmpty()) {
                    user = users[0]
                }
            }
        }
    }
}