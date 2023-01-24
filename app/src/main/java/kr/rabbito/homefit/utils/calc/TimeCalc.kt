package kr.rabbito.homefit.utils.calc

class TimeCalc {
    companion object {
        fun secToHourMinSec(time: Int): Array<Int> {
            val sec = time % 60
            val min = (time % 3600) / 60
            val hour = time / 3600

            return arrayOf(hour, min, sec)
        }
    }
}