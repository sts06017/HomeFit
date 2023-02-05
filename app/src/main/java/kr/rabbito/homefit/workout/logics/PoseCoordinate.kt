package kr.rabbito.homefit.workout.logics

import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

class PoseCoordinate(val pose: Pose) {
    val leftHand = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX)!!
    val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)!!
    val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)!!

    val rightHand = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX)!!
    val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)!!
    val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)!!
}