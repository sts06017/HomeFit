package kr.rabbito.homefit.workout.logics

import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

class PoseCoordinate(val pose: Pose) {
    // 왼쪽
    val leftEar = pose.getPoseLandmark(PoseLandmark.LEFT_EAR)
    val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
    val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
    val leftHand = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX)
    val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
    val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
    val leftFeet = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)

    // 오른쪽
    val rightEar = pose.getPoseLandmark(PoseLandmark.RIGHT_EAR)
    val rightHand = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX)
    val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
    val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
    val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
    val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
    val rightFeet = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)
}