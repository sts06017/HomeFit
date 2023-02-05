package kr.rabbito.homefit.workout.logics

import android.content.Context
import android.util.Log
import com.google.mlkit.vision.pose.Pose
import kr.rabbito.homefit.workout.WorkoutState
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic.Companion.greenPaint
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic.Companion.redPaint

class PullUpPose: WorkoutPose() {
    lateinit var pose: Pose
    lateinit var context: Context

    override fun calcCount(pose: Pose) {
        Log.d("pose test", "call calcKneeDrive")

        val c = PoseCoordinate(pose)

        /*
        자세 검사 (임시)
        팔이 굽혀져 있으면 초록색으로 표시
         */
        try {
            //Log.d("Angle test","$rightArmAngle")
            if (getAngle(c.rightHand, c.rightElbow, c.rightShoulder) > 90 ) {
                //----각도가 90' 보다 큼
                PoseGraphic.rightShoulderToRightElbowPaint = redPaint // 90보다 크면 빨간색으로 변경
                PoseGraphic.rightElbowToRightWristPaint = redPaint

            } else if (getAngle(c.rightHand, c.rightElbow, c.rightShoulder) < 90) {
                //----각도가 90' 보다 작음
                PoseGraphic.rightShoulderToRightElbowPaint = greenPaint //90보다 작으면 초록색으로 변경
                PoseGraphic.rightElbowToRightWristPaint = greenPaint
            }
        } catch (_: NullPointerException) {
        }

        /*
        횟수 검사 (임시)
        팔을 굽혔다 피면 횟수 증가
         */
        try {
            if (
                (getAngle(c.leftHand, c.leftElbow, c.leftShoulder) > 90)
                && (getAngle(c.rightHand, c.rightElbow,c. rightShoulder) > 90)
                && WorkoutState.isUp
            ) {
                Log.d("pull_up","down")
                WorkoutState.count += 1
                WorkoutState.isUp = false
            } else if (
                (getAngle(c.leftHand, c.leftElbow, c.leftShoulder) <= 90)
                && (getAngle(c.rightHand, c.rightElbow, c.rightShoulder) <= 90)
                && !WorkoutState.isUp
            ) {
                Log.d("pull_up","up")
                WorkoutState.isUp = true
            }
        } catch (_: NullPointerException) {
        }
    }
}

