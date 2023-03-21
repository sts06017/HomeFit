package kr.rabbito.homefit.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.rabbito.homefit.databinding.ActivityWohistoryBinding
import kr.rabbito.homefit.screens.navigatorBar.DReportFragment

class WOHistoryActivity : AppCompatActivity() {
    private var mBinding: ActivityWohistoryBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWohistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 임시
        binding.wohistoryVCalendar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("VIEW_PAGER_INDEX",1)
            startActivity(intent)
        }
    }
}