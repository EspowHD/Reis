package com.example.reis.other
//Used whenever attempting to access a Resource that may result in exception saves a lot of repeated lines of code
inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
    return try {
        action()
    } catch (e: Exception) {
        Resource.Error(e.message ?: "An unkown error occured")
    }
}