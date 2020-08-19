package com.tinytools.files.ui.files.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tinytools.common.viewmodel.BaseViewModel
import com.tinytools.files.filesystem.HybridFile
import com.tinytools.files.filesystem.getStorageDirectories
import com.tinytools.files.model.ui.HybridFileItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class FilesFragmentViewModel(application: Application) : BaseViewModel(application) {
    private val pageItems = MutableLiveData<Pair<Int, List<HybridFileItem>>>()
    private val directories = mutableMapOf<Int, HybridFile>()

    fun pageCount() = 2
    fun pageItems(): LiveData<Pair<Int, List<HybridFileItem>>> = pageItems

    //TODO consider preferences
    fun savedDirectories() = (0 until pageCount())
            .map { getStorageDirectories(context).first() }
            .also { Log.d("TAG", it.first().path) }
            .map { HybridFile(it.path).getTypedFile(context) }

    fun listFiles(page: Int, directory: HybridFile) {
        directories[page] = directory
        viewModelScope.launch(Dispatchers.IO) {
            val file = File(directory.path)
            Log.d("TAG", "File: ${file.path}, isDirectory: ${file.isDirectory}, canRead: ${file.canRead()}, listFiles: ${file.listFiles()?.joinToString("\n")}}")
            val files = directory.listFiles(context, true).map { it.toVisualItem(context) }
            pageItems.postValue(Pair(page, files))
        }
    }

    fun directory(page: Int) = directories[page]
}
