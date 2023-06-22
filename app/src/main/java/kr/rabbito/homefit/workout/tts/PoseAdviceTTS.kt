package kr.rabbito.homefit.workout.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import kr.rabbito.homefit.workout.WorkoutState
import java.util.*

class PoseAdviceTTS(val context: Context) {
    var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context, TextToSpeech.OnInitListener {
            if (it == TextToSpeech.SUCCESS) {
                val result = tts!!.setLanguage(Locale.KOREAN)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("tts", "Language not supported.")
                    return@OnInitListener
                }
            }
        })
//        // 음성 톤 높이
//        tts?.setPitch(0.25f)
    }


    /*
    구상: 기본적인 동작에 대한 음성 안내를 함수 형태로 구성하고, 각 파일에서 PoseAdviceTTS 객체 생성후 함수 호출해 사용
     */

    private fun checkCondition(): Boolean {
        // 지정한 제한 시간 지난 후에만 음성 출력
        Log.d("ttsDelay", WorkoutState.ttsDelay.toString())
        if (WorkoutState.ttsDelay >= WorkoutState.ttsDelayLimit && WorkoutState.count >= 1) {
            WorkoutState.ttsDelay = 0
            return true
        } else {
            return false
        }
    }

    fun wrongPoseTTS() {
        if (checkCondition()) tts?.speak("잘못된 자세입니다.", TextToSpeech.QUEUE_ADD, null, "")
    }

    fun raiseArmTTS() {
        if (checkCondition()) tts?.speak("팔이 너무 낮아요.", TextToSpeech.QUEUE_ADD, null, "")
    }
    
    fun lowerArmTTS(){
        if (checkCondition()) tts?.speak("팔이 너무 높아요.", TextToSpeech.QUEUE_ADD, null, "")
    }

    fun straightArmTTS(){
        if (checkCondition()) tts?.speak("팔을 펴주세요.", TextToSpeech.QUEUE_ADD, null, "")
    }

    fun frontStraightArmTTS(){
        if (checkCondition()) tts?.speak("팔을 앞으로 펴주세요.", TextToSpeech.QUEUE_ADD, null, "")
    }
    
    fun underStretchArmTTS(){
        if (checkCondition()) tts?.speak("팔을 너무 쭉 피지 마세요.", TextToSpeech.QUEUE_ADD, null, "")
    }

    fun raiseLegTTS() {
        if (checkCondition()) tts?.speak("다리가 너무 낮아요.", TextToSpeech.QUEUE_ADD, null, "")
    }

    fun lowerLegTTS(){
        if (checkCondition()) tts?.speak("다리가 너무 높아요.", TextToSpeech.QUEUE_ADD, null, "")
    }
    
    fun wrongElbowTTS(){
        if (checkCondition()) tts?.speak("팔꿈치를 옆구리에 붙이세요.", TextToSpeech.QUEUE_ADD, null, "")
    }
    
    fun highShoulderTTS(){
        if (checkCondition()) tts?.speak("너무 높이 올라갔어요.", TextToSpeech.QUEUE_ADD, null, "")
    }
    
    fun raiseUpperbodyTTS(){
        if (checkCondition()) tts?.speak("상체를 너무 많이 내렸어요.", TextToSpeech.QUEUE_ADD, null, "")
    }

    fun countTTS(count: Int){
        // 횟수 카운트에는 딜레이 적용 x
        tts?.speak(count.toString(), TextToSpeech.QUEUE_ADD, null, "")
    }

    fun countSetTTS(count: Int){
        // 세트 카운트에는 딜레이 적용 x
        tts?.speak(count.toString()+"세트", TextToSpeech.QUEUE_ADD, null, "")
    }

    fun WorkoutFinish(){
        tts?.speak("수고하셨습니다.", TextToSpeech.QUEUE_ADD, null, "")
    }

    fun customTTS(string: String) {
        if (checkCondition()) tts?.speak(string, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    fun finish() {
        tts?.shutdown()
    }
}