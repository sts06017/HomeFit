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
    @ColumnInfo(name = "weight") val weight: Double?,
    @ColumnInfo(name = "calorie") val calorie: Double?,
    @ColumnInfo(name = "carbohydrate") val carbohydrate: Double?,
    @ColumnInfo(name = "protein") val protein: Double?,
    @ColumnInfo(name = "fat") val fat: Double?,
    @ColumnInfo(name = "dDate") val dDate: LocalDate?,
    @ColumnInfo(name = "dTime") val dTime: String?,
    @ColumnInfo(name = "jsonHash") val jsonHash: Int?,
): Parcelable {
    constructor(parcel: Parcel?): this(
        parcel!!.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(LocalDate::class.java.classLoader) as? LocalDate,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(foodName)
        parcel.writeValue(weight)
        parcel.writeValue(calorie)
        parcel.writeValue(carbohydrate)
        parcel.writeValue(protein)
        parcel.writeValue(fat)
        parcel.writeValue(dDate)
        parcel.writeValue(dTime)
        parcel.writeValue(jsonHash)
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