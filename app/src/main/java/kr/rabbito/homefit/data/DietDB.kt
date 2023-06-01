package kr.rabbito.homefit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Diet::class], version =1)
@TypeConverters(WorkoutTypeConverters::class)
abstract class DietDB : RoomDatabase() {
    abstract fun DietDAO(): DietDAO

    companion object {
        private var INSTANCE: DietDB? = null

        fun getInstance(context: Context): DietDB? {
            if (INSTANCE == null) {
                synchronized(DietDB::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        DietDB::class.java, "Diet.db"
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
