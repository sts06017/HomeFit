package kr.rabbito.homefit.data

import androidx.lifecycle.LiveData
import androidx.room.*
import java.time.LocalDate

@Dao
interface WorkoutDAO {
    @Query("SELECT * FROM Workout")
    fun getAll(): List<Workout>

    @Query("SELECT * FROM Workout WHERE date = :date")
    fun getWorkoutByDate(date: LocalDate): List<Workout>

//    @Query("UPDATE Workout SET workoutName = :workoutName, set = :height, count = :weight, mealCount = :mealCount, favWorkout = :favWorkout WHERE date = date")
//    fun updateWorkoutBydate(date: LocalDate, workoutName: String, height: Int, weight: Int, mealCount: Int, favWorkout: String, basicDiet: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(workout: Workout)

    @Delete
    fun delete(workout: Workout)

    @Query("DELETE FROM Workout")
    fun deleteAll()
}