package com.example.chatx.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.chatx.model.data.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.State

class UserViewModel : ViewModel() {

    private val _user = mutableStateOf<UserInfo?>(null)
    val user: State<UserInfo?> = _user

    init {
        fetchUserDetails()
    }

    private fun fetchUserDetails() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val user = document.toObject(UserInfo::class.java)
                        _user.value = user
                    }
                }
                .addOnFailureListener {
                    // Handle failure
                }
        }
    }
}
