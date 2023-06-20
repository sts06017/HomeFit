package kr.rabbito.homefit.screens.viewHolder

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kr.rabbito.homefit.data.Routine
import kr.rabbito.homefit.databinding.RoutinelistItemBinding
import kr.rabbito.homefit.screens.RoutineActivity
import kr.rabbito.homefit.screens.WODetailActivity
import java.time.format.DateTimeFormatter

class RoutineListViewHolder(private val binding: RoutinelistItemBinding)
    : RecyclerView.ViewHolder(binding.root), View.OnClickListener{

    private var routine: Routine? = null

    init {
        itemView.setOnClickListener(this)
    }
    fun bind(routine: Routine, Screen_Type: String, workoutIndex: Int){
        this.routine = routine

        val setNumber = "${routine.set} 세트"

        binding.routinelistItemTvSetname.text = routine.setName // 글자 수 일정 이상일 경우 사이즈 감소 구현, 세트 이름
        binding.routinelistItemTvWorkname.text = routine.workoutName // 운동 종류
        binding.routinelistItemTvSetnumber.text = setNumber // 세트 횟수

        if (Screen_Type == "from_WODetailActivity") { // 세트 불러오기
            binding.routinelistItemIvTempItemBackground.setOnClickListener { // 리스트 각 항목 선택
                val intent = Intent(itemView.context, WODetailActivity::class.java).apply {
                    putExtra("routine", routine) // 넘겨줄 routine 객체
                    putExtra("workoutIndex", workoutIndex)
                }
                itemView.context.startActivity(intent)
                (itemView.context as Activity).finish()
            }
        } else { // 세트 수정
            binding.routinelistItemIvTempItemBackground.setOnClickListener{
                val intent = Intent(itemView.context, RoutineActivity::class.java).apply {
                    putExtra("routine", routine) // DB routine 객체
                }
                itemView.context.startActivity(intent)
                (itemView.context as Activity).finish()
            }
        }
    }

    override fun onClick(v: View?) { // binding을 통해 setOnClickListener 실행, 현재 코드 실행 X
//        if (this.routine != null) {
//            val intent = Intent(itemView.context, RoutineActivity::class.java).apply {
//                putExtra("routine", routine) // DB routine 객체
//            }
//            itemView.context.startActivity(intent)
//        }
    }
}
