package com.tta.todolistainew.feature.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tta.todolistainew.core.common.UiEvent
import com.tta.todolistainew.navigation.Route
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Login screen.
 * Handles Google Sign-In logic and navigation.
 */
class LoginViewModel : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    // One-time UI events
    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()
    
    /**
     * Called when Google Sign-In button is clicked.
     * For now, this navigates directly to the home screen.
     * TODO: Add actual Google Sign-In flow and API token validation.
     */
    fun onGoogleSignInClick() {
        viewModelScope.launch {
            // Show loading state
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            // Simulate a brief loading delay for UX
            delay(500)
            
            // TODO: Implement actual Google Sign-In here
            // For now, just navigate to home screen
            
            _uiState.update { LoginUiState.LoggedIn }
            _uiEvent.send(UiEvent.Navigate(Route.TaskList))
        }
    }
    
    /**
     * Check if user is already logged in.
     * TODO: Implement token validation API call.
     */
    fun checkLoginStatus() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // TODO: Call API to validate token
            // For now, assume user is not logged in
            delay(300)
            
            _uiState.update { LoginUiState.Initial }
        }
    }
    
    /**
     * Handle sign-in error.
     */
    fun onSignInError(message: String) {
        viewModelScope.launch {
            _uiState.update { LoginUiState.error(message) }
            _uiEvent.send(UiEvent.ShowSnackbar(message))
        }
    }
    
    /**
     * Factory for creating LoginViewModel.
     */
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
