package kr.rabbito.homefit.screens.viewHolder

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kr.rabbito.homefit.client.*
import kr.rabbito.homefit.data.Diet
import kr.rabbito.homefit.databinding.DreportItemBinding
import kr.rabbito.homefit.screens.DReportActivity
import kr.rabbito.homefit.screens.MainActivity
import java.time.LocalDate

class DHistoryViewHolder(private val binding: DreportItemBinding)
    : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    private var diet : Diet? = null

    init {
        itemView.setOnClickListener(this)
    }

    fun bind(results: Diet){
        this.diet = results
        binding.dreportItemTvResultTitle.text = FOOD_NAMES_KR[FOOD_CLASSES.indexOf(results.foodName)]
        binding.dreportItemTvResultCalorie.text = "${results.calorie?.toInt().toString()}kcal"
        binding.dreportItemTvResultTime.text = results.dTime


    }

    override fun onClick(v: View?) {
        Log.d("diet","dHistoryList onCLick")
        if(diet != null){
            val intent = Intent(itemView.context, DReportActivity::class.java)
            intent.putExtra("DATE", diet!!.dDate.toString())

            itemView.context.startActivity(intent)
            (itemView.context as Activity).finish()
        }
    }
}
