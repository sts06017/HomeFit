package kr.rabbito.homefit.screens

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import kr.rabbito.homefit.databinding.ActivityWoreportBinding

class WOReportActivity : AppCompatActivity() {
    private var mBinding: ActivityWoreportBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWoreportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val woreportVGraph1 = binding.woreportVGraph1
        val woreportVGraph2 = binding.woreportVGraph2

        val pieDateTest = mutableMapOf<String,Float>()
        pieDateTest["운동"]=100f
        pieDateTest["휴식"]=20f
        pieDateTest["정지"]=10f

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

        createLineChart(xLineData as ArrayList<String>, yLineData as ArrayList<Int>, woreportVGraph2)
        binding.woreportBtnHistory.setOnClickListener {
            startActivity(Intent(this, WOHistoryActivity::class.java))
        }
        binding.woreportBtnSaveReport.setOnClickListener {
            startActivity(Intent(this,WOHistoryActivity::class.java))
        }
        binding.woreportBtnDeleteReport.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
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
}