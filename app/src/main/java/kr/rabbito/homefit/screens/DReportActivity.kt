package kr.rabbito.homefit.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.rabbito.homefit.databinding.ActivityDreportBinding

class DReportActivity : AppCompatActivity() {
    private var mBinding: ActivityDreportBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDreportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.dreportBtnAdd.setOnClickListener {
            startActivity(Intent(this, DAddActivity::class.java))
        }

        binding.dreportBtnHistory.setOnClickListener {
            startActivity(Intent(this, DHistoryActivity::class.java))
        }
    }
}