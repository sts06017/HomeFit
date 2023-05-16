package kr.rabbito.homefit.data

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime

class WorkoutTypeConverters {

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }
}