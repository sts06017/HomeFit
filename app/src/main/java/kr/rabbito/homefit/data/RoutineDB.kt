package kr.rabbito.homefit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Routine::class], version = 1)
@TypeConverters(WorkoutTypeConverters::class) // 형 변환
abstract class RoutineDB : RoomDatabase() {
    abstract fun routineDAO(): RoutineDAO

    companion object {
        private var INSTANCE: RoutineDB? = null

        fun getInstance(context: Context): RoutineDB? {
            if (INSTANCE == null) {
                synchronized(RoutineDB::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        RoutineDB::class.java, "Routine.db"
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
