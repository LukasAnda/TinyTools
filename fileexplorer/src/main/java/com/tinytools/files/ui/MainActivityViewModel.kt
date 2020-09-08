package com.tinytools.files.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tinytools.common.viewmodel.BaseViewModel
import com.tinytools.files.filesystem.getStorageDirectories
import com.tinytools.files.model.ui.StorageDirectory

class MainActivityViewModel(application: Application) : BaseViewModel(application) {
    private val _currentDirectory: MutableLiveData<StorageDirectory> = MutableLiveData<StorageDirectory>()

    init {
        loadLastOpenedDirectory()
    }

    fun currentDirectory(): LiveData<StorageDirectory> = _currentDirectory

    private fun loadLastOpenedDirectory() {
        // TODO Consider loading from database
        _currentDirectory.postValue(getStorageDirectories(context).first())
    }

    fun changeDirectory(directory: StorageDirectory){
        _currentDirectory.postValue(directory)
    }
}
