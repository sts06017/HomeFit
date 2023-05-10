package kr.rabbito.homefit.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import kr.rabbito.homefit.R
import kr.rabbito.homefit.R.*
import kr.rabbito.homefit.databinding.ActivityDhistoryBinding
import kr.rabbito.homefit.screens.calendar.SetCalendar
import kr.rabbito.homefit.screens.calendar.daysOfWeek
import kr.rabbito.homefit.screens.navigatorBar.ProfileFragment
import java.time.format.TextStyle
import java.util.Locale

class DHistoryActivity : AppCompatActivity() {
    private var mBinding: ActivityDhistoryBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDhistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val calendar_v = binding.dhistoryVCalendar
        val calendar_tv_weekTitles = findViewById<ViewGroup>(R.id.dhistory_v_weekTitles)
        val calendar_v_monthTitle = binding.dhistoryTvMonth

        SetCalendar(calendar_v, calendar_v_monthTitle, this).setting()
        // 임시
        binding.dhistoryVCalendar.setOnClickListener {
//            startActivity(Intent(this, ProfileFragment::class.java))
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("VIEW_PAGER_INDEX",3)
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