package kr.rabbito.homefit.screens.workoutViews

import android.content.Context
import kr.rabbito.homefit.databinding.ActivityWoBinding

abstract class WorkoutView(val context: Context, val binding: ActivityWoBinding) {
    open fun generateWidgets() {}
    open fun refreshValues() {}
}