package kr.rabbito.homefit.screens.navigatorBar

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.rabbito.homefit.R
import kr.rabbito.homefit.client.*
import kr.rabbito.homefit.data.Diet
import kr.rabbito.homefit.data.DietDB
import kr.rabbito.homefit.data.WorkoutDB
import kr.rabbito.homefit.databinding.FragmentDreportBinding
import kr.rabbito.homefit.screens.DAddActivity
import kr.rabbito.homefit.screens.DAddTypeSelectActivity
import kr.rabbito.homefit.screens.DHistoryActivity
import kr.rabbito.homefit.screens.adapter.DReportAdapter
import kr.rabbito.homefit.utils.calc.Converter
import kr.rabbito.homefit.utils.calc.Converter.Companion.timeFormatter
import java.time.LocalDate
import java.time.LocalDateTime

// 기존의 DReportActivity.kt 파일
data class DietInfo(@SerializedName("volume(cm^3)") val volume: Double, @SerializedName("weight(g)") val weight: Double, @SerializedName("calorie(kcal)") val calorie: Double, @SerializedName("fat(g)") val fat: Double, @SerializedName("carbohydrate(g)") val carbohydrate: Double, @SerializedName("protein(g)") val protein: Double)
data class Nutrient(val carbohydrate: Double?, val protein: Double?, val fat: Double?)
data class DietCalorie(val dDate: LocalDate, val totalCalorie: Double)
typealias DietMap = Map<String, DietInfo>
class DReportFragment : Fragment() {
    private var mBinding: FragmentDreportBinding? = null
    private val binding get() = mBinding!!
    private var dietDB: DietDB? = null

    private var dietMap: DietMap? = null
    private var resultJson: String? = null
    private var date: LocalDate? = null
    private var dateString: String? = null
    private val layoutManager = LinearLayoutManager(this.context)

    private var nutrinetAdvice: String = ""
    private var calorieAdvice: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resultJson = arguments?.getString("RESULT_JSON")
        dateString = arguments?.getString("DATE")
        val timeString = arguments?.getString("TIME")

