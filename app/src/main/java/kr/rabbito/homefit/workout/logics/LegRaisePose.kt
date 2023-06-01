package kr.rabbito.homefit.workout.logics

import android.content.Context
import android.util.Log
import kr.rabbito.homefit.workout.WorkoutState
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic.Companion.redPaint
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic.Companion.whitePaint
import kr.rabbito.homefit.workout.tts.PoseAdviceTTS

class LegRaisePose(context: Context) : WorkoutPose(context) {
    private val poseAdviceTTS = PoseAdviceTTS(context)
    private var ttsFlag : Boolean = false

    override fun guidePose(c: PoseCoordinate) {
        try {
            if (getAngle(c.leftFeet, c.leftHip, c.leftShoulder) < 80) {    // 다리를 너무 올린 경우
                PoseGraphic.leftHipToLeftKneePaint = redPaint
                PoseGraphic.leftKneeToLeftAnklePaint = redPaint
            } else {
                PoseGraphic.leftHipToLeftKneePaint = whitePaint
                PoseGraphic.leftKneeToLeftAnklePaint = whitePaint
            }

            if (getAngle(c.rightFeet, c.rightHip, c.rightShoulder) < 80) {
                PoseGraphic.rightHipToRightKneePaint = redPaint
                PoseGraphic.rightKneeToRightAnklePaint = redPaint
            } else {
                PoseGraphic.rightHipToRightKneePaint = whitePaint
                PoseGraphic.rightKneeToRightAnklePaint = whitePaint
            }

            if(!ttsFlag
                && getAngle(c.leftFeet, c.leftHip, c.leftShoulder) < 80
                && getAngle(c.rightFeet, c.rightHip, c.rightShoulder) < 80){
                // 다리를 너무 올린 경우 tts
                poseAdviceTTS.lowerLegTTS()
                ttsFlag = true
            }
        } catch (_: NullPointerException) {
            // 추후 로그 작성
        }
    }

    // 다리 펴고 하는 경우를 기준으로 계산
    override fun checkCount(c: PoseCoordinate) {
        try {
            if (
                getAngle(c.leftHip, c.leftShoulder, c.leftEar) > 140    // 고개 너무 굽히지 않도록
                && getAngle(c.leftFeet, c.leftHip, c.leftShoulder) < 100    // 다리 충분히 들었는지 확인
                && getAngle(c.rightFeet, c.rightHip, c.rightShoulder) < 100
                && WorkoutState.isUp
            ) {
                Log.d("leg_raise", "down")
                WorkoutState.count += 1
                WorkoutState.isUp = false

                poseAdviceTTS.countTTS(WorkoutState.count)// 운동 횟수 카운트 tts
                ttsFlag = false

                checkSetCondition()
                checkEnd()
            } else if (
                getAngle(c.leftHip, c.leftShoulder, c.leftEar) > 140
                && getAngle(c.leftFeet, c.leftHip, c.leftShoulder) > 130    // 다리 적당히 내렸는지 확인
                && getAngle(c.leftFeet, c.leftHip, c.leftShoulder) < 170
                && getAngle(c.rightFeet, c.rightHip, c.rightShoulder) > 130
                && getAngle(c.rightFeet, c.rightHip, c.rightShoulder) < 170
                && !WorkoutState.isUp
            ) {
                Log.d("leg_raise", "up")
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
                poseAdviceTTS.countSetTTS(WorkoutState.set) // 세트 수 증가 tts
            }        }
    }

    // 운동이 끝났는지 확인
    private fun checkEnd() {
        if (WorkoutState.set == WorkoutState.setTotal + 1) {
            // 운동 종료
            Log.d("leg_raise", "finish")

            poseAdviceTTS.WorkoutFinish() // 운동 종료 tts
        }
    }
}

