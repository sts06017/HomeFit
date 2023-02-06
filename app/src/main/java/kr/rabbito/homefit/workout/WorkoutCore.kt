package kr.rabbito.homefit.workout

import android.content.Context
import kr.rabbito.homefit.databinding.ActivityWoBinding
import kr.rabbito.homefit.screens.workoutViews.*
import kr.rabbito.homefit.workout.logics.*

class WorkoutCore(val context: Context, val binding: ActivityWoBinding) {
    companion object {
        // 로직 적용 위한 객체
        val workoutPoses = arrayListOf(
            PushUpPose(),
            PullUpPose(),
            SquatPose(),
            SideLateralRaisePose(),
            DumbbellCurlPose(),
            LegRaisePose()
        )
    }

    // 위젯, 시각효과 적용 위한 객체
    val workoutViews = arrayListOf(
        PushUpView(context, binding),
        PullUpView(context, binding),
        SquatView(context, binding),
        SideLateralRaiseView(context, binding),
        DumbbellCurlView(context, binding),
        LegRaiseView(context, binding)
    )
}