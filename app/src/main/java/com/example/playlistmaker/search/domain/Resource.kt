package com.example.playlistmaker.search.domain

sealed class Resource<T>(
    val value: T? = null,
    val message: String? = null
) {
    class Success<T>(value: T) : Resource<T>(value)
    class Error<T>(message: String, value: T? = null) : Resource<T>(value, message)
}
