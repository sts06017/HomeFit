package kr.rabbito.homefit.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "Workout")
class Workout(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "workoutName") val workoutName: String?,
    @ColumnInfo(name = "set") val set: Int?,
    @ColumnInfo(name = "count") val count: Int?,
    @ColumnInfo(name = "time") val workTime: Int?,
    @ColumnInfo(name = "date") val date: LocalDate?,
)