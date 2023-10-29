package com.bangkit.storyappdicoding.data

sealed class ApiStatus<out R> private constructor() {
    data class Success<out T>(val data: T) : ApiStatus<T>()
    data class Error(val error: String, val message: String? = null) : ApiStatus<Nothing>()
    data object Loading : ApiStatus<Nothing>()
}