package kr.rabbito.homefit.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDAO {
    @Query("SELECT * FROM User ORDER BY id")
    fun getAll(): LiveData<List<User>>

    @Query("SELECT * FROM User WHERE id = :id")
    fun getByUserId(id: Long): LiveData<List<User>>

    @Query("UPDATE User SET userName = :userName, height = :height, weight = :weight")
    fun updateByUserId(id: Long, height: String, weight: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Delete
    fun delete(user: User)

    @Query("DELETE FROM User")
    fun deleteAll()
}