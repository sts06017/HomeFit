package kr.rabbito.homefit.screens

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.rabbito.homefit.R
import kr.rabbito.homefit.data.Diet
import kr.rabbito.homefit.data.DietDB
import kr.rabbito.homefit.databinding.ActivityDreportBinding
import kr.rabbito.homefit.screens.adapter.DReportAdapter
import kr.rabbito.homefit.screens.navigatorBar.DietCalorie
import kr.rabbito.homefit.screens.navigatorBar.DietMap
import kr.rabbito.homefit.utils.calc.Converter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DReportActivity : AppCompatActivity() {
    private var mBinding: ActivityDreportBinding? = null
    private val binding get() = mBinding!!
    private var dietDB: DietDB? = null

    private var date: LocalDate? = null
    private var dateString: String? = null
    private val layoutManager = LinearLayoutManager(this)

    private val commentList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityDreportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dateString = intent.getStringExtra("DATE")

        if (dateString != null) {
            date = LocalDate.parse(dateString)
        }

        binding.dreportBtnBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        initView()
        initPieChart()
        initLineChart()
    }

    private fun initView() {
        CoroutineScope(Dispatchers.IO).launch {
            dietDB = DietDB.getInstance(this@DReportActivity)
            val todayDiets = dietDB!!.DietDAO().getDietByDate(date ?: LocalDate.now())

            var formattedDate: String? = null
            if (dateString != null) {
                val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val outputFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일")
                val date = LocalDate.parse(dateString, inputFormatter)
                formattedDate = date.format(outputFormatter)
            }

            todayDiets?.let {
                Log.d("DReport", "todayDiets: $it")
                withContext(Dispatchers.Main) {
                    binding.dreportTvDate.text =
                        formattedDate?.format(Converter.dateFormatter_ko) ?: LocalDate.now()
                            .format(Converter.dateFormatter_ko)
                    binding.dreportRvFoods.layoutManager = layoutManager
                    binding.dreportRvFoods.adapter = DReportAdapter(it)
                }
            }
        }
    }

    private suspend fun getTodayNutrients(): List<Diet>? {
        return dietDB?.DietDAO()?.getDietByDate(date ?: LocalDate.now())
    }
    // 해당 날짜 영양소 합

    private suspend fun getTodayCalories(): List<DietCalorie>? {
        return dietDB?.DietDAO()?.getCaloriesByDate(dateString ?: LocalDate.now().toString())
    }
    // 해당 날짜 칼로리 합

    private suspend fun getNowCalories(): Float {
        val dietDAO = dietDB?.DietDAO()
        val todayCalories = dietDAO?.getTodayCalories(dateString ?: LocalDate.now().toString())
        return todayCalories ?: 0.0f
    }

    data class NutrientData(
        val nutrientMap: MutableMap<String, Float>,
        val carbohydrate: Float,
        val protein: Float,
        val fat: Float
    )

    private fun saveToNutrientMap(diets: List<Diet>?): NutrientData {
        val nutrientMap = mutableMapOf("탄수화물" to 0.0f, "단백질" to 0.0f, "지방" to 0.0f)
        var carbohydrate: Float = 0.0f
        var protein: Float = 0.0f
        var fat: Float = 0.0f

        diets?.forEach {
            nutrientMap["탄수화물"] = nutrientMap["탄수화물"]!! + it.carbohydrate!!.toFloat()
            nutrientMap["단백질"] = nutrientMap["단백질"]!! + it.protein!!.toFloat()
            nutrientMap["지방"] = nutrientMap["지방"]!! + it.fat!!.toFloat()
            carbohydrate = it.carbohydrate!!.toFloat()
            protein = it.protein!!.toFloat()
            fat = it.fat!!.toFloat()
        }
        Log.d("nutrient", carbohydrate.toString())
        return NutrientData(nutrientMap, carbohydrate, protein, fat)
    }

    private fun saveToCalorieMap(calorie: List<DietCalorie>): MutableMap<String, Float> {
        val calorieMap = mutableMapOf<String, Float>()

        calorie.forEach {
            calorieMap[it.dDate.toString()] = it.totalCalorie.toFloat()
        }
        return calorieMap
    }

    private fun initPieChart() {
        CoroutineScope(Dispatchers.Main).launch {
            val todayDiets = getTodayNutrients()
            val (nutrientMap, carbohydrate, protein, fat) = saveToNutrientMap(todayDiets)

            if (nutrientMap.toString() != "{탄수화물=0.0, 단백질=0.0, 지방=0.0}") {

                createPieGraph(nutrientMap, binding.dreportVGraph1)
                binding.dreportVGraph1.visibility = View.VISIBLE
                binding.dreportVGraph2.visibility = View.VISIBLE

                val params = binding.dreportTvComment.layoutParams as ViewGroup.MarginLayoutParams
                params.topMargin = Converter.dpToPx(resources, 200)
                binding.dreportTvComment.layoutParams = params

                setNutrientComment(carbohydrate, protein, fat)
                binding.dreportTvComment.text = commentList.takeLast(2)
                    .joinToString("\n") // 결과가 너무 길게 나오지 않도록, 최대 2문장만 출력되도록 설정
            } else {
                commentList.clear()
                commentList.add("입력된 정보가 없습니다.\n우측 하단의 버튼을 터치해 새로운 식단 정보를 추가해보세요.")
                binding.dreportTvComment.text = commentList.takeLast(2).joinToString("\n")

                binding.dreportVGraph1.visibility = View.GONE
                binding.dreportVGraph2.visibility = View.GONE

                val params = binding.dreportTvComment.layoutParams as ViewGroup.MarginLayoutParams
                params.topMargin = Converter.dpToPx(resources, 52)
                binding.dreportTvComment.layoutParams = params
            }

            if (commentList.size == 0) {
                Log.d("comment", "check")
                commentList.clear()
                commentList.add(getString(R.string.dreport_comment_basic))   // 기본 코멘트
                binding.dreportTvComment.text = commentList.takeLast(2)
                    .joinToString("\n")
            }
        }
    }

    private suspend fun setNutrientComment(carbohydrate: Float, protein: Float, fat: Float) {
        commentList.clear()

        val carbohydrateCalories = 4 * carbohydrate
        val proteinCalories = 4 * protein
        val fatCalories = 9 * fat

        // 탄수화물, 단백질, 지방의 비율 계산
        val totalCalories = carbohydrateCalories + proteinCalories + fatCalories
        val carbRatio = carbohydrateCalories / totalCalories
        val proteinRatio = proteinCalories / totalCalories
        val fatRatio = fatCalories / totalCalories

        // 비율에 따른 조언 작성
        if (carbRatio > 0.7) {
            commentList.add(getString(R.string.dreport_comment_carb_high))
        } else if (carbRatio < 0.4) {
            commentList.add(getString(R.string.dreport_comment_carb_low))
        }

        if (proteinRatio > 0.4) {
            commentList.add(getString(R.string.dreport_comment_protein_high))
        } else if (proteinRatio < 0.05) {
            commentList.add(getString(R.string.dreport_comment_protein_low))
        }

        if (fatRatio > 0.4) {
            commentList.add(getString(R.string.dreport_comment_fat_high))
        } else if (fatRatio < 0.15) {
            commentList.add(getString(R.string.dreport_comment_fat_low))
        }


        if (getNowCalories() > 3000) {
            commentList.add("칼로리 섭취량이 너무 많습니다. 더 나은 균형을 위해 칼로리 섭취량을 줄이는 것이 좋습니다. ")
        } else if (getNowCalories() < 2000 && getNowCalories() > 0.0) {
            commentList.add("칼로리 섭취량이 너무 적습니다. 충분한 에너지를 위해 충분한 칼로리를 섭취하고 있는지 확인하세요. ")
        }

    }

    private fun initLineChart() {
        CoroutineScope(Dispatchers.Main).launch {
            val totalCalorie = getTodayCalories()
            val calorieMap = saveToCalorieMap(totalCalorie!!)

            var calorie: Float? = 0.0f
            calorie = getNowCalories()

            val comment = calorieComment(calorie!!)


            if (calorieMap.isNotEmpty()) {
                createLineChart(calorieMap, binding.dreportVGraph2)
//                binding.dreportVGraph2.visibility = View.VISIBLE
            } else {
//                binding.dreportVGraph2.visibility  = View.GONE
            }
        }
    }

    private fun calorieComment(calorie: Float): String? {
        var comment: String? = ""
        Log.d("calorie", calorie.toString())
        if (calorie > 3000) {
            comment = "칼로리 섭취량이 너무 많습니다. 더 나은 균형을 위해 칼로리 섭취량을 줄이는 것이 좋습니다. "
        } else if (calorie < 2000 && calorie > 0.0) {
            comment = "칼로리 섭취량이 너무 적습니다. 충분한 에너지를 위해 충분한 칼로리를 섭취하고 있는지 확인하세요. "
        }
        return comment
    }

    private fun createPieGraph(data: Map<String, Float>, chart: PieChart) {
        //------입력 데이터 pieEntry로 변환------
        val testEntries = ArrayList<PieEntry>()
        for (i in data.entries) {
            testEntries.add(PieEntry(i.value, i.key))
        }

        //------chart dataset 설정------
        val pieDataSet = PieDataSet(testEntries, "")
        pieDataSet.apply {
            valueTextColor = Color.WHITE
            valueTextSize = 9f
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
            legend.textColor = Color.WHITE
            holeRadius = 80f
            transparentCircleRadius = 0f
            legend.form = Legend.LegendForm.CIRCLE
        }
    }

    // line chart -> 데이터 최신화 및 변동 데이터 입력 처리 필요
    private fun createLineChart(yValues: MutableMap<String, Float>, chart: LineChart) {
        val entries = ArrayList<Entry>()
        var y = 10
        for (i in yValues) {
            entries.add(Entry(y.toFloat(), i.value))
            y += 10
        }

        val xValues = ArrayList<String>()
        for (i in yValues) {
            xValues.add(i.key)
        }

        val yValues = ArrayList<Entry>()
        yValues.add(Entry(10f, 2300F))
        yValues.add(Entry(20f, 2210F))
        yValues.add(Entry(30f, 2230F))
        yValues.add(Entry(40f, 2320F))
        yValues.add(Entry(50f, 2480F))
        yValues.add(Entry(60f, 2340F))

        chart.xAxis.valueFormatter = IndexAxisValueFormatter(xValues)
        chart.xAxis.position = XAxis.XAxisPosition.TOP
        chart.xAxis.granularity = 5f

        val maxCalorie = entries.maxByOrNull { it.y }
        val leftAxis = chart.axisLeft
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = roundToNearestHundred(maxCalorie?.y ?: 5000f).toFloat()
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
            axisLeft.setLabelCount(6, true)
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
    private fun roundToNearestHundred(input: Float): Int {
        val intInput = input.toInt()
        val remainder = intInput % 100
        return intInput + (100 - remainder)
    }
}