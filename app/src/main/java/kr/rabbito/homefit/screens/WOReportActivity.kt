package kr.rabbito.homefit.screens

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
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
import kr.rabbito.homefit.utils.calc.Converter.Companion.dateFormatter_ko
import kr.rabbito.homefit.utils.calc.TimeCalc
import kr.rabbito.homefit.utils.setWidthPercentage
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
    private var dietDB: DietDB? = null

    private var weight = 0
    private var comment = ""

    private val commentList = ArrayList<String>()

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

        getCurrentCountsByWOName(workout.workoutName!!,workout.id)    // 최근 운동기록의 횟수 가져오고, lineChart에 출력

        val woTimeData = mutableMapOf<String, Float>()
        woTimeData["운동"] = workout.woDuration!!.toFloat() / 60000
        woTimeData["휴식"] = workout.restTime!!.toFloat() / 60000

        createPieGraph(woTimeData, binding.woreportVGraph1)

        binding.woreportBtnBack.setOnClickListener {
            onBackPressed()
        }

        binding.woreportBtnHistory.setOnClickListener {
            val builder = AlertDialog.Builder(this, R.style.DeleteAlertDialog)
            builder.setMessage("히스토리 창으로 이동하면 현재의 운동 결과는 삭제됩니다.")
                .setPositiveButton("삭제 후 이동") { dialog, _ ->
                    startActivity(Intent(this, WOHistoryActivity::class.java))
                    finish()
                }.create().apply {
                    show()
                    setWidthPercentage(0.8)
                }
        }

        binding.woreportBtnSaveReport.setOnClickListener {
            // 운동 결과 DB저장
            CoroutineScope(Dispatchers.IO).launch {
                workoutDB!!.workoutDAO().insert(workout)
            }

            startActivity(Intent(this, WOHistoryActivity::class.java))
            finish()
        }

        binding.woreportBtnDeleteReport.setOnClickListener {
            val builder = AlertDialog.Builder(this, R.style.DeleteAlertDialog)
            builder.setMessage("정보를 삭제하시겠습니까?")
                .setPositiveButton("삭제") { dialog, _ ->
                    // 운동 결과 DB삭제
                    if (workout.id != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            workoutDB!!.workoutDAO().deleteRecord(workout.id!!)
                        }
                    }


                    startActivity(Intent(this, MainActivity::class.java))
                    intent.putExtra("VIEW_PAGER_INDEX",0)

                    finish()
                }.create().apply {
                    show()
                    setWidthPercentage(0.8)
                }
        }

        binding.woreportBtnLongDeleteReport.setOnClickListener {
            val builder = AlertDialog.Builder(this, R.style.DeleteAlertDialog)
            builder.setMessage("정보를 삭제하시겠습니까?")
                .setPositiveButton("삭제") { dialog, _ ->
                    // 운동 결과 DB삭제
                    if (workout.id != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            workoutDB!!.workoutDAO().deleteRecord(workout.id!!)
                        }
                    }

                    startActivity(Intent(this, WOHistoryActivity::class.java))
                    finish()
                }.create().apply {
                    show()
                    setWidthPercentage(0.8)
                }
        }
    }

    private fun initView() {

        binding.woreportTvWo.text = workout.workoutName
        binding.woreportTvCorrectTime.text = TimeCalc.milliSecFormat(workout.woDuration!!)
        binding.woreportTvStopTime.text = TimeCalc.milliSecFormat(workout.restTime!!)
        binding.woreportTvDate.text = workout.date!!.format(dateFormatter_ko) // 임시

        if (workout.id != null) {
            binding.woreportBtnLongDeleteReport.visibility = View.VISIBLE

            binding.woreportBtnSaveReport.visibility = View.GONE
            binding.woreportBtnDeleteReport.visibility = View.GONE

            binding.woreportBtnHistory.visibility = View.INVISIBLE
        }else{
            binding.woreportBtnLongDeleteReport.visibility = View.GONE

            binding.woreportBtnSaveReport.visibility = View.VISIBLE
            binding.woreportBtnDeleteReport.visibility = View.VISIBLE

            binding.woreportBtnHistory.visibility = View.VISIBLE
        }

        CoroutineScope(Dispatchers.Main).launch {
            // 운동 코멘트를 생성하는 함수
            setWorkoutComment()
            binding.woreportTvComment.text = commentList.takeLast(2).joinToString("\n")
//            Log.d("check text", binding.woreportTvComment.text.toString())
        }

    }

    private fun createPieGraph(data: Map<String, Float>, chart: PieChart) {
        //------입력 데이터 pieEntry로 변환------
        val testEntries = ArrayList<PieEntry>()
        for (i in data.entries) {
            testEntries.add(PieEntry(i.value, i.key))
        }

        //------chart data 폰트 불러오기------
        val typeface = ResourcesCompat.getFont(this, R.font.pretendard_regular)!!

        //------chart dataset 설정------
        val pieDataSet = PieDataSet(testEntries, "")
        pieDataSet.apply {
            valueTextColor = Color.WHITE
            valueTextSize = 11f
        }
        pieDataSet.setColors(this.getColor(R.color.pie_chart_1)
            , this.getColor(R.color.pie_chart_2)
            , this.getColor(R.color.pie_chart_3)
            , this.getColor(R.color.pie_chart_4))
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

    private fun createLineChart(yValues: MutableList<Float>, chart: LineChart) {
        val entries = ArrayList<Entry>()
        var y = 10

        for (i in yValues) {
            entries.add(Entry(y.toFloat(), i))
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

        val maxWOCount = entries.maxByOrNull { it.y }
        val leftAxis = chart.axisLeft
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = roundToNearestTen(maxWOCount?.y ?: 50f).toFloat()
        Log.d("최승호","leftAxis : ${leftAxis.axisMaximum}")
        leftAxis.textColor = Color.WHITE


        val dataSet = LineDataSet(entries, "횟수")
        dataSet.apply {
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
//            axisLeft.setLabelCount(5, true)
            axisRight.isEnabled = false
            setTouchEnabled(false)
            description.isEnabled = false
            xAxis.setDrawLabels(false)
            description.text = "차트 제목"
            description.textColor = Color.WHITE
            description.setPosition(chart.width.toFloat(), 0f)
            description.typeface = Typeface.DEFAULT_BOLD
            legend.isEnabled = false
        }

        chart.invalidate()

    }

    private fun getWeightById(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            weight = userDB?.userDAO()?.getWeightById(id)!!

            weight?.let {
                withContext(Dispatchers.Main) {
                    binding.woreportTvCalorie.text =
                        getCalories(workout.workoutName!!, it, workout.count!!)
                }
            }
        }
    }

    private fun getCurrentCountsByWOName(workoutName: String, id: Int?) {
        var woId = id
        CoroutineScope(Dispatchers.IO).launch {
            if (id == null) {
                woId = workoutDB?.workoutDAO()?.getMaxId()!!
            } else {
                woId = woId!! - 1
            }

            val currentCounts =
                workoutDB?.workoutDAO()?.getCurrentCountsByWOName(workoutName,woId!!)?.reversed()?.toMutableList()
            woId?.let {
                currentCounts?.let {
                    currentCounts.add(workout.count!!)
                    val lineChartData = currentCounts.map { it.toFloat() }.toMutableList()
                    createLineChart(lineChartData, binding.woreportVGraph2)
                }
            }
        }
    }

    private suspend fun getTodayCalorie(): Int? {    // 오늘 혹은 선택한 운동 날짜의 총 섭취 칼로리 가져오는 기능 수행
        var result = 0
        val todayDate = workout.date.toString() // 액티비티로 이동되며 전달받은 인텐트 중 운동DB객체의 날짜 뽑아옴
        val todayCalorie =
            dietDB?.DietDAO()?.getTodayCalories(todayDate ?: LocalDate.now().toString())

        if (todayCalorie != null) result = todayCalorie.toInt()
        return result   // 해당 날짜에 섭취한 음식이 없는경우 0을 반환
    }

    private suspend fun setWorkoutComment() = withContext(Dispatchers.IO) {
        commentList.clear()

        val nowCount = workout.count!!

        var prevCount = -1

        Log.d("checkRest", workout.woDuration.toString() + " " + workout.restTime.toString())

        val userWeight = userDB?.userDAO()?.getWeightById(0)

        val todayCalories = getTodayCalorie()   // workoutDB에 담겨있는 날짜정보로 calorie 가져옴
        val commentData =
            commentData(prevCount, nowCount, todayCalories) // 이전count, 현재count, 총calorie 정보 객체

        commentData.let {   // 정보객체 활용코드 부분 (it->prevCount, newCount, todayCalorie)
            val lowLimitCal = if (userWeight != null) userWeight * 22 else 1000
            val highLimitCal = if (userWeight != null) userWeight * 40 else 2700
//            Log.d("cal low", lowLimitCal.toString())
//            Log.d("cal high", highLimitCal.toString())

            if (it.todayCalorie != null) {
                if (it.todayCalorie > highLimitCal) {
                    commentList.add(getString(R.string.wo_comment_cal_high))
                } else if (it.todayCalorie < lowLimitCal && nowCount > 10) {
                    commentList.add(getString(R.string.wo_comment_cal_low))
                } else if (it.todayCalorie < lowLimitCal && nowCount < 7) {
                    commentList.add(getString(R.string.wo_comment_cal_count_low))
                }
            }

            if (workout.count != null) {
                if (workout.count!! < 5) {
                    commentList.add(getString(R.string.wo_comment_count_low))
                }
            }

            if (workout.woDuration != null && workout.restTime != null) {
                if (workout.woDuration!! < workout.restTime!!) {
                    commentList.add(getString(R.string.wo_comment_rest_high))
                }
            }

            if (commentList.size == 0) {
                commentList.add(getString(R.string.wo_result_basic))    // 기본 문자열
            }
        }
    }
    private fun roundToNearestTen(input: Float): Int {
        val intInput = input.toInt()
        Log.d("최승호","$intInput")
        val remainder = intInput % 10
        Log.d("최승호","$remainder, ${intInput + (10 - remainder)}")
        return intInput + (10 - remainder)
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    override fun onBackPressed(){
        if (workout.id != null) {
            startActivity(Intent(this, WOHistoryActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            intent.putExtra("VIEW_PAGER_INDEX",0)
        }

        finish()
    }
}
