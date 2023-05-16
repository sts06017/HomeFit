package kr.rabbito.homefit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Workout::class], version = 4)
@TypeConverters(WorkoutTypeConverters::class)
abstract class WorkoutDB : RoomDatabase() {
    abstract fun workoutDAO(): WorkoutDAO

    companion object {
        private var INSTANCE: WorkoutDB? = null

        fun getInstance(context: Context): WorkoutDB? {
            if (INSTANCE == null) {
                synchronized(WorkoutDB::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        WorkoutDB::class.java, "Workout.db"
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
