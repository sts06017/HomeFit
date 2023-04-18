package kr.rabbito.homefit.screens.navigatorBar

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.ui.ViewContainer
import kr.rabbito.homefit.databinding.CalendarDayLayoutBinding
import kr.rabbito.homefit.screens.WOHistoryActivity
import java.time.LocalDate

class DayViewContainer(view: View) : ViewContainer(view) {
    var textView = CalendarDayLayoutBinding.bind(view).calendarDayText
    lateinit var day : CalendarDay
    init{
        view.setOnClickListener {
            Log.d("캘린더", day.toString())
        }
    }
}