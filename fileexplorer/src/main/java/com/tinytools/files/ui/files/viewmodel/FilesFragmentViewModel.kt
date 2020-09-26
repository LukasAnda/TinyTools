package com.tinytools.files.ui.files.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tinytools.common.viewmodel.BaseViewModel
import com.tinytools.common.views.DrawerView
import com.tinytools.files.R
import com.tinytools.files.data.ui.Directory
import com.tinytools.files.data.ui.HybridFileItem
import com.tinytools.files.data.ui.LibraryDirectory
import com.tinytools.files.data.ui.PageViewStyle
import com.tinytools.files.data.ui.StorageDirectory
import com.tinytools.files.filesystem.HybridFile
import com.tinytools.files.filesystem.LibraryFile
import com.tinytools.files.filesystem.LibraryFile.Apps
import com.tinytools.files.filesystem.LibraryFile.Archives
import com.tinytools.files.filesystem.LibraryFile.Audio
import com.tinytools.files.filesystem.LibraryFile.Documents
import com.tinytools.files.filesystem.LibraryFile.Images
import com.tinytools.files.filesystem.LibraryFile.Recents
import com.tinytools.files.filesystem.LibraryFile.Video
import com.tinytools.files.filesystem.getLibraryDirectories
import com.tinytools.files.filesystem.getStorageDirectories
import com.tinytools.files.filesystem.listApks
import com.tinytools.files.filesystem.listAudio
import com.tinytools.files.filesystem.listDocs
import com.tinytools.files.filesystem.listImages
import com.tinytools.files.filesystem.listVideo
import com.tinytools.files.helpers.getFileType
import com.tinytools.files.repository.PageStyleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FilesFragmentViewModel(application: Application, private val pageStyleRepository: PageStyleRepository) : BaseViewModel(application) {
    private val pageItems = MutableLiveData<List<HybridFileItem>>()
    private var currentDirectory: HybridFile? = null
    private val currentPageStyle = MutableLiveData<PageViewStyle>()

    private val _drawerConfiguration = MutableLiveData<DrawerView.Configuration>()

    private val _currentDirectory: MutableLiveData<Directory> = MutableLiveData<Directory>()

    var shouldShowIcon = true

    init {
        loadLastOpenedDirectory()
    }

    fun currentDirectory(): LiveData<Directory> = _currentDirectory

    private fun loadLastOpenedDirectory() {
        // TODO Consider loading from database
        _currentDirectory.postValue(getStorageDirectories(context).first())
    }

    fun changeDirectory(directory: Directory) {
        _currentDirectory.postValue(directory)
    }


    fun pageItems(): LiveData<List<HybridFileItem>> = pageItems

    fun listFiles(directory: Directory) {
        when (directory) {
            is StorageDirectory -> listFiles(HybridFile(directory.path).getTypedFile(context))
            is LibraryDirectory -> listFiles(directory.type)
        }
    }

    fun listFiles(directory: HybridFile) {
        pageItems.postValue(emptyList())
        currentDirectory = directory
        launchAsync {
            val style = pageStyleRepository.getPage(directory.path)
            currentPageStyle.postValue(style.viewStyle)
            val files = directory.listFiles(context, true)
            mapFilesToItems(files, style.viewStyle)
        }
    }

    private suspend fun mapFilesToItems(files: List<HybridFile>, style: PageViewStyle) {
        val items = when (style) {
            PageViewStyle.List -> files.map { HybridFileItem.HybridFileLinearItem(it.name(context), it.getFileType(context), it.readableSize(context), it.getTypedFile(context), "") }
            PageViewStyle.Grid -> files.map { HybridFileItem.HybridFileGridItem(it.name(context), it.getFileType(context), it.readableSize(context), it.getTypedFile(context), "") }
        }
        pageItems.postValue(items)
    }

    private fun listFiles(directory: LibraryFile) {
        pageItems.postValue(emptyList())
        currentDirectory = null
        launchAsync {
            val files = when (directory) {
                Recents -> emptyList()
                Images -> listImages(context)
                Video -> listVideo(context)
                Audio -> listAudio(context)
                Documents -> listDocs(context)
                Apps -> listApks(context)
                Archives -> emptyList()
            }.sortedByDescending { it.lastModified() }

            val style = pageStyleRepository.getPage(directory.name)
            currentPageStyle.postValue(style.viewStyle)

            mapFilesToItems(files, style.viewStyle)
        }
    }

    fun changeDirectoryStyle() {
        launchAsync {
            val items = pageItems.value ?: emptyList()
            val newStyle = when {
                items.any { it is HybridFileItem.HybridFileLinearItem } -> {
                    PageViewStyle.Grid
                }
                items.any { it is HybridFileItem.HybridFileGridItem } -> {
                    PageViewStyle.List
                }
                else -> error("Unknown item type")
            }
            pageStyleRepository.setViewStyle(currentDirectory?.path.orEmpty(), newStyle)
            mapFilesToItems(items.map { it.file }, newStyle)
            currentPageStyle.postValue(newStyle)
        }
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

    fun pageStyle(): LiveData<PageViewStyle> = currentPageStyle
}
