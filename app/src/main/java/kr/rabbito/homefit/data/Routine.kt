package kr.rabbito.homefit.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Routine")
class Routine(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "setName") var setName: String?,
    @ColumnInfo(name = "workoutName") var workoutName: String?,
    @ColumnInfo(name = "set") var set: Int?,
    @ColumnInfo(name = "count") var count: Int?,
    @ColumnInfo(name = "restTime") var restTime: Long?

): Serializable{ // DB 객체 단위로 전달하기 위함
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readLong()
    )
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeValue(id)
//        parcel.writeValue(setName)
//        parcel.writeString(workoutName)
//        parcel.writeValue(set)
//        parcel.writeValue(count)
//        parcel.writeValue(restTime)
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
    companion object CREATOR : Parcelable.Creator<Routine> {
        override fun createFromParcel(parcel: Parcel): Routine {
            return Routine(parcel)
        }

        override fun newArray(size: Int): Array<Routine?> {
            return arrayOfNulls(size)
        }
    }
}