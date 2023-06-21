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
import kr.rabbito.homefit.data.DietDB
import kr.rabbito.homefit.data.UserDB
import kr.rabbito.homefit.data.Workout
import kr.rabbito.homefit.data.WorkoutDB
import kr.rabbito.homefit.databinding.ActivityWoreportBinding
import kr.rabbito.homefit.screens.navigatorBar.DietCalorie
import kr.rabbito.homefit.utils.calc.Converter.Companion.dateFormatter_ko
import kr.rabbito.homefit.utils.calc.TimeCalc
import kr.rabbito.homefit.workout.logics.getCalories
import java.lang.Math.abs
import java.time.LocalDate

data class commentData(val prevCount: Int?, val nowCount: Int, val todayCalorie: Int?)
class WOReportActivity : AppCompatActivity() {
    private var mBinding: ActivityWoreportBinding? = null
    private val binding get() = mBinding!!

    private var userDB: UserDB? = null
    private var userId: Long? = null
    private var workoutDB: WorkoutDB? = null
    private lateinit var workout: Workout
    private var dietDB : DietDB? = null

    private var weight = 0
    private var comment = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWoreportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        workoutDB = Room.databaseBuilder(
            this, WorkoutDB::class.java, "workout"
        ).build()
        dietDB = DietDB.getInstance(this)
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
        // 운동 코멘트를 생성하는 함수
        val woComment = createWorkoutComment()
        Log.d("최승호","woComment: $comment")
        binding.woreportTvComment.text = comment
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
    private suspend fun getTodayCalorie() :Int?{    // 오늘 혹은 선택한 운동 날짜의 총 섭취 칼로리 가져오는 기능 수행
        var result = 0
        val todayDate = workout.date.toString() // 액티비티로 이동되며 전달받은 인텐트 중 운동DB객체의 날짜 뽑아옴
        val todayCalorie = dietDB?.DietDAO()?.getTodayCalories(todayDate ?: LocalDate.now().toString())

        if (todayCalorie != null) result = todayCalorie.toInt()
        return result   // 해당 날짜에 섭취한 음식이 없는경우 0을 반환
    }

    // 이 함수가 어느 위치에 들어가야 하는지
    // 코멘트를 문자열로 반환 - binding.woreportTvComment.text = createWorkoutComment(prevCount, nowCount) 형태로 적용하면 됨
    // 응용은 DReportFragment의 createNutrientComment() 참고
    private fun createWorkoutComment() {
        // count 등을 꼭 인자로 받을 필요는 없음, 함수 내에서 DB 호출해도 괜찮고, 편한대로
        val nowCount = workout.count!!
        var result = ""
        // 코루틴 - 선택날짜의 count, calorie 불러오기 수행
        CoroutineScope(Dispatchers.IO).launch {
            var prevCount = -1
            prevCount = if (workout.id == null){
                // DB id가 null일 경우 : DB에 가장 최근에 저장된 운동의 count 가져옴
                val maxId = workoutDB?.workoutDAO()?.getMaxId()!!
                workoutDB?.workoutDAO()?.getCountById(maxId)!!
            } else{
                // DB id가 null이 아닌경우 : DB에 선택한 운동의 전에 저장된 운동의 count 가져옴
                workoutDB?.workoutDAO()?.getCountById(workout.id!! - 1)!!
            }

            val todayCalories = getTodayCalorie()   // workoutDB에 담겨있는 날짜정보로 calorie 가져옴
            val commentData = commentData(prevCount,nowCount,todayCalories) // 이전count, 현재count, 총calorie 정보 객체

            commentData.let {   // 정보객체 활용코드 부분 (it->prevCount, newCount, todayCalorie)
                if (it.prevCount == -1 || it.prevCount == null || it.todayCalorie == null) {
//                    return null
                    // 칼로리 또는 이전 운동 데이터가 없는 경우
                    Log.d("최승호","prevCount or Calorie is null")
                    if (it.todayCalorie == null)Log.d("WOReport","Calorie is null")
                }
                else{
                    val commentList = ArrayList<String>()
                    commentList.add(getString(R.string.wo_result_basic))    // 기본 문자열

                    val gap = abs(it.prevCount - it.nowCount)
                    if (it.prevCount < it.nowCount) {
                        commentList.add("이전보다 ${gap}회 더 많이 했습니다.")
                    } else if (it.prevCount > it.nowCount) {
                        commentList.add("이전보다 ${gap}회 더 적게 했습니다.")
                    }

                    /*
                    식단 연계 코멘트 추가 - 논의 필요
                     */
                    commentList.add(" 칼로리 : ${it.todayCalorie}")    // 오늘 섭취한 총 칼로리

                    result = commentList.takeLast(2).joinToString("\n") // 결과가 너무 길게 나오지 않도록, 최대 2문장만 출력되도록 설정

                    binding.woreportTvComment.text = result // 완성된 코멘트 변경
                }
            }
        }
    }
}
