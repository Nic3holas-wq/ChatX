package com.example.chatx.model.data
import androidx.room.*

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE phone = :phone")
    suspend fun getUser(phone: String): User?

    @Query("DELETE FROM users")
    suspend fun clearAllUsers()

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getLoggedInUser(): User?

}
