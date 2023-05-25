package kr.rabbito.homefit.screens

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.ColorTemplate.COLORFUL_COLORS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.rabbito.homefit.R
import kr.rabbito.homefit.data.User
import kr.rabbito.homefit.data.UserDB
import kr.rabbito.homefit.data.Workout
import kr.rabbito.homefit.data.WorkoutDB
import kr.rabbito.homefit.databinding.ActivityWoreportBinding
import kr.rabbito.homefit.utils.calc.TimeCalc
import kr.rabbito.homefit.utils.calc.Converter.Companion.pxToSp
import kr.rabbito.homefit.workout.WorkoutData
import kr.rabbito.homefit.workout.logics.getCalories
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class WOReportActivity : AppCompatActivity() {
    private var mBinding: ActivityWoreportBinding? = null
    private val binding get() = mBinding!!

    private var userDB: UserDB? = null
    private var userId: Long? = null

    private var weight: Int = 0
    private var workoutIdx: Int = 0
    private var totalCount: Int = 0
    private var workoutTime: Long = 0
    private var totalRestTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWoreportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val db = Room.databaseBuilder(
            this, WorkoutDB::class.java, "workout"
        )//.fallbackToDestructiveMigration()
            .build()
        userDB = UserDB.getInstance(this)
        userId = 0L  // 임시
        getWeightById(userId!!)  // 사용자 몸무게 정보 불러옴

        workoutIdx = intent.getIntExtra("index", 0)
        totalCount = intent.getIntExtra("count",0)
        workoutTime = intent.getLongExtra("woTime",0)
        totalRestTime = intent.getLongExtra("restTime",0)

        initView()

        val woreportVGraph1 = binding.woreportVGraph1
        val woreportVGraph2 = binding.woreportVGraph2

        val pieDateTest = mutableMapOf<String,Float>()
        pieDateTest["운동"] = workoutTime.toFloat() / 60000
        pieDateTest["휴식"]= totalRestTime.toFloat() / 60000

        createPieGraph(pieDateTest,woreportVGraph1)

        val xLineData = mutableListOf<String>()
        xLineData.add("Jan")
        xLineData.add("Feb")
        xLineData.add("Mar")
        xLineData.add("Apr")
        xLineData.add("May")
        xLineData.add("Jun")

        val yLineData = mutableListOf<Int>()
//        yLineData.add(40)
//        yLineData.add(10)
//        yLineData.add(30)
//        yLineData.add(3)
//        yLineData.add(35)
//        yLineData.add(25)

        val newWorkout = Workout(
            id = null,
            workoutName = WorkoutData.workoutNamesKOR[workoutIdx],
            set = 3,
            count = 10,
            woDuration = 0,
            date = LocalDate.now(),
            time = LocalDateTime.now().format(formatter().timeFormatter)
        )
        createLineChart(xLineData as ArrayList<String>, yLineData as ArrayList<Int>, woreportVGraph2)
        binding.woreportBtnHistory.setOnClickListener {
            startActivity(Intent(this, WOHistoryActivity::class.java))
        }
//        val migration_3_4 = object : Migration(3,4) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("DROP TABLE IF EXISTS Workout")
//            }
//        }
        binding.woreportBtnSaveReport.setOnClickListener {
            // 운동 결과 DB저장
            CoroutineScope(Dispatchers.IO).launch {
                db.workoutDAO().insert(newWorkout)
            }

            startActivity(Intent(this,WOHistoryActivity::class.java))
        }
        binding.woreportBtnDeleteReport.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }

    }
    private fun initView(){
        binding.woreportTvWo.text = WorkoutData.workoutNamesKOR[workoutIdx]
        binding.woreportTvCalorie.text = getCalories(WorkoutData.workoutNamesKOR[workoutIdx], weight, totalCount)
        binding.woreportTvCorrectTime.text = TimeCalc.milliSecFormat(workoutTime)
        binding.woreportTvStopTime.text = TimeCalc.milliSecFormat(totalRestTime)
        binding.woreportTvDate.text = LocalDateTime.now().format(formatter().dateFormatter_ko) // 임시
//        piechart 출력
//        linechart 출력

    }

    private fun createPieGraph(data: Map<String,Float>, chart: PieChart){
        //------입력 데이터 pieEntry로 변환------
        val testEntries = ArrayList<PieEntry>()
        for(i in data.entries){
            testEntries.add(PieEntry(i.value,i.key))
        }

        //------chart colors (chart 색조합 저장)------
        val colorsItems = ArrayList<Int>()
//        for(c in ColorTemplate.VORDIPLOM_COLORS) colorsItems.add(c)
//        for(c in ColorTemplate.JOYFUL_COLORS) colorsItems.add(c)
        for(c in COLORFUL_COLORS) colorsItems.add(c)
        for(c in ColorTemplate.LIBERTY_COLORS) colorsItems.add(c)
        for(c in ColorTemplate.PASTEL_COLORS) colorsItems.add(c)

        //------chart data 폰트 불러오기------
        val typeface = ResourcesCompat.getFont(this, R.font.pretendard_regular)!!

        //------chart dataset 설정------
        val pieDataSet = PieDataSet(testEntries,"")
        pieDataSet.apply {
            colors = colorsItems
            valueTextColor = Color.WHITE
            valueTextSize = 11f
        }
        pieDataSet.setDrawValues(false)
        val pieData = PieData(pieDataSet)

        //------chart 디자인 요소들 설정------
        chart.apply {
            chart.data = pieData
            setUsePercentValues(true)
            description.isEnabled = false
            isRotationEnabled = false
            setEntryLabelColor(Color.WHITE)
            animateY(1400, Easing.EaseInOutQuad)
            animate()
            setDrawEntryLabels(false)
            setHoleColor(Color.TRANSPARENT)
            legend.textSize = 11f
            legend.textColor = Color.WHITE
            legend.typeface = typeface
            legend.form = Legend.LegendForm.CIRCLE
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            holeRadius = 80f
            transparentCircleRadius = 0f
        }
    }

    // line chart -> 데이터 최신화 및 변동 데이터 입력 처리 필요
    fun createLineChart(xValues : ArrayList<String>, yValues : ArrayList<Int>, chart: LineChart){
        val entries = ArrayList<Entry>()
        var y = 10
        for(i in yValues){
            entries.add(Entry(y.toFloat(),i.toFloat()))
            y += 10
        }

//        val xValues = ArrayList<String>()
//        xValues.add("Jan")
//        xValues.add("Feb")
//        xValues.add("Mar")
//        xValues.add("Apr")
//        xValues.add("May")
//        xValues.add("Jun")

        val yValues = ArrayList<Entry>()
        yValues.add(Entry(10f, 40F))
        yValues.add(Entry(20f, 10F))
        yValues.add(Entry(30f, 30F))
        yValues.add(Entry(40f, 3F))
        yValues.add(Entry(50f, 40F))
        yValues.add(Entry(60f, 30F))

        chart.xAxis.valueFormatter = IndexAxisValueFormatter(xValues)
        chart.xAxis.position = XAxis.XAxisPosition.TOP
        chart.xAxis.granularity = 5f

        val leftAxis = chart.axisLeft
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 40f
        leftAxis.textColor = Color.WHITE


        val dataSet = LineDataSet(yValues, "횟수")
        dataSet.apply{
            color = Color.parseColor("#6BE3CF")
            lineWidth = 1f      // 그래프 선 두께 설정
            circleRadius = 2f   // 그래프 점 크기 설정
            setCircleColor(Color.WHITE)     // 그래프 점 색 변경
            valueTextSize = 0f  // 그래프 각 value 텍스트크기 설정
            dataSet.setDrawValues(false)
        }

        val lineData = LineData(dataSet)
        chart.data = lineData

        chart.apply {
            setBackgroundColor(Color.TRANSPARENT)
            xAxis.setDrawGridLines(false)       // x축 선 제거
            axisLeft.setDrawAxisLine(false)     // 좌측 y축 선 제거
            axisRight.setDrawAxisLine(false)    // 우측 y축 선 제거
            axisLeft.gridColor = Color.WHITE    // 가로 선 색 변경
            axisRight.isEnabled = false
            setTouchEnabled(false)
            description.isEnabled = false
            xAxis.setDrawLabels(false)
            description.text = "차트 제목"
            description.textColor = Color.WHITE
            description.setPosition(chart.width.toFloat(),0f)
            description.typeface = Typeface.DEFAULT_BOLD
            legend.isEnabled = false
        }

        chart.invalidate()

    }
    inner class formatter{
        val dateFormatter_ko = DateTimeFormatter.ofPattern("yyyy년 M월 dd일")
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-M-dd")
        val timeFormatter = DateTimeFormatter.ofPattern( "a HH시 mm분 ss초").withLocale(Locale.forLanguageTag("ko"))
    }
    private fun getWeightById(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            weight = userDB?.userDAO()?.getWeightById(id)!!
        }
    }
}
