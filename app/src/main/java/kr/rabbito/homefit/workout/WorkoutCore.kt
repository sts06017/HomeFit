package kr.rabbito.homefit.workout

import android.content.Context
import kr.rabbito.homefit.databinding.ActivityWoBinding
import kr.rabbito.homefit.screens.workoutView.*
import kr.rabbito.homefit.workout.logics.*
import kr.rabbito.homefit.workout.tts.PoseAdviceTTS

class WorkoutCore(val context: Context, val binding: ActivityWoBinding, val tts: PoseAdviceTTS) {

        // 로직 적용 위한 객체
        val workoutPoses = arrayListOf(
            PushUpPose(context, tts),
            PullUpPose(context, tts),
            SquatPose(context, tts),
            SideLateralRaisePose(context, tts),
            DumbbellCurlPose(context, tts),
            LegRaisePose(context, tts)
        )


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