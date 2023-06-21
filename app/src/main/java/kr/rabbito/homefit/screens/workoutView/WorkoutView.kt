package kr.rabbito.homefit.screens.workoutView

import android.content.Context
import kr.rabbito.homefit.databinding.ActivityWoBinding
import kr.rabbito.homefit.workout.WorkoutState

abstract class WorkoutView(val context: Context, val binding: ActivityWoBinding) {
    open fun generateWidgets() {}
    fun refreshValues() {
        binding.woTvCount.text = "${WorkoutState.count}/${WorkoutState.setCondition}"
        binding.woTvSet.text = "${WorkoutState.set}/${WorkoutState.setTotal}"
    }
}