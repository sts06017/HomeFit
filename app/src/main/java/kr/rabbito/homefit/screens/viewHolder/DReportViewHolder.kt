package kr.rabbito.homefit.screens.viewHolder

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kr.rabbito.homefit.client.*
import kr.rabbito.homefit.databinding.DreportItemBinding

class DReportViewHolder(private val binding: DreportItemBinding)
    : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
    fun bind(key: String, results: Map<String, Any>) {
        val detailInfo = results[key] as Map<*, *>

        val foodName = FOOD_NAMES_KR[FOOD_CLASSES.indexOf(key)]

        val calorie = detailInfo[CALORIE].toString().toDouble().toInt()
        val weight = detailInfo[WEIGHT].toString().toDouble().toInt()

        binding.dreportItemTvResultTitle.text = foodName
        binding.dreportItemTvResultCalorie.text = "${calorie}kcal"
        binding.dreportItemTvResultTime.text = "${weight}g"

    }

    override fun onClick(v: View?) {
        Log.d("diet","dList onCLick")
    }
}