package kr.rabbito.homefit.screens.viewHolder

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kr.rabbito.homefit.client.*
import kr.rabbito.homefit.data.Diet
import kr.rabbito.homefit.databinding.DreportItemBinding
import kr.rabbito.homefit.screens.DAddActivity
import kr.rabbito.homefit.screens.MainActivity

class DReportViewHolder(private val binding: DreportItemBinding) :
    RecyclerView.ViewHolder(binding.root), View.OnClickListener {
    //    fun bind(key: String, results: Map<String, Any>) {
    private var diet: Diet? = null
    private var fromHistory = false

    init {
        itemView.setOnClickListener(this)
    }

    fun bind(results: Diet, fromHistory: Boolean) {
        this.diet = results
        this.fromHistory = fromHistory

        binding.dreportItemTvResultTitle.text =
            FOOD_NAMES_KR[FOOD_CLASSES.indexOf(results.foodName)]
        binding.dreportItemTvResultCalorie.text = "${results.calorie?.toInt().toString()}kcal"
        binding.dreportItemTvResultTime.text = results.dTime
    }

    override fun onClick(v: View?) {
        Log.d("diet", "dReportList onCLick")
        if (diet != null) {
            val intent = Intent(itemView.context, DAddActivity::class.java).apply {
                putExtra("DIET", diet)
                putExtra("DATE", diet!!.dDate.toString())
                putExtra("fromHistory", fromHistory)
            }

            (itemView.context as Activity).finish()
            itemView.context.startActivity(intent)
        }
    }
}