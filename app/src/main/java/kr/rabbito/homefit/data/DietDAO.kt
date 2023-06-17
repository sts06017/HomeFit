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

    @Query("SELECT dDate, SUM(calorie) AS totalCalorie FROM Diet WHERE dDate >= date(:selectedDate,'-5 days') GROUP BY dDate ORDER BY dDate LIMIT 6")
    suspend fun getCaloriesByDate(selectedDate: String): List<DietCalorie>

//    @Query("SELECT dDate FROM Diet WHERE dDate >= date(:selectedDate,'-5 days') GROUP BY dDate ORDER BY dDate LIMIT 6")
//    suspend fun getDates(selectedDate: String): List<LocalDate>
    @Query("SELECT * FROM Diet WHERE jsonHash = :jsonHash LIMIT 1")
    fun findByJsonHash(jsonHash: Int): Diet?

    @Query("SELECT carbohydrate, protein, fat FROM Diet WHERE dDate = :dDate")
    fun selectNutrient(dDate: LocalDate): List<Nutrient>

    @Query("UPDATE Diet SET foodName = :foodName, weight = :weight, calorie = :calorie, carbohydrate = :carbohydrate, protein = :protein, fat = :fat WHERE id = :id")
    suspend fun updateDietById(id: Int, foodName: String, weight: Double, calorie: Double, carbohydrate: Double, protein: Double, fat: Double)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(diet: Diet)

    @Delete
    fun delete(diet: Diet)

    @Query("DELETE FROM Diet")
    fun deleteAll()
}
