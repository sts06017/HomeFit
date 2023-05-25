package kr.rabbito.homefit.workout.logics

import android.util.Log
import kr.rabbito.homefit.workout.WorkoutState
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic.Companion.redPaint
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic.Companion.whitePaint

class SquatPose: WorkoutPose() {

    override fun guidePose(c: PoseCoordinate) {
        try {
            if (
                getAngle(c.leftHand, c.leftShoulder, c.leftHip) < 60    // 팔을 내리거나
                || getAngle(c.leftHand, c.leftElbow, c.leftShoulder) < 140  // 팔을 굽히면
            ) {
                PoseGraphic.leftShoulderToLeftElbowPaint = redPaint
                PoseGraphic.leftElbowToLeftWristPaint = redPaint
            } else {
                PoseGraphic.leftShoulderToLeftElbowPaint = whitePaint
                PoseGraphic.leftElbowToLeftWristPaint = whitePaint
            }

            if (
                getAngle(c.rightHand, c.rightShoulder, c.rightHip) < 60
                || getAngle(c.rightHand, c.rightElbow, c.rightShoulder) < 140
            ) {
                PoseGraphic.rightShoulderToRightElbowPaint = redPaint
                PoseGraphic.rightElbowToRightWristPaint = redPaint
            } else {
                PoseGraphic.rightShoulderToRightElbowPaint = whitePaint
                PoseGraphic.rightElbowToRightWristPaint = whitePaint
            }
        } catch (_: NullPointerException) {
            // 추후 로그 작성
        }
    }

    override fun checkCount(c: PoseCoordinate) {
        try {
            Log.d("운동 테스트","${WorkoutState.isUp}")
            Log.d("스쿼트 각도","lH: ${getAngle(c.leftShoulder,c.leftHip,c.leftKnee)}, lK: ${getAngle(c.leftHip,c.leftKnee,c.leftFeet)}")
            if (
                getAngle(c.leftShoulder, c.leftHip, c.leftKnee) < 90    // 몸을 충분히 내리고
                && getAngle(c.leftHip, c.leftKnee, c.leftFeet) < 120 // 다리를 충분히 굽히게
                && getAngle(c.rightShoulder, c.rightHip, c.rightKnee) < 90
                && getAngle(c.rightHip, c.rightKnee, c.rightFeet) < 120
//                && getAngle(c.rightShoulder, c.rightHip, c.rightKnee) > 90
//                && getAngle(c.rightHip, c.rightKnee, c.rightFeet) > 90
                && getYDistance(c.rightFeet, c.rightElbow) > 200 //  정확도 향상 목적
                && getYDistance(c.leftFeet, c.leftElbow) > 200   //  정확도 향상 목적
                && WorkoutState.isUp
            ) {
                Log.d("squat","down")
                WorkoutState.isUp = false
            } else if (
                getAngle(c.leftShoulder, c.leftHip, c.leftKnee) >= 160
                && getAngle(c.leftHip, c.leftKnee, c.leftFeet) >= 160
                && getAngle(c.rightShoulder, c.rightHip, c.rightKnee) >= 160
                && getAngle(c.rightHip, c.rightKnee, c.rightFeet) >= 160
                && getYDistance(c.rightFeet, c.rightElbow) > 200 //  정확도 향상 목적
                && getYDistance(c.leftFeet, c.leftElbow) > 200   //  정확도 향상 목적
                && !WorkoutState.isUp
            ) {
                Log.d("squat","up")
                WorkoutState.isUp = true
                WorkoutState.count += 1
                WorkoutState.totalCount += 1

                checkSetCondition()
                checkEnd()
            }
        } catch (_: NullPointerException) {
        }
    }

    // 세트가 끝났는지 확인
    private fun checkSetCondition() {
        if (WorkoutState.count == WorkoutState.setCondition) {
            WorkoutState.count = 0
            WorkoutState.set += 1
            WorkoutState.mySet.value = (WorkoutState.mySet.value!!) + 1 // 임시 live data 증가 코드
            Log.d("디버깅","mySet plus 1 : ${WorkoutState.mySet.value}")
        }
    }

    // 운동이 끝났는지 확인
    private fun checkEnd() {
        if (WorkoutState.set == WorkoutState.setTotal + 1) {
            // 운동 종료
            Log.d("squat", "finish")
        }
    }
}

