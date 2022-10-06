package kr.rabbito.homefit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.rabbito.homefit.databinding.ActivityDhistoryBinding

class DHistoryActivity : AppCompatActivity() {
    private var mBinding: ActivityDhistoryBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDhistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 임시
        binding.dhistoryVCalendar.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}