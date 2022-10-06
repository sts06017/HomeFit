package kr.rabbito.homefit

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import kr.rabbito.homefit.databinding.ActivityLoadingBinding

class LoadingActivity : AppCompatActivity() {
    private var mBinding: ActivityLoadingBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startLoading()
    }

    private fun startLoading() {
        val handler = Handler()
        handler.postDelayed({
            finish()
            startActivity(Intent(this, InitActivity::class.java))
        }, 1000)
    }

}