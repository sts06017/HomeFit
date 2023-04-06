package kr.rabbito.homefit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 5)
abstract class UserDB : RoomDatabase() {
    abstract fun userDAO(): UserDAO

    companion object {
        private var INSTANCE: UserDB? = null

        fun getInstance(context: Context): UserDB? {
            if (INSTANCE == null) {
                synchronized(UserDB::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        UserDB::class.java, "User.db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}