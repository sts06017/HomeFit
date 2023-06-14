package kr.rabbito.homefit.data

import androidx.room.*
import kr.rabbito.homefit.screens.navigatorBar.DietCalorie
import kr.rabbito.homefit.screens.navigatorBar.Nutrient
import java.time.LocalDate

@Dao
interface DietDAO {
    @Query("SELECT * FROM Diet")
    fun getAll(): List<Diet>

    @Query("SELECT * FROM Diet WHERE dDate = :dDate")
    suspend fun getDietByDate(dDate: LocalDate): List<Diet>

    @Query("SELECT dDate, SUM(calorie) AS totalCalorie FROM Diet WHERE dDate >= date('now','-5 days') GROUP BY dDate ORDER BY dDate LIMIT 6")
    suspend fun getCaloriesByDate(): List<DietCalorie>

    @Query("SELECT * FROM Diet WHERE jsonHash = :jsonHash LIMIT 1")
    fun findByJsonHash(jsonHash: Int): Diet?

    @Query("SELECT carbohydrate, protein, fat FROM Diet WHERE dDate = :dDate")
    fun selectNutrient(dDate: LocalDate): List<Nutrient>
//    @Query("UPDATE Workout SET workoutName = :workoutName, set = :height, count = :weight, mealCount = :mealCount, favWorkout = :favWorkout WHERE date = date")
//    fun updateWorkoutBydate(date: LocalDate, workoutName: String, height: Int, weight: Int, mealCount: Int, favWorkout: String, basicDiet: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(diet: Diet)

    @Delete
    fun delete(diet: Diet)

    @Query("DELETE FROM Diet")
    fun deleteAll()
}
