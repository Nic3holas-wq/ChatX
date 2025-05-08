package com.example.chatx.model.data

import com.google.firebase.Timestamp

data class UserInfo(
    val uid: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val profileImageUrl: String = ""
)

data class LastMessage(
    val text: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val senderId: String = ""
)

data class ChatItem(
    val user: UserInfo,
    val lastMessage: LastMessage
)

