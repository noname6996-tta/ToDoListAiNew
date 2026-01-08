package com.tta.todolistainew.feature.auth.ui

/**
 * UI State for the Login screen.
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null
) {
    companion object {
        val Initial = LoginUiState()
        val Loading = LoginUiState(isLoading = true)
        val LoggedIn = LoginUiState(isLoggedIn = true)
        
        fun error(message: String) = LoginUiState(errorMessage = message)
    }
}
