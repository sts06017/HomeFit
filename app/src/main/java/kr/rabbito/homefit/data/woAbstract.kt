package kr.rabbito.homefit.data

import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

// 임시

abstract class woAbstract(val name : String, val Set : Set,var pose : Pose){
    abstract fun setInit() // 자세 초기설정 세팅
    abstract fun detectWrong() // 잘못된 자세 감지
    abstract fun countReps() // 운동 횟수 측정
    abstract fun setPoses(pose: Pose) // PoseDetectorProcessor로 부터 포즈 좌표값 가져옴


}
