package com.example.chatx.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.chatx.model.data.ChatItem
import com.example.chatx.model.data.LastMessage
import com.example.chatx.model.data.User
import com.example.chatx.model.data.UserInfo
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

class HomeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    var chatList = mutableStateListOf<ChatItem>()
        private set
    private val _allUsers = MutableStateFlow<List<UserInfo>>(emptyList())
    val allUsers: StateFlow<List<UserInfo>> = _allUsers

    init {
        fetchChats()
        fetchAllUsers()
    }
    fun fetchAllUsers() {
        Firebase.firestore.collection("users")
            .get()
            .addOnSuccessListener { result ->
                val users = result.documents.mapNotNull { it.toObject(UserInfo::class.java) }
                _allUsers.value = users
            }
    }
    private fun fetchChats() {
        val currentUser = auth.currentUser ?: return

        db.collection("chats")
            .whereArrayContains("participants", currentUser.uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                chatList.clear()

                for (doc in snapshot.documents) {
                    val lastMsg = doc.get("lastMessage") as? Map<*, *> ?: continue
                    val otherUserId = (doc.get("participants") as List<*>)
                        .first { it != currentUser.uid }.toString()

                    val message = LastMessage(
                        text = lastMsg["text"].toString(),
                        timestamp = lastMsg["timestamp"] as? com.google.firebase.Timestamp ?: Timestamp.now(),
                        senderId = lastMsg["senderId"].toString()
                    )

                    db.collection("users").document(otherUserId)
                        .get()
                        .addOnSuccessListener { userDoc ->
                            val user = UserInfo(
                                uid = userDoc.id,
                                name = userDoc.getString("name") ?: "",
                                profileImageUrl = userDoc.getString("profileImageUrl") ?: ""
                            )
                            chatList.add(ChatItem(user, message))
                        }
                }
            }
    }
}
