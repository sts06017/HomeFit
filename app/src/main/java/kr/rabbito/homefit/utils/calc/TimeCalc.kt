package kr.rabbito.homefit.utils.calc

import java.util.concurrent.TimeUnit

class TimeCalc {
    companion object {
        fun secToHourMinSec(time: Int): Array<Int> {
            val sec = time % 60
            val min = (time % 3600) / 60
            val hour = time / 3600

            return arrayOf(hour, min, sec)
        }
        fun milliSecFormat(millis: Long): String{
            val hour = TimeUnit.MILLISECONDS.toHours(millis)
            val minute = TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1)
            val second = TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)

            return String.format("%02d:%02d:%02d",hour,minute,second)
        }
    }
}