package kr.rabbito.homefit.workout.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
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
//        // 1: 딜레이 설정
//        tts?.playSilentUtterance(750, TextToSpeech.QUEUE_ADD, null)
    }


    /*
    구상: 기본적인 동작에 대한 음성 안내를 함수 형태로 구성하고, 각 파일에서 PoseAdviceTTS 객체 생성후 함수 호출해 사용
     */

    fun wrongPoseTTS() {
        tts?.speak("잘못된 자세입니다.", TextToSpeech.QUEUE_ADD, null, "")
    }

    fun raiseArmTTS() {
        tts?.speak("팔이 너무 낮아요.", TextToSpeech.QUEUE_ADD, null, "")
    }
    
    fun lowerArmTTS(){
        tts?.speak("팔이 너무 높아요.", TextToSpeech.QUEUE_ADD, null, "")
    }

    fun straightArmTTS(){
        tts?.speak("팔을 곧게 펴주세요.", TextToSpeech.QUEUE_ADD, null, "")
    }

    fun frontStraightArmTTS(){
        tts?.speak("팔을 전방으로 곧게 펴주세요.", TextToSpeech.QUEUE_ADD, null, "")
    }
    
    fun understretchArmTTS(){
        tts?.speak("팔을 너무 쭉 피지 마세요.", TextToSpeech.QUEUE_ADD, null, "")
    }

    fun raiseLegTTS() {
        tts?.speak("다리가 너무 낮아요.", TextToSpeech.QUEUE_ADD, null, "")
    }

    fun lowerLegTTS(){
        tts?.speak("다리가 너무 높아요.", TextToSpeech.QUEUE_ADD, null, "")
    }
    
    fun wrongElbowTTS(){
        tts?.speak("팔꿈치를 옆구리에 붙이세요.", TextToSpeech.QUEUE_ADD, null, "")
    }
    
    fun highShoulderTTS(){
        tts?.speak("너무 높이 올라갔어요.", TextToSpeech.QUEUE_ADD, null, "")
    }
    
    fun raiseUpperbodyTTS(){
        tts?.speak("상체를 너무 많이 내렸어요.", TextToSpeech.QUEUE_ADD, null, "")
    }

    fun countTTS(count: Int){
        tts?.speak(count.toString(), TextToSpeech.QUEUE_ADD, null, "")
    }

    fun countSetTTS(count: Int){
        tts?.speak(count.toString()+"세트", TextToSpeech.QUEUE_ADD, null, "")
    }

    fun WorkoutFinish(){
        tts?.speak("수고하셨습니다.", TextToSpeech.QUEUE_ADD, null, "")
    }

    fun customTTS(string: String) {
        tts?.speak(string, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}