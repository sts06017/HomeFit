package kr.rabbito.homefit.workout.logics

import android.content.Context
import android.util.Log
import kr.rabbito.homefit.workout.WorkoutState
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic.Companion.redPaint
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic.Companion.whitePaint
import kr.rabbito.homefit.workout.tts.PoseAdviceTTS

class PullUpPose(context: Context, tts: PoseAdviceTTS): WorkoutPose(context, tts) {
    private var ttsHandShoulderFlag : Boolean = false
    private var ttsArmFlag : Boolean = false

    override fun guidePose(c: PoseCoordinate) {
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

            if(!ttsHandShoulderFlag
                && getYDistance(c.rightShoulder, c.rightHand) < 0
                && getYDistance(c.leftShoulder, c.leftHand) < 0){
                tts.highShoulderTTS() // 너무 높이 올라간 경우 tts
                ttsHandShoulderFlag = true
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

                if(!ttsArmFlag
                    && getAngle(c.rightHand, c.rightElbow, c.rightShoulder) > 170
                    && getAngle(c.leftHand, c.leftElbow, c.leftShoulder) > 170){
                    tts.understretchArmTTS() // 팔을 내릴 때 너무 쭉 핀 경우 tts
                    ttsArmFlag = true
                }
            }
        } catch (_: NullPointerException) {
            // 추후 로그 작성
        }
    }

    override fun checkCount(c: PoseCoordinate) {
        try {
            Log.d("풀업 각도", "${getAngle(c.leftHand, c.leftElbow, c.leftShoulder)}")
            if (
                (getAngle(c.leftHand, c.leftElbow, c.leftShoulder) > 95)
                && (getAngle(c.rightHand, c.rightElbow,c. rightShoulder) > 95)
                && getYDistance(c.rightShoulder, c.rightHand) > 60  // 한 번에 여러번 검사되는 것 방지, 정확도 향상
                && getYDistance(c.leftShoulder, c.leftHand) > 60
                && WorkoutState.isUp
            ) {
                Log.d("pull_up","down")
                WorkoutState.count += 1
                WorkoutState.totalCount += 1
                WorkoutState.isUp = false

                ttsArmFlag = false
                ttsHandShoulderFlag = false
                tts.countTTS(WorkoutState.count)// 운동 횟수 카운트 tts

                checkSetCondition()
                checkEnd()
            } else if (
                (getAngle(c.leftHand, c.leftElbow, c.leftShoulder) <= 95)
                && (getAngle(c.rightHand, c.rightElbow, c.rightShoulder) <= 95)
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

    // 세트가 끝났는지 확인
    private fun checkSetCondition() {
        if (WorkoutState.count == WorkoutState.setCondition) {
            WorkoutState.count = 0
            WorkoutState.set += 1
            WorkoutState.mySet.value = (WorkoutState.mySet.value!!) + 1 // 임시 live data 증가 코드
            Log.d("디버깅","mySet plus 1 : ${WorkoutState.mySet.value}")

            if (!(WorkoutState.set == WorkoutState.setTotal + 1)){
                tts.countSetTTS(WorkoutState.set) // 세트 수 증가 tts
            }        }
    }

    // 운동이 끝났는지 확인
    private fun checkEnd() {
        if (WorkoutState.set == WorkoutState.setTotal + 1) {
            // 운동 종료
            Log.d("pull up pose", "운동 종료")

            tts.WorkoutFinish() // 운동 종료 tts
        }
    }
}

