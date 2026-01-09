package com.tta.todolistainew.feature.settings.data

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for managing application settings.
 * Uses SharedPreferences for simple persistence.
 */
class SettingsRepository(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    private val _isDarkTheme = MutableStateFlow(sharedPreferences.getBoolean(KEY_DARK_THEME, false))
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _languageCode = MutableStateFlow(sharedPreferences.getString(KEY_LANGUAGE, "en") ?: "en")
    val languageCode: StateFlow<String> = _languageCode.asStateFlow()
    
    // Notification state is system managed, we just track if we *want* them typically, 
    // but here we might just check permission. For simple toggle state persistence:
    private val _notificationsEnabled = MutableStateFlow(sharedPreferences.getBoolean(KEY_NOTIFICATIONS, true))
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    fun setDarkTheme(isDark: Boolean) {
        sharedPreferences.edit { putBoolean(KEY_DARK_THEME, isDark) }
        _isDarkTheme.value = isDark
    }

    fun setLanguage(code: String) {
        sharedPreferences.edit { putString(KEY_LANGUAGE, code) }
        _languageCode.value = code
    }
    
    fun setNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit { putBoolean(KEY_NOTIFICATIONS, enabled) }
        _notificationsEnabled.value = enabled
    }

    companion object {
        private const val KEY_DARK_THEME = "dark_theme"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_NOTIFICATIONS = "notifications"
    }
}
