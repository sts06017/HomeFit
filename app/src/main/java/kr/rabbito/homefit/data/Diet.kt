package kr.rabbito.homefit.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "Diet")
class Diet(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "foodName") val foodName: String?,
    @ColumnInfo(name = "calorie") val calorie: Int?,
    @ColumnInfo(name = "dDate") val dDate: LocalDate?,
    @ColumnInfo(name = "dTime") val dTime: String?,
    @ColumnInfo(name = "carbohydrates") val carbohydrates: Long?,
    @ColumnInfo(name = "protein") val protein: Long?,
    @ColumnInfo(name = "fat") val fat: Long?,
    @ColumnInfo(name = "water") val water: Long?,
): Parcelable {
    constructor(parcel: Parcel?): this(
        parcel!!.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(LocalDate::class.java.classLoader) as? LocalDate,
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(foodName)
        parcel.writeValue(calorie)
        parcel.writeValue(dDate)
        parcel.writeValue(dTime)
        parcel.writeValue(carbohydrates)
        parcel.writeValue(protein)
        parcel.writeValue(fat)
        parcel.writeValue(water)
    }

    override fun describeContents(): Int {
        return 0
    }
    companion object CREATOR : Parcelable.Creator<Diet> {
        override fun createFromParcel(parcel: Parcel): Diet {
            return Diet(parcel)
        }

        override fun newArray(size: Int): Array<Diet?> {
            return arrayOfNulls(size)
        }
    }
}