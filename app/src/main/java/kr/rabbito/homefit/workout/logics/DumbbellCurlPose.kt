package kr.rabbito.homefit.workout.logics

import android.util.Log
import kr.rabbito.homefit.workout.WorkoutState
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic.Companion.redPaint
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic.Companion.whitePaint
import kotlin.math.absoluteValue

class DumbbellCurlPose: WorkoutPose() {

    override fun guidePose(c: PoseCoordinate) {
        try {
            if (getXDistance(c.rightShoulder, c.rightElbow).absoluteValue > 20) {
                PoseGraphic.rightShoulderToRightElbowPaint = redPaint
                PoseGraphic.rightShoulderToRightHipPaint = redPaint
            } else {
                PoseGraphic.rightShoulderToRightElbowPaint = whitePaint
                PoseGraphic.rightShoulderToRightHipPaint = whitePaint
            }

            if (getXDistance(c.leftShoulder, c.leftElbow).absoluteValue > 20) {
                PoseGraphic.leftShoulderToLeftElbowPaint = redPaint
                PoseGraphic.leftShoulderToLeftHipPaint = redPaint
            } else {
                PoseGraphic.leftShoulderToLeftElbowPaint = whitePaint
                PoseGraphic.leftShoulderToLeftHipPaint = whitePaint
            }

            if (!WorkoutState.isUp) { // 내려가는 시점
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
            }
        } catch (_: NullPointerException) {
            // 추후 로그 작성
        }
    }

    override fun checkCount(c: PoseCoordinate) {
        try {
            if (
                (getAngle(c.leftHand, c.leftElbow, c.leftShoulder) > 120)
                && (getAngle(c.rightHand, c.rightElbow,c. rightShoulder) > 120)
                && getYDistance(c.leftHand, c.rightHand).absoluteValue < 30  // 한 번에 여러번 검사되는 것 방지, 정확도 향상
                //&& getYDistance(c.leftShoulder, c.leftHand) > 60
                && !WorkoutState.isUp
            ) {
                Log.d("dumbbell_curl","down")
                WorkoutState.isUp = true

                checkSetCondition()
                checkEnd()
            } else if (
                (getAngle(c.leftHand, c.leftElbow, c.leftShoulder) < 90)
                && (getAngle(c.rightHand, c.rightElbow, c.rightShoulder) < 90)
                && getYDistance(c.leftHand, c.rightHand).absoluteValue < 30
                //&& getYDistance(c.leftShoulder, c.leftHand) <= 60
                && WorkoutState.isUp
            ) {
                Log.d("dumbbell_curl","up")
                WorkoutState.count += 1
                WorkoutState.isUp = false
            }
        } catch (_: NullPointerException) {
        }
    }

    // 세트가 끝났는지 확인
    private fun checkSetCondition() {
        if (WorkoutState.count == WorkoutState.setCondition) {
            WorkoutState.count = 0
            WorkoutState.set += 1
        }
    }

    // 운동이 끝났는지 확인
    private fun checkEnd() {
        if (WorkoutState.set == WorkoutState.setTotal + 1) {
            // 운동 종료
            Log.d("dumbbell curl pose", "운동 종료")
        }
    }
}

