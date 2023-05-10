package kr.rabbito.homefit.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import kr.rabbito.homefit.R
import kr.rabbito.homefit.screens.calendar.SetCalendar
import kr.rabbito.homefit.databinding.ActivityWohistoryBinding
import kr.rabbito.homefit.screens.calendar.daysOfWeek
import java.time.format.TextStyle


import java.util.*

class WOHistoryActivity : AppCompatActivity() {
    private var mBinding: ActivityWohistoryBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWohistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val caledar_v = binding.wohistoryVCalendar
        val calendar_tv_weekTitles = findViewById<ViewGroup>(R.id.wohistory_v_weekTitles)
        val calendar_tv_monthTitle = binding.wohistoryTvMonth
        SetCalendar(caledar_v, calendar_tv_monthTitle,this).setting()

        binding.wohistoryBtnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("VIEW_PAGER_INDEX",0)
            startActivity(intent)
        }

        calendar_tv_weekTitles.children
            .map { it as TextView }
            .forEachIndexed { idx, textView ->
                val dayOfWeek = daysOfWeek()[idx]
                val title = dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault())
                textView.text = title
            }
    }
}