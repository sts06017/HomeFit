package kr.rabbito.homefit.workout.logics

import android.content.Context
import com.google.mlkit.vision.pose.Pose

abstract class WorkoutPose(val context: Context) {
    open fun calculate(pose: Pose) {
        val coordinate = PoseCoordinate(pose)

        // 자세 검사
        guidePose(coordinate)

        // 횟수 검사
        checkCount(coordinate)
    }

    protected open fun guidePose(c: PoseCoordinate) {}
    protected open fun checkCount(c: PoseCoordinate) {}
}