package kr.rabbito.homefit.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.rabbito.homefit.R
import kr.rabbito.homefit.R.*
import kr.rabbito.homefit.databinding.ActivityDhistoryBinding
import kr.rabbito.homefit.screens.navigatorBar.ProfileFragment

class DHistoryActivity : AppCompatActivity() {
    private var mBinding: ActivityDhistoryBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDhistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 임시
        binding.dhistoryVCalendar.setOnClickListener {
//            startActivity(Intent(this, ProfileFragment::class.java))
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("VIEW_PAGER_INDEX",3)
            startActivity(intent)
        }
    }
}