package kr.rabbito.homefit.workout.logics

import android.content.Context
import android.util.Log
import kr.rabbito.homefit.workout.WorkoutState
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic.Companion.redPaint
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic.Companion.whitePaint
import kotlin.math.absoluteValue

import kr.rabbito.homefit.workout.tts.PoseAdviceTTS

class SideLateralRaisePose(context: Context): WorkoutPose(context) {
    private val poseAdviceTTS = PoseAdviceTTS(context)
    private var ttsLowerFlag : Boolean = false
    private var ttsStraightFlag : Boolean = false

    override fun guidePose(c: PoseCoordinate) {
        try {   //팔을 굽히면 빨간색 표시
            if (getAngle(c.rightHand, c.rightElbow, c.rightShoulder) < 130) {
                PoseGraphic.rightShoulderToRightElbowPaint = redPaint
                PoseGraphic.rightElbowToRightWristPaint = redPaint
            } else {
                PoseGraphic.rightShoulderToRightElbowPaint = whitePaint
                PoseGraphic.rightElbowToRightWristPaint = whitePaint
            }

            if (getAngle(c.leftHand, c.leftElbow, c.leftShoulder) < 130) {
                PoseGraphic.leftShoulderToLeftElbowPaint = redPaint
                PoseGraphic.leftElbowToLeftWristPaint = redPaint
            } else {
                PoseGraphic.leftShoulderToLeftElbowPaint = whitePaint
                PoseGraphic.leftElbowToLeftWristPaint = whitePaint
            }

            if(!ttsStraightFlag
                && getAngle(c.rightHand, c.rightElbow, c.rightShoulder) < 130
                && getAngle(c.leftHand, c.leftElbow, c.leftShoulder) < 130){
                poseAdviceTTS.straightArmTTS() // 팔을 너무 구부린 경우 tts
                ttsStraightFlag = true
            }

            if (!WorkoutState.isUp) { // 올라가는 시점
                if (
                    getAngle(c.rightElbow, c.rightShoulder, c.leftShoulder) < 130
                ) {   // 팔을 너무 낮게 들면 안내선 빨갛게
                    PoseGraphic.rightShoulderToRightElbowPaint = redPaint
                    PoseGraphic.rightElbowToRightWristPaint = redPaint
                } else {
                    PoseGraphic.rightShoulderToRightElbowPaint = whitePaint
                    PoseGraphic.rightElbowToRightWristPaint = whitePaint
                }

                if (
                    getAngle(c.leftElbow, c.leftShoulder, c.rightShoulder) < 130
                ) {
                    PoseGraphic.leftShoulderToLeftElbowPaint = redPaint
                    PoseGraphic.leftElbowToLeftWristPaint = redPaint
                } else {
                    PoseGraphic.leftShoulderToLeftElbowPaint = whitePaint
                    PoseGraphic.leftElbowToLeftWristPaint = whitePaint
                }

            }

            if(WorkoutState.isUp){
                if (
                    getYDistance(c.rightElbow, c.rightShoulder) <= -15
                ) {   // 팔을 너무 높게 들면 안내선 빨갛게
                    PoseGraphic.rightShoulderToRightElbowPaint = redPaint
                    PoseGraphic.rightElbowToRightWristPaint = redPaint
                } else {
                    PoseGraphic.rightShoulderToRightElbowPaint = whitePaint
                    PoseGraphic.rightElbowToRightWristPaint = whitePaint
                }

                Log.d("higharm", getYDistance(c.rightElbow, c.rightShoulder).toString())

                if (
                    getYDistance(c.leftElbow, c.leftShoulder) <= -15
                ) {
                    PoseGraphic.leftShoulderToLeftElbowPaint = redPaint
                    PoseGraphic.leftElbowToLeftWristPaint = redPaint
                } else {
                    PoseGraphic.leftShoulderToLeftElbowPaint = whitePaint
                    PoseGraphic.leftElbowToLeftWristPaint = whitePaint
                }

                if(!ttsLowerFlag && getYDistance(c.rightElbow, c.rightShoulder) <= -15 && getYDistance(c.leftElbow, c.leftShoulder) <= -15){
                    poseAdviceTTS.lowerArmTTS()
                    ttsLowerFlag =true
                }
            }


        } catch (_: NullPointerException) {
            // 추후 로그 작성
        }
    }

    override fun checkCount(c: PoseCoordinate) {
        try {
            Log.d("사레레 각도","겨: ${getAngle(c.leftElbow,c.leftShoulder,c.leftHip)}")
            Log.d("side lateral raise","L:${getXDistance(c.leftShoulder, c.leftHand).absoluteValue.toInt()}, R:${getXDistance(c.rightShoulder, c.rightHand).absoluteValue.toInt()}")
            if (
                (getAngle(c.leftElbow, c.leftShoulder, c.leftHip) > 80)
                && (getAngle(c.rightElbow, c.rightShoulder,c. rightHip) > 80)
                && getXDistance(c.rightShoulder, c.rightHand).absoluteValue > 90  // 한 번에 여러번 검사되는 것 방지, 정확도 향상
                && getXDistance(c.leftShoulder, c.leftHand).absoluteValue > 90
                && !WorkoutState.isUp
            ) {
                Log.d("side lateral raise","up")
                WorkoutState.count += 1
                WorkoutState.isUp = true

                poseAdviceTTS.countTTS(WorkoutState.count)// 운동 횟수 카운트 tts
                ttsLowerFlag = false
                ttsStraightFlag = false

                checkSetCondition()
                checkEnd()
            } else if (
                (getAngle(c.leftElbow, c.leftShoulder, c.leftHip) <= 30)
                && (getAngle(c.rightElbow, c.rightShoulder,c. rightHip) <= 30)
                && getXDistance(c.rightShoulder, c.rightHand).absoluteValue <= 40
                && getXDistance(c.leftShoulder, c.leftHand).absoluteValue <= 40
                && WorkoutState.isUp
            ) {
                Log.d("side lateral raise","down")
                WorkoutState.isUp = false

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
            Log.d("side lateral raise pose", "운동 종료")

            poseAdviceTTS.WorkoutFinish() // 운동 종료 tts
        }
    }
}

