package com.tinytools.files.ui.files.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tinytools.common.viewmodel.BaseViewModel
import com.tinytools.common.views.DrawerView
import com.tinytools.files.R
import com.tinytools.files.filesystem.HybridFile
import com.tinytools.files.filesystem.LibraryFile
import com.tinytools.files.filesystem.LibraryFile.*
import com.tinytools.files.filesystem.getLibraryDirectories
import com.tinytools.files.filesystem.getStorageDirectories
import com.tinytools.files.filesystem.listApks
import com.tinytools.files.filesystem.listAudio
import com.tinytools.files.filesystem.listImages
import com.tinytools.files.filesystem.listVideo
import com.tinytools.files.model.ui.Directory
import com.tinytools.files.model.ui.HybridFileItem
import com.tinytools.files.model.ui.Icon
import com.tinytools.files.model.ui.LibraryDirectory
import com.tinytools.files.model.ui.PageStyle
import com.tinytools.files.model.ui.StorageDirectory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FilesFragmentViewModel(application: Application) : BaseViewModel(application) {
    private val pageItems = MutableLiveData<List<HybridFileItem>>()
    private var currentDirectory: HybridFile? = null
    private val currentPageStyle = MutableLiveData<PageStyle>()

    private val _drawerConfiguration = MutableLiveData<DrawerView.Configuration>()

    private val _currentDirectory: MutableLiveData<Directory> = MutableLiveData<Directory>()

    init {
        loadLastOpenedDirectory()
    }

    fun currentDirectory(): LiveData<Directory> = _currentDirectory

    private fun loadLastOpenedDirectory() {
        // TODO Consider loading from database
        _currentDirectory.postValue(getStorageDirectories(context).first())
    }

    fun changeDirectory(directory: Directory){
        _currentDirectory.postValue(directory)
    }


    fun pageItems(): LiveData<List<HybridFileItem>> = pageItems

    fun listFiles(directory: Directory) {
        when(directory){
            is StorageDirectory -> listFiles(HybridFile(directory.path).getTypedFile(context))
            is LibraryDirectory -> listFiles(directory.type)
        }
    }

    fun listFiles(directory: HybridFile) {
        currentDirectory = directory
        viewModelScope.launch(Dispatchers.IO) {
            // Todo load preferred directory style grid/linear
            currentPageStyle.postValue(PageStyle.List)
            val files = directory.listFiles(context, true).map { HybridFileItem.HybridFileLinearItem(it.name(context), it.getIcon(context), it.readableSize(context), it.getTypedFile(context), "") }
            pageItems.postValue(files)
        }
    }

    private fun listFiles(directory: LibraryFile){
        currentDirectory = null
        viewModelScope.launch(Dispatchers.IO){
            val filesToProcess = when(directory){
                Recents -> emptyList()
                Images -> listImages(context)
                Video -> listVideo(context)
                Audio -> listAudio(context)
                Documents -> emptyList()
                Apps -> listApks(context)
                Archives -> emptyList()
            }
            currentPageStyle.postValue(PageStyle.List)
            val files = filesToProcess.map { HybridFileItem.HybridFileLinearItem(it.name(context), it.getIcon(context), it.readableSize(context), it.getTypedFile(context), "") }
            pageItems.postValue(files)
        }
    }

    fun changeDirectoryStyle() {
        // TODO add saving this style to database
        var items = pageItems.value ?: emptyList()
        items = when {
            items.any { it is HybridFileItem.HybridFileLinearItem } -> {
                currentPageStyle.postValue(PageStyle.Grid)
                items.map { HybridFileItem.HybridFileGridItem(it.name, it.icon, it.size, it.file, it.permissions) }
            }
            items.any { it is HybridFileItem.HybridFileGridItem } -> {
                currentPageStyle.postValue(PageStyle.List)
                items.map { HybridFileItem.HybridFileLinearItem(it.name, it.icon, it.size, it.file, it.permissions) }
            }
            else -> error("Unknown item type")
        }
        pageItems.postValue(items)
    }

    fun navigateUp() {
        val parentDirectoryPath = currentDirectory?.parent(context).orEmpty()
        listFiles(HybridFile(parentDirectoryPath).getTypedFile(context))
    }

    fun configuration(): LiveData<DrawerView.Configuration> = _drawerConfiguration

    fun getDrawerConfiguration() = viewModelScope.launch {
        val storageItems = getStorageDirectories(context).map { DrawerView.Item(it.name, it.icon, it) }
        val libraryItems = getLibraryDirectories(context).map { DrawerView.Item(it.name, it.icon, it) }
        val configuration = DrawerView.Configuration(listOf(
                DrawerView.Category(context.getString(R.string.storage), storageItems, true),
                DrawerView.Category(context.getString(R.string.library), libraryItems, true)
        ), null)

        _drawerConfiguration.postValue(configuration)
    }

    fun pageStyle(): LiveData<PageStyle> = currentPageStyle
}
