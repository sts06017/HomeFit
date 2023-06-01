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
    @ColumnInfo(name = "woDuration") val woDuration: Long?,
    @ColumnInfo(name = "date") val date: LocalDate?,
    @ColumnInfo(name = "time") val time: String?,
    @ColumnInfo(name = "restTime") val restTime: Long?,
): Parcelable {
    constructor(parcel: Parcel?): this(
        parcel!!.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(LocalDate::class.java.classLoader) as? LocalDate,
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(workoutName)
        parcel.writeValue(set)
        parcel.writeValue(count)
        parcel.writeValue(woDuration)
        parcel.writeValue(date)
        parcel.writeString(time)
        parcel.writeValue(restTime)
    }

    override fun describeContents(): Int {
        return 0
    }
    companion object CREATOR : Parcelable.Creator<Workout> {
        override fun createFromParcel(parcel: Parcel): Workout {
            return Workout(parcel)
        }

        override fun newArray(size: Int): Array<Workout?> {
            return arrayOfNulls(size)
        }
    }
}