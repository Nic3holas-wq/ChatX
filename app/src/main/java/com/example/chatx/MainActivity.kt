package com.example.chatx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chatx.ui.theme.ChatXTheme
import com.example.chatx.viewmodel.SettingsViewModel
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    val viewModel: SettingsViewModel by viewModels<SettingsViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {

            val isDarkMode by viewModel.isDarkMode.collectAsState()
            ChatXTheme(isDarkTheme = isDarkMode) {
                AppNavigator()
            }
        }
    }
}


