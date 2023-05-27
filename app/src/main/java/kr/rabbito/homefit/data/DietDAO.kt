package kr.rabbito.homefit.data

import androidx.room.*
import java.time.LocalDate

@Dao
interface DietDAO {
    @Query("SELECT * FROM Diet")
    fun getAll(): List<Diet>

    @Query("SELECT * FROM Diet WHERE dDate = :dDate")
    fun getDietByDate(dDate: LocalDate): List<Diet>

//    @Query("UPDATE Workout SET workoutName = :workoutName, set = :height, count = :weight, mealCount = :mealCount, favWorkout = :favWorkout WHERE date = date")
//    fun updateWorkoutBydate(date: LocalDate, workoutName: String, height: Int, weight: Int, mealCount: Int, favWorkout: String, basicDiet: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(diet: Diet)

    @Delete
    fun delete(diet: Diet)

    @Query("DELETE FROM Diet")
    fun deleteAll()
}
