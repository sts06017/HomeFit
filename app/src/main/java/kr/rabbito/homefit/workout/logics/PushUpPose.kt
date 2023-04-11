package kr.rabbito.homefit.workout.logics

import android.util.Log
import kr.rabbito.homefit.workout.WorkoutState
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic.Companion.redPaint
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic.Companion.whitePaint

class PushUpPose: WorkoutPose() {

    override fun guidePose(c: PoseCoordinate) {
        try {
            if (getYDistance(c.rightHand, c.rightShoulder) <= 0) {
                PoseGraphic.rightShoulderToRightElbowPaint = redPaint
                PoseGraphic.rightElbowToRightWristPaint = redPaint
            } else {
                PoseGraphic.rightShoulderToRightElbowPaint = whitePaint
                PoseGraphic.rightElbowToRightWristPaint = whitePaint
            }

            if (getYDistance(c.leftHand, c.leftShoulder) <= 0) {
                PoseGraphic.leftShoulderToLeftElbowPaint = redPaint
                PoseGraphic.leftElbowToLeftWristPaint = redPaint
            } else {
                PoseGraphic.leftShoulderToLeftElbowPaint = whitePaint
                PoseGraphic.leftElbowToLeftWristPaint = whitePaint
            }

            /*if (!WorkoutState.isUp) { // 내려가는 시점
                if (
                    getAngle(c.rightHand, c.rightElbow, c.rightShoulder) > 170
                ) {   // 팔을 너무 펴면 안내선 빨갛게
                    PoseGraphic.rightShoulderToRightElbowPaint = redPaint
                    PoseGraphic.rightElbowToRightWristPaint = redPaint
                } else {
                    PoseGraphic.rightShoulderToRightElbowPaint = whitePaint
                    PoseGraphic.rightElbowToRightWristPaint = whitePaint
                }

                if (
                    getAngle(c.leftHand, c.leftElbow, c.leftShoulder) > 170
                ) {
                    PoseGraphic.leftShoulderToLeftElbowPaint = redPaint
                    PoseGraphic.leftElbowToLeftWristPaint = redPaint
                } else {
                    PoseGraphic.leftShoulderToLeftElbowPaint = whitePaint
                    PoseGraphic.leftElbowToLeftWristPaint = whitePaint
                }
            }*/
        } catch (_: NullPointerException) {
            // 추후 로그 작성
        }
    }

    override fun checkCount(c: PoseCoordinate) {
        try {
            if (
                (getAngle(c.leftHand, c.leftElbow, c.leftShoulder) > 120)
                && (getAngle(c.rightHand, c.rightElbow,c. rightShoulder) > 120)
                //&& getYDistance(c.rightShoulder, c.rightHand) > 60  // 한 번에 여러번 검사되는 것 방지, 정확도 향상
                //&& getYDistance(c.leftShoulder, c.leftHand) > 60
                && WorkoutState.isUp
            ) {
                Log.d("push_up","up")
                WorkoutState.count += 1
                WorkoutState.isUp = false

                checkSetCondition()
                checkEnd()
            } else if (
                (getAngle(c.leftHand, c.leftElbow, c.leftShoulder) <= 90)
                && (getAngle(c.rightHand, c.rightElbow, c.rightShoulder) <= 90)
                //&& getYDistance(c.rightShoulder, c.rightHand) <= 60
                //&& getYDistance(c.leftShoulder, c.leftHand) <= 60
                && !WorkoutState.isUp
            ) {
                Log.d("push_up","down")
                WorkoutState.isUp = true
            }
        } catch (_: NullPointerException) {
        }
    }

    // 세트가 끝났는지 확인
    // 임시 public
    fun checkSetCondition() {
        if (WorkoutState.count == WorkoutState.setCondition) {
            WorkoutState.count = 0
            WorkoutState.set += 1
            WorkoutState.mySet.value = (WorkoutState.mySet.value!!) + 1 // 임시 live data 증가 코드
            Log.d("디버깅","mySet plus 1 : ${WorkoutState.mySet.value}")
        }
    }

    // 운동이 끝났는지 확인
    // 임시 public
    fun checkEnd() {
        if (WorkoutState.set == WorkoutState.setTotal + 1) {
            // 운동 종료
            Log.d("push_up", "finish")
        }
    }
}