        if(dateString != null) {
            date = LocalDate.parse(dateString)
        }
//        var resultDiet = arguments?.getParcelable<Diet>("RESULT_DIET")
//        Log.d("최승호","$resultDiet")
        if (resultJson != null) {
            Log.d("jsonFile", resultJson!!)

            val resultMap = parseJSONString(resultJson)
            val gson = Gson()
            dietMap = gson.fromJson(resultJson, object : TypeToken<DietMap>() {}.type)
            dietDB = DietDB.getInstance(requireContext())
            insertDiet(dietMap, timeString)
            Log.d("jsonFileKeys", resultMap.keys.toString())

        }

    }

    // activity와 다르게 onCreateView에 코드 작성
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        mBinding = FragmentDreportBinding.inflate(inflater, container, false)


        val dreportVGraph1 = binding.dreportVGraph1
        val dreportVGraph2 = binding.dreportVGraph2

        binding.dreportBtnAdd.setOnClickListener {
            startActivity(Intent(activity, DAddTypeSelectActivity::class.java))
        }

        binding.dreportBtnHistory.setOnClickListener {
            startActivity(Intent(activity, DHistoryActivity::class.java))
        }
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    override fun onResume() {
        super.onResume()
        initView()
        initPieChart()
        initLineChart()
    }
    private fun initView(){
        CoroutineScope(Dispatchers.IO).launch {
            dietDB = DietDB.getInstance(requireContext())
            val todayDiets = dietDB!!.DietDAO().getDietByDate(date?: LocalDate.now())

            todayDiets?.let{
                Log.d("DReport","todayDiets: $it")
                withContext(Dispatchers.Main){
                    binding.dreportTvDate.text = date?.format(Converter.dateFormatter_ko)?: LocalDate.now().format(Converter.dateFormatter_ko)
                    binding.dreportRvFoods.layoutManager = layoutManager
                    binding.dreportRvFoods.adapter = DReportAdapter(it)
                }
            }
        }
    }

    private suspend fun getTodayNutrients(): List<Diet>? { return dietDB?.DietDAO()?.getDietByDate(date?: LocalDate.now()) }
    // 해당 날짜 영양소 합

    private suspend fun getTodayCalories(): List<DietCalorie>? { return dietDB?.DietDAO()?.getCaloriesByDate(dateString?: LocalDate.now().toString()) }
    // 해당 날짜 칼로리 합
    data class NutrientData(val nutrientMap: MutableMap<String, Float>, val carbohydrate: Float, val protein: Float, val fat: Float)

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
        return NutrientData(nutrientMap, carbohydrate, protein, fat)
    }

    data class CalorieData(val calorieMap: MutableMap<String, Float>, val totalCalorie: Float)

    private fun saveToCalorieMap(calorie: List<DietCalorie>): CalorieData {
        val calorieMap = mutableMapOf<String,Float>()
        var totalCalorie: Float = 0.0f

        calorie.forEach {
            calorieMap[it.dDate.toString()] = it.totalCalorie.toFloat()
            totalCalorie = it.totalCalorie.toFloat()
        }
        return CalorieData(calorieMap, totalCalorie)
    }
    private fun initPieChart() {
        CoroutineScope(Dispatchers.Main).launch {
            val todayDiets = getTodayNutrients()
            val (nutrientMap, carbohydrate, protein, fat) = saveToNutrientMap(todayDiets)

            val comment = nutrinetComment(carbohydrate, protein, fat)
            Log.e("nutrient", nutrientMap.toString())
            if (nutrientMap.toString() != "{탄수화물=0.0, 단백질=0.0, 지방=0.0}") {
                createPieGraph(nutrientMap, binding.dreportVGraph1)
                binding.dreportVGraph1.visibility = View.VISIBLE
                Log.e("nutrient", comment)
                editDreportComment(comment, "nutrient")
            } else {
                editDreportComment("입력된 정보가 없습니다.", "nutrient")
                binding.dreportVGraph1.visibility = View.GONE
            }
        }
    }

    private fun nutrinetComment(carbohydrate: Float, protein: Float, fat: Float): String {
        var comment: String? = null
        var carbohydrateCaloaries = 4*carbohydrate
        var proteinCalories = 4*protein
        var fatCalories = 9*fat

        // 탄수화물, 단백질, 지방의 비율 계산
        val totalCalories = carbohydrateCaloaries + proteinCalories + fatCalories
        val carbRatio = carbohydrateCaloaries / totalCalories
        val proteinRatio = proteinCalories / totalCalories
        val fatRatio = fatCalories / totalCalories

        // 비율에 따른 조언 작성
        if (carbRatio > 0.7) {
            comment += "탄수화물 비율이 너무 높습니다. 적절한 탄수화물 섭취를 고려해보세요. "
        } else if (carbRatio < 0.4) {
            comment += "탄수화물 비율이 낮습니다. 충분한 에너지 공급을 위해 탄수화물 섭취를 늘려보세요. "
        }

        if (proteinRatio > 0.4) {
            comment += "단백질 비율이 높습니다. 적절한 단백질 섭취를 유지하세요. "
        } else if (proteinRatio < 0.05) {
            comment += "단백질 비율이 낮습니다. 근육 유지 및 회복에 필요한 단백질 섭취를 늘려보세요. "
        }

        if (fatRatio > 0.4) {
            comment += "지방 비율이 높습니다. 적절한 지방 섭취를 유지하세요. "
        } else if (fatRatio < 0.15) {
            comment += "지방 비율이 낮습니다. 비타민 흡수와 필수 기능에 필요한 지방 섭취를 늘려보세요. "
        }

        if(comment == null){
            comment = "적절한 식습관을 유지하고 있습니다."
        }

        return comment
    }

    private fun initLineChart() {
        CoroutineScope(Dispatchers.Main).launch {
            val totalCalorie = getTodayCalories()
            val (calorieMap, calorie) = saveToCalorieMap(totalCalorie!!)

//            val comment = calorieCommnet(calorie)

//            Log.e("calorie", totalCalorie.toString())
            if (calorieMap.isNotEmpty()) {
                Log.e("calorie", calorie.toString())
                createLineChart(calorieMap, binding.dreportVGraph2)
                binding.dreportVGraph2.visibility = View.VISIBLE
//                if (comment != null) {
//                    editDreportComment(comment, "calorie")
//                }
            } else {
                binding.dreportVGraph2.visibility  = View.GONE
            }
        }
    }

