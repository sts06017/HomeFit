package kr.rabbito.homefit.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.rabbito.homefit.databinding.ActivityWohistoryBinding

class WOHistoryActivity : AppCompatActivity() {
    private var mBinding: ActivityWohistoryBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWohistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 임시
        binding.wohistoryVCalendar.setOnClickListener {
            startActivity(Intent(this, DReportActivity::class.java))
        }
    }
}