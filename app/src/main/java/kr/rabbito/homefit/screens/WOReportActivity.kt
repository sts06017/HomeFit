package kr.rabbito.homefit.screens

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.room.Room
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.ColorTemplate.COLORFUL_COLORS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.rabbito.homefit.R
import kr.rabbito.homefit.data.UserDB
import kr.rabbito.homefit.data.Workout
import kr.rabbito.homefit.data.WorkoutDB
import kr.rabbito.homefit.databinding.ActivityWoreportBinding
import kr.rabbito.homefit.utils.calc.Converter.Companion.dateFormatter_ko
import kr.rabbito.homefit.utils.calc.TimeCalc
import kr.rabbito.homefit.workout.logics.getCalories
import java.lang.Math.abs

class WOReportActivity : AppCompatActivity() {
    private var mBinding: ActivityWoreportBinding? = null
    private val binding get() = mBinding!!

    private var userDB: UserDB? = null
    private var userId: Long? = null
    private var workoutDB: WorkoutDB? = null
    private lateinit var workout: Workout

    private var weight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWoreportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        workoutDB = Room.databaseBuilder(
            this, WorkoutDB::class.java, "workout"
        ).build()
        userDB = UserDB.getInstance(this)
        userId = 0L  // 임시
        getWeightById(userId!!)  // 사용자 몸무게 정보 불러오고 소모칼로리 계산.

        workout = intent.getParcelableExtra("workout")!!

        getCurrentCounts(workout.id)    // 최근 운동기록의 횟수 가져오고, lineChart에 출력
        initView()

        val woTimeData = mutableMapOf<String,Float>()
        woTimeData["운동"] = workout.woDuration!!.toFloat() / 60000
        woTimeData["휴식"]= workout.restTime!!.toFloat() / 60000

        createPieGraph(woTimeData,binding.woreportVGraph1)

        binding.woreportBtnBack.setOnClickListener {
            finish()
        }

        binding.woreportBtnHistory.setOnClickListener {
            startActivity(Intent(this, WOHistoryActivity::class.java))
        }

        binding.woreportBtnSaveReport.setOnClickListener {
            // 운동 결과 DB저장
            CoroutineScope(Dispatchers.IO).launch {
                workoutDB!!.workoutDAO().insert(workout)
            }

            startActivity(Intent(this,WOHistoryActivity::class.java))
            finish()
        }
        binding.woreportBtnDeleteReport.setOnClickListener {
            // 운동 결과 DB삭제
            if (workout.id != null){
                CoroutineScope(Dispatchers.IO).launch {
                    workoutDB!!.workoutDAO().deleteRecord(workout.id!!)
                }
            }
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

    }
    private fun initView(){
        binding.woreportTvWo.text = workout.workoutName
        binding.woreportTvCorrectTime.text = TimeCalc.milliSecFormat(workout.woDuration!!)
        binding.woreportTvStopTime.text = TimeCalc.milliSecFormat(workout.restTime!!)
        binding.woreportTvDate.text = workout.date!!.format(dateFormatter_ko) // 임시

        if(workout.id != null){
            binding.woreportBtnSaveReport.text = "뒤로가기"
            binding.woreportBtnSaveReport.setOnClickListener {
                finish()
            }
        }

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

    private fun createLineChart(yValues : MutableList<Float>, chart: LineChart){
        val entries = ArrayList<Entry>()
        var y = 10

        for(i in yValues){
            entries.add(Entry(y.toFloat(),i))
            y += 10
        }

        val xValues = ArrayList<String>()
        xValues.add("0")
        xValues.add("1")
        xValues.add("2")
        xValues.add("3")
        xValues.add("4")
        xValues.add("5")

        chart.xAxis.valueFormatter = IndexAxisValueFormatter(xValues)
        chart.xAxis.position = XAxis.XAxisPosition.TOP
        chart.xAxis.granularity = 5f

        val leftAxis = chart.axisLeft
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 50f
        leftAxis.textColor = Color.WHITE


        val dataSet = LineDataSet(entries, "횟수")
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

    private fun getWeightById(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            weight = userDB?.userDAO()?.getWeightById(id)!!

            weight?.let{
                withContext(Dispatchers.Main){
                    binding.woreportTvCalorie.text = getCalories(workout.workoutName!!, it, workout.count!!)
                }
            }
        }
    }
    private fun getCurrentCounts(id: Int?) {
        var woId = id
        CoroutineScope(Dispatchers.IO).launch {
            if (id == null){
                woId = workoutDB?.workoutDAO()?.getMaxId()!!
            }
            else {
                woId = woId!! - 1
            }

            val currentCounts = workoutDB?.workoutDAO()?.getCurrentCounts(woId!!)?.reversed()?.toMutableList()
            woId?.let{
                currentCounts?.let {
                    currentCounts.add(workout.count!!)
                    val lineChartData = currentCounts.map { it.toFloat() }.toMutableList()
                    createLineChart(lineChartData, binding.woreportVGraph2)
                }
            }
        }
    }

    // 이 함수가 어느 위치에 들어가야 하는지
    // 코멘트를 문자열로 반환 - binding.woreportTvComment.text = createWorkoutComment(prevCount, nowCount) 형태로 적용하면 됨
    // 응용은 DReportFragment의 createNutrientComment() 참고
    private fun createWorkoutComment(prevCount: Int, nowCount: Int): String? {
        // count 등을 꼭 인자로 받을 필요는 없음, 함수 내에서 DB 호출해도 괜찮고, 편한대로

        if (prevCount == -1) {
            return null
        }

        val commentList = ArrayList<String>()
        commentList.add(getString(R.string.wo_result_basic))    // 기본 문자열

        val gap = abs(prevCount - nowCount)
        if (prevCount < nowCount) {
            commentList.add("이전보다 ${gap}회 더 많이 했습니다.")
        } else if (prevCount > nowCount) {
            commentList.add("이전보다 ${gap}회 더 적게 했습니다.")
        }

        /*
        식단 연계 코멘트 추가 - 논의 필요
         */

//        Log.d("check result", commentList.joinToString("\n"))
        val result = commentList.takeLast(2).joinToString("\n") // 결과가 너무 길게 나오지 않도록, 최대 2문장만 출력되도록 설정

        return result
    }
}
