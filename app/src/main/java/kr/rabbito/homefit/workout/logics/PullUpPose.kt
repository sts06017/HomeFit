package kr.rabbito.homefit.workout.logics

import android.content.Context
import android.util.Log
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kr.rabbito.homefit.workout.WorkoutState

class PullUpPose {
    lateinit var pose: Pose
    lateinit var context: Context

    fun calcPullUp(pose: Pose) {
        Log.d("pose test", "call calcKneeDrive")

        val leftHand = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX)!!
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)!!
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)!!

        val rightHand = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX)!!
        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)!!
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)!!

        // 자세 검사
        try {
            var rightArmAngle = getAngle(rightHand!!, rightElbow!!, rightShoulder!!)
            //Log.d("Angle test","$rightArmAngle")
            if (rightArmAngle > 90) {

            } else {
                Log.d("Angle test", "under 90`")
            }
        } catch (e: NullPointerException) {
            return
        }

        // 횟수 카운트
        try {
            if (
                (getAngle(leftHand, leftElbow, leftShoulder) > 90)
                && (getAngle(rightHand, rightElbow, rightShoulder) > 90)
                && WorkoutState.isUp
            ) {
                Log.d("pull_up","down")
                WorkoutState.count += 1
                WorkoutState.isUp = false
            } else if (
                (getAngle(leftHand, leftElbow, leftShoulder) <= 90)
                && (getAngle(rightHand, rightElbow, rightShoulder) <= 90)
                && !WorkoutState.isUp
            ){
                Log.d("pull_up","up")
                WorkoutState.isUp = true
            }
        } catch (_: java.lang.NullPointerException) {
        }
    }
}

