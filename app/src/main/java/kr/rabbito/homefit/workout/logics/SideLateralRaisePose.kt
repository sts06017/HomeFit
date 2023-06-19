package kr.rabbito.homefit.workout.logics

import android.content.Context
import android.util.Log
import kr.rabbito.homefit.workout.WorkoutState
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic.Companion.redPaint
import kr.rabbito.homefit.workout.poseDetection.PoseGraphic.Companion.whitePaint
import kr.rabbito.homefit.workout.tts.PoseAdviceTTS
import kotlin.math.absoluteValue

class SideLateralRaisePose(context: Context, tts: PoseAdviceTTS) : WorkoutPose(context, tts) {
    private var ttsLowerFlag: Boolean = false
    private var ttsStraightFlag: Boolean = false

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

            if (ttsStraightFlag
                && getAngle(c.rightHand, c.rightElbow, c.rightShoulder) in 20.0..130.0
                && getAngle(c.leftHand, c.leftElbow, c.leftShoulder) in 20.0..130.0
            ) {
                tts.straightArmTTS() // 팔을 너무 구부린 경우 tts
            }

            if (!WorkoutState.isUp) { // 올라가는 시점
                Log.d("사레레 팔 각도", "${getAngle(c.rightElbow, c.rightShoulder, c.leftShoulder)}")
                if (
                    getAngle(c.rightElbow, c.rightShoulder, c.leftShoulder) < 140
                ) {   // 팔을 너무 낮게 들면 안내선 빨갛게
                    PoseGraphic.rightShoulderToRightElbowPaint = redPaint
                    PoseGraphic.rightElbowToRightWristPaint = redPaint
                } else {
                    PoseGraphic.rightShoulderToRightElbowPaint = whitePaint
                    PoseGraphic.rightElbowToRightWristPaint = whitePaint
                }

                if (
                    getAngle(c.leftElbow, c.leftShoulder, c.rightShoulder) < 140
                ) {
                    PoseGraphic.leftShoulderToLeftElbowPaint = redPaint
                    PoseGraphic.leftElbowToLeftWristPaint = redPaint
                } else {
                    PoseGraphic.leftShoulderToLeftElbowPaint = whitePaint
                    PoseGraphic.leftElbowToLeftWristPaint = whitePaint
                }

            }

            if (WorkoutState.isUp) {
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

                if (ttsLowerFlag && getYDistance(
                        c.rightElbow,
                        c.rightShoulder
                    ) <= -15 && getYDistance(c.leftElbow, c.leftShoulder) <= -15
                ) {
                    tts.lowerArmTTS()
                }
            }


        } catch (_: NullPointerException) {
            // 추후 로그 작성
        }
    }

    override fun checkCount(c: PoseCoordinate) {
        try {
            Log.d("사레레 각도", "겨: ${getAngle(c.leftElbow, c.leftShoulder, c.leftHip)}")
            Log.d(
                "side lateral raise",
                "L:${
                    getXDistance(
                        c.leftShoulder,
                        c.leftHand
                    ).absoluteValue.toInt()
                }, R:${getXDistance(c.rightShoulder, c.rightHand).absoluteValue.toInt()}"
            )
            if (
                (getAngle(c.leftElbow, c.leftShoulder, c.leftHip) > 85)
                && (getAngle(c.rightElbow, c.rightShoulder, c.rightHip) > 85)
                && getXDistance(
                    c.rightShoulder,
                    c.rightHand
                ).absoluteValue > 90  // 한 번에 여러번 검사되는 것 방지, 정확도 향상
                && getXDistance(c.leftShoulder, c.leftHand).absoluteValue > 90
                && !WorkoutState.isUp
            ) {
                Log.d("side lateral raise", "up")
                WorkoutState.count += 1
                WorkoutState.totalCount += 1
                WorkoutState.isUp = true

                tts.countTTS(WorkoutState.count)// 운동 횟수 카운트 tts
                ttsLowerFlag = true
                ttsStraightFlag = true

                checkSetCondition()
                checkEnd()
            } else if (
                (getAngle(c.leftElbow, c.leftShoulder, c.leftHip) <= 30)
                && (getAngle(c.rightElbow, c.rightShoulder, c.rightHip) <= 30)
                && getXDistance(c.rightShoulder, c.rightHand).absoluteValue <= 40
                && getXDistance(c.leftShoulder, c.leftHand).absoluteValue <= 40
                && WorkoutState.isUp
            ) {
                Log.d("side lateral raise", "down")
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
            Log.d("디버깅", "mySet plus 1 : ${WorkoutState.mySet.value}")

            if (!(WorkoutState.set == WorkoutState.setTotal + 1)) {
                tts.countSetTTS(WorkoutState.set) // 세트 수 증가 tts
            }
        }
    }

    // 운동이 끝났는지 확인
    private fun checkEnd() {
        if (WorkoutState.set == WorkoutState.setTotal + 1) {
            // 운동 종료
            Log.d("side lateral raise pose", "운동 종료")

            tts.WorkoutFinish() // 운동 종료 tts
        }
    }
}

