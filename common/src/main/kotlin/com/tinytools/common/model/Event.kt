package com.lukasanda.nioba.base

// Event class, so we can propagate events happening inside activity. We don't need to post data on Success because we observe them as LiveData
sealed class Event {
    object Loading : Event()
    class Failure(val message: String) : Event()
    object Success : Event()
}
