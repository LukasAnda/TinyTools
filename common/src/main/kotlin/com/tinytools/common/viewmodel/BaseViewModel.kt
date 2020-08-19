package com.tinytools.common.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

abstract class BaseViewModel (val context: Application): AndroidViewModel(context){
    fun launchAsync(func: suspend () -> Unit){
        viewModelScope.launch {
            func()
        }
    }
}
