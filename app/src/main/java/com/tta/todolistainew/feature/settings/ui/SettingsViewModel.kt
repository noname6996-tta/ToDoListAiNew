package com.tta.todolistainew.feature.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tta.todolistainew.feature.settings.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val isDarkTheme: StateFlow<Boolean> = settingsRepository.isDarkTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val languageCode: StateFlow<String> = settingsRepository.languageCode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")
        
    val notificationsEnabled: StateFlow<Boolean> = settingsRepository.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun toggleTheme(isDark: Boolean) {
        settingsRepository.setDarkTheme(isDark)
    }
    
    fun setLanguage(code: String) {
        settingsRepository.setLanguage(code)
    }
    
    fun toggleNotifications(enabled: Boolean) {
        settingsRepository.setNotificationsEnabled(enabled)
    }

    class Factory(private val settingsRepository: SettingsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                return SettingsViewModel(settingsRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
