package kr.rabbito.homefit.screens.calendar

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.rabbito.homefit.R
import kr.rabbito.homefit.data.Workout
import kr.rabbito.homefit.data.WorkoutDB
import kr.rabbito.homefit.databinding.CalendarDayLayoutBinding
import kr.rabbito.homefit.screens.adapter.WOHistoryAdapter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Locale


private val today = LocalDate.now()
private var selectedDate: LocalDate? = null

class WOHistorySetCalendar(calendarView_: CalendarView, title_ : TextView, context: Context, recyclerView: RecyclerView) {
    private val calendarView = calendarView_
    private val title = title_
    private val recyclerView = recyclerView

    private val context = context
    //    val calendarView:CalendarView = activity.findViewById(R.id.calendarView)
    private val db = Room.databaseBuilder(context, WorkoutDB::class.java, "workout").build()

    fun setting() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        calendarView.monthScrollListener = {
            // Select the first day of the visible month.
            if(it.month==LocalDate.now().monthValue)
                selectDate(it.yearMonth.atDay(LocalDate.now().dayOfMonth))
            else
                selectDate(it.yearMonth.atDay(1))
        }
        val daysOfWeek = daysOfWeek()
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(50)
        val endMonth = currentMonth.plusMonths(50)
        configureBinders(daysOfWeek)
        calendarView.apply {
            setup(startMonth, endMonth, daysOfWeek.first())
            scrollToMonth(currentMonth)
        }
    }

    private fun selectDate(date: LocalDate) {
        // 캘린더 클릭,스크롤시 호출되는 함수.

        // 기능1  : 이전에 선택된 날짜를 oldDate에 저장 후 방금 선택한 날짜를 selectedDate에 저장.
        Log.d("캘린더","call selectDate()")
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { calendarView.notifyDateChanged(it) }
            calendarView.notifyDateChanged(date)
        }
        // 기능2  : 캘린더 상단의 {년.월} textView를 수정함.
        this.title.text = "${date.year}.${date.month.value}"

        // 기능3  : DB에서 선택날짜에 해당되는 데이터 리사이클러뷰에 출력.
        (context as? LifecycleOwner)?.lifecycleScope?.launch {
            val workouts = withContext(Dispatchers.IO) {
                // 데이터베이스에서 데이터를 가져옴.
                db.workoutDAO().getWorkoutByDate(date)
            }
            // 메인 스레드에서 UI를 갱신.
            val adapter = WOHistoryAdapter(workouts)
            recyclerView.adapter = adapter
        }
        //** DB 테이블 확인 코드
        CoroutineScope(Dispatchers.IO).launch {
            val workouts : List<Workout> = db.workoutDAO().getAll()
            Log.d("최승호","----Workout DB 조회----")
            workouts.forEach { workout ->
                Log.d("최승호", "DB id:${workout.id},      Set:${workout.set}, Count:${workout.count},     Duration:${workout.woDuration},     Date:${workout.date},   Time:${workout.time},   Name:${workout.workoutName}")
            }
        }
    }

    private fun configureBinders(daysOfWeek: List<DayOfWeek>) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = CalendarDayLayoutBinding.bind(view)
            init {
                view.setOnClickListener {
//                    Log.d("캘린더","configureBinders_init")
                    if (day.owner == DayOwner.THIS_MONTH) {
                        selectDate(day.date)
                        // day.date 포멧 : YYYY-MM-DD
                        Log.d("캘린더","click! date:${day.date} day:${day.day} ow:${day.owner}")
                    }
                }
            }
        }
        calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.binding.calendarDayText
                textView.text = day.date.dayOfMonth.toString()

                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.makeVisible()
                    when (day.date) {
                        today -> {
                            textView.setTextColorRes(R.color.calendar_white)
                            textView.setBackgroundResource(R.drawable.calendar_bg_today)
                        }

                        selectedDate -> {
                            textView.setTextColorRes(R.color.calendar_blue)
                            textView.setBackgroundResource(R.drawable.calendar_bg_selected)
                        }

                        else -> {
                            textView.setTextColorRes(R.color.calendar_white)
                            textView.background = null
                        }
                    }
                } else {
                    textView.makeInVisible()
                }
            }
        }
    }
}