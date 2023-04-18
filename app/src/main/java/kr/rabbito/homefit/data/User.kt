package kr.rabbito.homefit.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
class User(@PrimaryKey var id: Long?,
           @ColumnInfo(name = "userName") var userName: String?,
           @ColumnInfo(name = "height") var height: Int?,
           @ColumnInfo(name = "weight") var weight: Int?,
           @ColumnInfo(name = "mealCount") var mealCount: Int?,
           @ColumnInfo(name = "favWorkout") var favWorkout: String?,
           @ColumnInfo(name = "basicDiet") var basicDiet: String?,
    ) : Parcelable {    // 클래스의 객체를 인텐트로 전달할수 있게 해줌..

    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
    ) {}

    constructor() : this(null, null, null, null, null, null, null)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(userName)
        parcel.writeValue(height)
        parcel.writeValue(weight)
        parcel.writeValue(mealCount)
        parcel.writeString(favWorkout)
        parcel.writeString(basicDiet)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}