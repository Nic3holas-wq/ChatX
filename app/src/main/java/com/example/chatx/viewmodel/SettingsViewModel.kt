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
    val _isFingerprintEnabled = MutableStateFlow(false)
    val isFingerprintEnabled: StateFlow<Boolean> = _isFingerprintEnabled

    init {
        viewModelScope.launch{
            val settings = dao.getSettings()
            _isDarkMode.value = settings?.isDarkMode ?: false
            _isFingerprintEnabled.value = settings?.isFingerprintEnabled ?: false
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        viewModelScope.launch{
            dao.saveSettings(Settings(isDarkMode = enabled))
        }
    }

    fun toggleFingerprint(enabled: Boolean) {
        _isFingerprintEnabled.value = enabled
        viewModelScope.launch {
            val current = dao.getSettings()
            if (current != null) {
                dao.saveSettings(
                    current.copy(isFingerprintEnabled = enabled)
                )
            } else {
                dao.saveSettings(
                    Settings(
                        isDarkMode = _isDarkMode.value,
                        isFingerprintEnabled = enabled
                    )
                )
            }
        }
    }


}