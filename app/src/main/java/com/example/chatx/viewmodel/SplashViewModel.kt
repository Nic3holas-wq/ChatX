package com.example.chatx.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.chatx.model.data.AppDatabase

class SplashViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getInstance(application).userDao()

    suspend fun isUserLoggedIn(): Boolean {
        return userDao.getLoggedInUser() != null
    }
}
