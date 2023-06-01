package kr.rabbito.homefit.workout.logics

import android.content.Context
import android.util.Log
import kr.rabbito.homefit.workout.WorkoutState
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic.Companion.redPaint
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic.Companion.whitePaint
import kotlin.math.absoluteValue
import kr.rabbito.homefit.workout.tts.PoseAdviceTTS


class DumbbellCurlPose(context: Context): WorkoutPose(context) {
    private val poseAdviceTTS = PoseAdviceTTS(context)
    private var ttsArmFlag : Boolean = false
    private var ttsElbowFlag : Boolean = false

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

            if(!ttsElbowFlag
                && getXDistance(c.rightShoulder, c.rightElbow).absoluteValue > 20
                && getXDistance(c.leftShoulder, c.leftElbow).absoluteValue > 20){
                poseAdviceTTS.wrongElbowTTS() // 팔꿈치가 옆구리에서 너무 멀어진 경우 tts
                ttsElbowFlag = true

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
                    poseAdviceTTS.understretchArmTTS() // 팔을 내릴 때 너무 쭉 핀 경우 tts
                    ttsArmFlag = true
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

                ttsArmFlag = false
                ttsElbowFlag = false
                poseAdviceTTS.countTTS(WorkoutState.count)// 운동 횟수 카운트 tts
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
            }
        }
    }

    // 운동이 끝났는지 확인
    private fun checkEnd() {
        if (WorkoutState.set == WorkoutState.setTotal + 1) {
            // 운동 종료
            Log.d("dumbbell curl pose", "운동 종료")

            poseAdviceTTS.WorkoutFinish() // 운동 종료 tts

        }
    }
}

