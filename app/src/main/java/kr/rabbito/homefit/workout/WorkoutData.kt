package kr.rabbito.homefit.workout

import android.content.Context
import kr.rabbito.homefit.screens.workoutViews.PullUpView

class WorkoutData(val context: Context){
    companion object {
        // 운동 이름
        val workoutNamesENG = arrayListOf("Push Up", "Pull Up", "Squat", "Side Lateral Raise", "Dumbbell Curl", "Leg Raise")
        val workoutNamesKOR = arrayListOf("팔굽혀펴기", "턱걸이", "스쿼트", "사이드 레터럴 레이즈", "덤벨 컬", "레그 레이즈")

        // 운동 사진
        val workoutImages = {}
    }
}