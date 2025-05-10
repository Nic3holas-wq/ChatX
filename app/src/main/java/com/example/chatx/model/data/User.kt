package com.example.chatx.model.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val phone: String,
    val name: String? = null,
    val profileImageUrl: String? = null,
    val isLoggedIn: Boolean = false
)

