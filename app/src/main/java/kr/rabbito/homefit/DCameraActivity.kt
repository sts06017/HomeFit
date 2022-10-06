package kr.rabbito.homefit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.rabbito.homefit.databinding.ActivityDcameraBinding

class DCameraActivity : AppCompatActivity() {
    private var mBinding: ActivityDcameraBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDcameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 임시
        binding.dcameraBtnCancel.setOnClickListener {
            startActivity(Intent(this, DReportActivity::class.java))
        }
    }
}