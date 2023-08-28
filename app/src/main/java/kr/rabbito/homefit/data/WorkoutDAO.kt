package kr.rabbito.homefit.data

import androidx.room.*
import java.time.LocalDate

@Dao
interface WorkoutDAO {
    @Query("SELECT * FROM Workout")
    fun getAll(): List<Workout>

    @Query("SELECT * FROM Workout WHERE date = :date")
    fun getWorkoutByDate(date: LocalDate): List<Workout>

    @Query("SELECT count FROM Workout WHERE id <= :id ORDER BY id  DESC LIMIT 4")
    fun getCurrentCounts(id: Int): List<Int>
    @Query("SELECT count FROM Workout WHERE workoutName = :workoutName AND id <= :id ORDER BY id DESC LIMIT 4")
    fun getCurrentCountsByWOName(workoutName: String, id: Int): List<Int>

    @Query("SELECT count FROM Workout WHERE id = :id")
    fun getCountById(id: Int): Int
    @Query("SELECT MAX(id) FROM Workout")
    fun getMaxId(): Int

//    @Query("UPDATE Workout SET workoutName = :workoutName, set = :height, count = :weight, mealCount = :mealCount, favWorkout = :favWorkout WHERE date = date")
//    fun updateWorkoutBydate(date: LocalDate, workoutName: String, height: Int, weight: Int, mealCount: Int, favWorkout: String, basicDiet: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(workout: Workout)

    @Query("DELETE FROM Workout WHERE id = :id")
    fun deleteRecord(id: Int)

    @Delete
    fun delete(workout: Workout)

    @Query("DELETE FROM Workout")
    fun deleteAll()
}
