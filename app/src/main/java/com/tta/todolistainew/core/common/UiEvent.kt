package com.tta.todolistainew.core.common

/**
 * A sealed class representing one-time UI events that should be consumed only once.
 * These events are typically used for navigation, showing snackbars, toasts, or other
 * side effects that shouldn't be repeated on configuration changes.
 */
sealed class UiEvent {
    
    /**
     * Event to navigate to a specific route.
     *
     * @param route The destination route to navigate to
     */
    data class Navigate(val route: Any) : UiEvent()
    
    /**
     * Event to navigate back to the previous screen.
     */
    data object NavigateBack : UiEvent()
    
    /**
     * Event to show a snackbar with a message.
     *
     * @param message The message to display in the snackbar
     * @param actionLabel Optional action label for the snackbar
     */
    data class ShowSnackbar(
        val message: String,
        val actionLabel: String? = null
    ) : UiEvent()
    
    /**
     * Event to show a toast message.
     *
     * @param message The message to display in the toast
     */
    data class ShowToast(val message: String) : UiEvent()
    
    /**
     * Event to show an error message.
     *
     * @param message The error message to display
     */
    data class ShowError(val message: String) : UiEvent()
}
