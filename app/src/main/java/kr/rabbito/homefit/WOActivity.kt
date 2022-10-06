package kr.rabbito.homefit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.rabbito.homefit.databinding.ActivityWoBinding

class WOActivity : AppCompatActivity() {
    private var mBinding: ActivityWoBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 임시
        binding.woBtnStop.setOnClickListener {
            startActivity(Intent(this, WOReportActivity::class.java))
        }
    }
}