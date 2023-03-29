package kr.rabbito.homefit.screens

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
//import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
//import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
//import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
//import com.github.aachartmodel.aainfographics.aachartcreator.aa_toAAOptions
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
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
}