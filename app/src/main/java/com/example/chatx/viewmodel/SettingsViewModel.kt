package com.example.chatx.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatx.model.data.AppDatabase
import com.example.chatx.model.data.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application): AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).settingsDao()
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode
    init {
        viewModelScope.launch{
            val settings = dao.getSettings()
            _isDarkMode.value = settings?.isDarkMode ?: false
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        viewModelScope.launch{
            dao.saveSettings(Settings(isDarkMode = enabled))
        }
    }

}