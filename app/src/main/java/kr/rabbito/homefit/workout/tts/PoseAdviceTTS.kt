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
    }

    /*
    구상: 기본적인 동작에 대한 음성 안내를 함수 형태로 구성하고, 각 파일에서 PoseAdviceTTS 객체 생성후 함수 호출해 사용
     */

    fun wrongPoseTTS() {
        tts?.speak("잘못된 자세입니다.", TextToSpeech.QUEUE_FLUSH, null, "")
    }

    fun raiseArmTTS() {
        tts?.speak("팔을 올리세요.", TextToSpeech.QUEUE_FLUSH, null, "")
    }

    fun customTTS(string: String) {
        tts?.speak(string, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}