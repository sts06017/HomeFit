package kr.rabbito.homefit.workout.logics

import android.content.Context
import android.util.Log
import com.google.mlkit.vision.pose.Pose
import kr.rabbito.homefit.workout.WorkoutState
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic.Companion.redPaint
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic.Companion.whitePaint

class PullUpPose: WorkoutPose() {
    lateinit var pose: Pose
    lateinit var context: Context

    override fun calculate(pose: Pose) {
        val coordinate = PoseCoordinate(pose)

        // 자세 검사
        guidePose(coordinate)

        // 횟수 검사
        checkCount(coordinate)
    }

    private fun guidePose(c: PoseCoordinate) {
        try {
            if (getYDistance(c.rightShoulder, c.rightHand) < 0) {
                PoseGraphic.rightShoulderToRightElbowPaint = redPaint
                PoseGraphic.rightElbowToRightWristPaint = redPaint
            } else {
                PoseGraphic.rightShoulderToRightElbowPaint = whitePaint
                PoseGraphic.rightElbowToRightWristPaint = whitePaint
            }

            if (getYDistance(c.leftShoulder, c.leftHand) < 0) {
                PoseGraphic.leftShoulderToLeftElbowPaint = redPaint
                PoseGraphic.leftElbowToLeftWristPaint = redPaint
            } else {
                PoseGraphic.leftShoulderToLeftElbowPaint = whitePaint
                PoseGraphic.leftElbowToLeftWristPaint = whitePaint
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

    private fun checkCount(c: PoseCoordinate) {
        try {
            if (
                (getAngle(c.leftHand, c.leftElbow, c.leftShoulder) > 90)
                && (getAngle(c.rightHand, c.rightElbow,c. rightShoulder) > 90)
                && getYDistance(c.rightShoulder, c.rightHand) > 60  // 한 번에 여러번 검사되는 것 방지, 정확도 향상
                && getYDistance(c.leftShoulder, c.leftHand) > 60
                && WorkoutState.isUp
            ) {
                Log.d("pull_up","down")
                WorkoutState.count += 1
                WorkoutState.isUp = false
            } else if (
                (getAngle(c.leftHand, c.leftElbow, c.leftShoulder) <= 90)
                && (getAngle(c.rightHand, c.rightElbow, c.rightShoulder) <= 90)
                && getYDistance(c.rightShoulder, c.rightHand) <= 60
                && getYDistance(c.leftShoulder, c.leftHand) <= 60
                && !WorkoutState.isUp
            ) {
                Log.d("pull_up","up")
                WorkoutState.isUp = true
            }
        } catch (_: NullPointerException) {
        }
    }
}

