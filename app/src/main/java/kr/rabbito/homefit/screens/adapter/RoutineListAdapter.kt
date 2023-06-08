package kr.rabbito.homefit.screens.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.rabbito.homefit.data.Routine
import kr.rabbito.homefit.databinding.RoutinelistItemBinding
import kr.rabbito.homefit.screens.viewHolder.RoutineListViewHolder

class RoutineListAdapter(private val routine: List<Routine>, private val Screen_Type: String, private val workoutIndex: Int)
    : RecyclerView.Adapter<RoutineListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineListViewHolder {
        val binding = RoutinelistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoutineListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoutineListViewHolder, position: Int) {
        val routine = routine[position]
        holder.bind(routine, Screen_Type, workoutIndex)
    }

    override fun getItemCount() = routine.size



}
