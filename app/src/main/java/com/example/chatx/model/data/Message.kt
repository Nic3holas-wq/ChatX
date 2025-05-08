package com.example.chatx.model.data

import com.google.firebase.Timestamp

data class Message(
    val id: String = "",
    val senderId: String = "",
    val content: String = "",
    val timestamp: Timestamp = Timestamp.now()
)

