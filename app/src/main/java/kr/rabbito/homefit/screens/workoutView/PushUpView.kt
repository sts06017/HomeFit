package kr.rabbito.homefit.screens.workoutView

import android.content.Context
import android.util.Log
import kr.rabbito.homefit.databinding.ActivityWoBinding

class PushUpView(context: Context, binding: ActivityWoBinding): WorkoutView(context, binding) {
    override fun generateWidgets() {
        Log.d("debug","pushUpView start")
//        val target = ImageView(context)
//        target.setImageResource(R.drawable.loading_iv_icon)
//
//        binding.woClRoot.addView(target)
    }
}