package kr.rabbito.homefit.screens.viewHolder

import androidx.recyclerview.widget.RecyclerView
import kr.rabbito.homefit.data.Workout
import kr.rabbito.homefit.databinding.WohistoryItemBinding
import java.time.format.DateTimeFormatter
import java.util.Locale

class WOHistoryViewHolder(private val binding: WohistoryItemBinding)
    : RecyclerView.ViewHolder(binding.root){
    fun bind(workout: Workout){

        val dateFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 dd일")
        val workoutDate = workout.date?.format(dateFormatter)
        val timeFormatter = DateTimeFormatter.ofPattern("a h시 mm분").withLocale(Locale.forLanguageTag("ko"))
        val workoutTime = workout.time?.format(timeFormatter)

        binding.wohistoryItemTvWorkname.text = workout.workoutName
        binding.wohistoryItemTvDate.text = workoutDate
        binding.wohistoryItemTvTime.text = workoutTime
    }
}
