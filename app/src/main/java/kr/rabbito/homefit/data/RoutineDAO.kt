package kr.rabbito.homefit.data

import androidx.room.*
import java.time.LocalDate

@Dao
interface RoutineDAO {
    @Query("SELECT * FROM Routine")
    fun getAll(): List<Routine>

//    @Query("SELECT * FROM Routine WHERE date = :date")
//    fun getRoutineByDate(date: LocalDate): List<Routine>

    // 각 운동명에 따른 정보 가져오기
    @Query("SELECT * FROM Routine WHERE workoutName = :workoutName")
    fun getRoutineByWorkoutName(workoutName: LocalDate): List<Routine>

    @Query("SELECT count FROM Routine WHERE id <= :id ORDER BY id  DESC LIMIT 4")
    fun getCurrentCounts(id: Int): List<Int>

    @Query("SELECT MAX(id) FROM Routine")
    fun getMaxId(): Int

//    @Query("UPDATE Workout SET workoutName = :workoutName, set = :height, count = :weight, mealCount = :mealCount, favWorkout = :favWorkout WHERE date = date")
//    fun updateWorkoutBydate(date: LocalDate, workoutName: String, height: Int, weight: Int, mealCount: Int, favWorkout: String, basicDiet: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(routine: Routine)

    @Query("DELETE FROM Routine WHERE id = :id")
    fun deleteRecord(id: Int)

    @Delete
    fun delete(routine: Routine)

    @Query("DELETE FROM Routine")
    fun deleteAll()
}
