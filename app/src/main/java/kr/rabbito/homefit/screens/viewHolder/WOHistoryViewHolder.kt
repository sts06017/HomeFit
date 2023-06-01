package kr.rabbito.homefit.screens.viewHolder

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import kr.rabbito.homefit.data.Workout
import kr.rabbito.homefit.databinding.WohistoryItemBinding
import kr.rabbito.homefit.screens.WOReportActivity
import kr.rabbito.homefit.screens.adapter.WOHistoryAdapter
import java.time.format.DateTimeFormatter
import java.util.Locale

class WOHistoryViewHolder(private val binding: WohistoryItemBinding)
    : RecyclerView.ViewHolder(binding.root), View.OnClickListener{

    private var workout: Workout? = null
    init {
        itemView.setOnClickListener(this)
    }
    fun bind(workout: Workout){
        this.workout = workout

        val dateFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 dd일")
        val workoutDate = workout.date?.format(dateFormatter)
        val timeFormatter = DateTimeFormatter.ofPattern("a h시 mm분").withLocale(Locale.forLanguageTag("ko"))
        val workoutTime = workout.time?.format(timeFormatter)

        binding.wohistoryItemTvWorkname.text = workout.workoutName // 글자 수 일정 이상일 경우 사이즈 감소 구현
        binding.wohistoryItemTvDate.text = workoutDate
        binding.wohistoryItemTvTime.text = workoutTime
    }

    override fun onClick(v: View?) {
        if (this.workout != null) {
            val intent = Intent(itemView.context, WOReportActivity::class.java).apply {
                putExtra("workout", workout) // DB workout 객체
            }
            itemView.context.startActivity(intent)
        }
    }
}
