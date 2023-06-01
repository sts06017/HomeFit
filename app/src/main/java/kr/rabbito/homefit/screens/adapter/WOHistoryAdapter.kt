package kr.rabbito.homefit.screens.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.rabbito.homefit.data.Workout
import kr.rabbito.homefit.databinding.WohistoryItemBinding
import kr.rabbito.homefit.screens.viewHolder.WOHistoryViewHolder

class WOHistoryAdapter(private val workouts: List<Workout>)
    : RecyclerView.Adapter<WOHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WOHistoryViewHolder {
        val binding = WohistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WOHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WOHistoryViewHolder, position: Int) {
        val workout = workouts[position]
        holder.bind(workout)
    }

    override fun getItemCount() = workouts.size



}
