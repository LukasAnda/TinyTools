package com.tinytools.common.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tinytools.common.model.Event
import com.tinytools.common.model.LiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseViewModel (val context: Application): AndroidViewModel(context){
    private val _events = LiveEvent<Event>()

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        _events.postValue(Event.Failure(throwable.message.orEmpty()))
    }

    private val job = Dispatchers.IO + exceptionHandler

    fun launchAsync(func: suspend () -> Unit){
        viewModelScope.launch(job) {
            _events.postValue(Event.Loading)
            func()
            _events.postValue(Event.Success)
        }
    }

    fun events() = _events
}
