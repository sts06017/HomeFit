package kr.rabbito.homefit.screens.workoutViews

import android.content.Context
import android.util.Log
import android.widget.ImageView
import kr.rabbito.homefit.R
import kr.rabbito.homefit.databinding.ActivityWoBinding
import kr.rabbito.homefit.workout.WorkoutState

class PushUpView(context: Context, binding: ActivityWoBinding): WorkoutView(context, binding) {
    override fun generateWidgets() {
        Log.d("debug","pushUpView start")
//        val target = ImageView(context)
//        target.setImageResource(R.drawable.loading_iv_icon)
//
//        binding.woClRoot.addView(target)
    }
}