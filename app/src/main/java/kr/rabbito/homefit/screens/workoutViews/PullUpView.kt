package kr.rabbito.homefit.screens.workoutViews

import android.content.Context
import android.widget.ImageView
import kr.rabbito.homefit.R
import kr.rabbito.homefit.databinding.ActivityWoBinding
import kr.rabbito.homefit.workout.WorkoutState

class PullUpView(context: Context, binding: ActivityWoBinding): WorkoutView(context, binding) {
    override fun generateWidgets() {
//        val target = ImageView(context)
//        target.setImageResource(R.drawable.loading_iv_icon)
//
//        binding.woClRoot.addView(target)
    }

    override fun refreshValues() {
        binding.woTvCount.text = WorkoutState.count.toString()
    }
}