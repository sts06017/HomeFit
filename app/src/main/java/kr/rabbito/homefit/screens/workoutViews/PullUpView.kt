package kr.rabbito.homefit.screens.workoutViews

import android.content.Context
import android.widget.ImageView
import kr.rabbito.homefit.R
import kr.rabbito.homefit.databinding.ActivityWoBinding

class PullUpView(val context: Context) {
    fun generateWidgets(binding: ActivityWoBinding) {
        val target = ImageView(context)
        target.setImageResource(R.drawable.loading_iv_icon)

        binding.woClRoot.addView(target)
    }
}