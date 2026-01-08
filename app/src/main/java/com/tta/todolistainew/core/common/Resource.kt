package com.tta.todolistainew.core.common

/**
 * A sealed class representing the state of an asynchronous operation.
 * Used to handle Loading, Success, and Error states in a type-safe manner.
 *
 * @param T The type of data in case of success
 */
sealed class Resource<out T> {
    
    /**
     * Represents a loading state while data is being fetched or processed.
     */
    data object Loading : Resource<Nothing>()
    
    /**
     * Represents a successful state containing the fetched data.
     *
     * @param data The successfully fetched data
     */
    data class Success<T>(val data: T) : Resource<T>()
    
    /**
     * Represents an error state containing error information.
     *
     * @param message Human-readable error message
     * @param throwable Optional exception that caused the error
     */
    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : Resource<Nothing>()
    
    /**
     * Returns the data if this is a Success, null otherwise.
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    /**
     * Returns true if this is a Success state.
     */
    val isSuccess: Boolean get() = this is Success
    
    /**
     * Returns true if this is a Loading state.
     */
    val isLoading: Boolean get() = this is Loading
    
    /**
     * Returns true if this is an Error state.
     */
    val isError: Boolean get() = this is Error
    
    /**
     * Maps the data of a Success to another type using the provided transform function.
     */
    fun <R> map(transform: (T) -> R): Resource<R> = when (this) {
        is Loading -> Loading
        is Success -> Success(transform(data))
        is Error -> Error(message, throwable)
    }
}
