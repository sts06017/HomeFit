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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resultJson = arguments?.getString("RESULT_JSON")
        dateString = arguments?.getString("DATE")
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
            insertDiet(dietMap)
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


        val xLineData = mutableListOf<String>()
        xLineData.add("Jan")
        xLineData.add("Feb")
        xLineData.add("Mar")
        xLineData.add("Apr")
        xLineData.add("May")
        xLineData.add("Jun")

        val yLineData = mutableListOf<Int>()

//        createLineChart(xLineData as ArrayList<String>, yLineData as ArrayList<Int>, dreportVGraph2)

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
    private suspend fun getTodayCalories(): List<DietCalorie>? { return dietDB?.DietDAO()?.getCaloriesByDate(dateString?: LocalDate.now().toString()) }
    private fun saveToNutrientMap(diets: List<Diet>?): MutableMap<String,Float>{
        val nutrientMap = mutableMapOf("탄수화물" to 0.0f, "단백질" to 0.0f, "지방" to 0.0f)
        diets?.forEach {
            nutrientMap["탄수화물"] = nutrientMap["탄수화물"]!! + it.carbohydrate!!.toFloat()
            nutrientMap["단백질"] = nutrientMap["단백질"]!! + it.protein!!.toFloat()
            nutrientMap["지방"] = nutrientMap["지방"]!! + it.fat!!.toFloat()
        }
        return nutrientMap
    }
    private fun saveToCalorieMap(calorie: List<DietCalorie>): MutableMap<String,Float>{
        val calorieMap = mutableMapOf<String,Float>()
        calorie.forEach {
            calorieMap[it.dDate.toString()] = it.totalCalorie.toFloat()
        }
        return calorieMap
    }
    private fun initPieChart(){
        CoroutineScope(Dispatchers.Main).launch {
            val todayDiets = getTodayNutrients()
            val nutrientMap = saveToNutrientMap(todayDiets)
            createPieGraph(nutrientMap,binding.dreportVGraph1)
        }
    }
    private fun initLineChart(){
        CoroutineScope(Dispatchers.Main).launch{
            val totalCalorie = getTodayCalories()
            Log.d("DReport","totalCalorie: $totalCalorie")
            val calorieMap = saveToCalorieMap(totalCalorie!!)
            createLineChart(calorieMap,binding.dreportVGraph2)
        }
    }
    private fun insertDiet(dietMap: DietMap?) {
        val currentDate = LocalDate.now()
        val currentTime = LocalDateTime.now().format(timeFormatter)
        val jsonHash = dietMap.hashCode()

        CoroutineScope(Dispatchers.IO).launch {
            val existingDiet = dietDB!!.DietDAO().findByJsonHash(jsonHash)
            if (existingDiet == null){
                dietMap?.forEach {(foodName, foodInfo) ->
                    val diet = Diet(
                        id = null, foodName = foodName, weight = foodInfo.volume, calorie = foodInfo.calorie, carbohydrate = foodInfo.carbohydrate, protein = foodInfo.protein, fat = foodInfo.fat, dDate = currentDate, dTime = currentTime, jsonHash = jsonHash//, json = resultJson
                    )
                    dietDB?.DietDAO()?.insert(diet)
                }
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