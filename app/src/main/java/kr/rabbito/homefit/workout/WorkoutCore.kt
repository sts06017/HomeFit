package kr.rabbito.homefit.workout

import android.content.Context
import kr.rabbito.homefit.databinding.ActivityWoBinding
import kr.rabbito.homefit.screens.workoutViews.PullUpView
import kr.rabbito.homefit.workout.logics.PullUpPose

class WorkoutCore(val context: Context, val binding: ActivityWoBinding) {
    companion object {
        // 로직 적용 위한 객체
        val workoutPoses = arrayListOf(PullUpPose())
    }

    // 위젯, 시각효과 적용 위한 객체
    val workoutViews = arrayListOf(PullUpView(context, binding))
}