//    private fun calorieCommnet(calorie: Float): String? {
//        var comment: String? = null
//
//        if(calorie > 3000){
//            comment = "칼로리 섭취량이 너무 많습니다. 더 나은 균형을 위해 칼로리 섭취량을 줄이는 것이 좋습니다. "
//        }else if(calorie < 2000 && calorie > 0){
//            comment = "칼로리 섭취량이 너무 적습니다. 충분한 에너지를 위해 충분한 칼로리를 섭취하고 있는지 확인하세요. "
//        }
//        return comment
//    }

    private fun editDreportComment(comment: String, from: String) {
        if (from == "nutrient") {
            nutrinetAdvice = comment
        } else if (from == "calorie") {
            calorieAdvice = comment
        }

        val totalComment = "$nutrinetAdvice\n$calorieAdvice"
        binding.dreportTvComment.text = totalComment
    }


    private fun insertDiet(dietMap: DietMap?, timeString: String?) {
        val currentDate = LocalDate.now()
        val currentTime = LocalDateTime.now().format(timeFormatter)
        val dietMapStirng = dietMap.toString() + timeString
        val dietMapHash = dietMapStirng.hashCode()

        CoroutineScope(Dispatchers.IO).launch {
            val existingDiet = dietDB!!.DietDAO().findByJsonHash(dietMapHash)
            if (existingDiet == null){
                dietMap?.forEach {(foodName, foodInfo) ->
                    val diet = Diet(
                        id = null, foodName = foodName, weight = foodInfo.volume, calorie = foodInfo.calorie, carbohydrate = foodInfo.carbohydrate, protein = foodInfo.protein, fat = foodInfo.fat, dDate = currentDate, dTime = currentTime, jsonHash = dietMapHash//, json = resultJson
                    )
                    dietDB?.DietDAO()?.insert(diet)
                }
            }
            else{
                Log.d("DReportFragment","같은 해쉬코드의 데이터 저장되어있음\n원인1: dietMap: $dietMap\n원인2:\n이미존재한 객체->${existingDiet.jsonHash}\n새로생성한 해쉬->$dietMapHash")
                Log.d("DReportFragment","이미존재한 음식이름: ${existingDiet.foodName}, w: ${existingDiet.weight}, cal: ${existingDiet.calorie}, fat: ${existingDiet.fat}, car: ${existingDiet.carbohydrate}, pro: ${existingDiet.protein}")
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
        for(c in ColorTemplate.COLORFUL_COLORS) colorsItems.add(c)
        for(c in ColorTemplate.LIBERTY_COLORS) colorsItems.add(c)
        for(c in ColorTemplate.PASTEL_COLORS) colorsItems.add(c)

        //------chart dataset 설정------
        val pieDataSet = PieDataSet(testEntries,"")
        pieDataSet.apply {
            colors = colorsItems
            valueTextColor = Color.WHITE
            valueTextSize = 9f
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
            legend.textColor = Color.WHITE
            holeRadius = 80f
            transparentCircleRadius = 0f
        }
    }

    // line chart -> 데이터 최신화 및 변동 데이터 입력 처리 필요
    private fun createLineChart( yValues : MutableMap<String,Float>, chart: LineChart){
        val entries = ArrayList<Entry>()
        var y = 10
        for(i in yValues){
            entries.add(Entry(y.toFloat(),i.value))
            y += 10
        }

        val xValues = ArrayList<String>()
        for (i in yValues){
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

        val leftAxis = chart.axisLeft
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 2500f
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
            axisLeft.setLabelCount(6,true)
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
}