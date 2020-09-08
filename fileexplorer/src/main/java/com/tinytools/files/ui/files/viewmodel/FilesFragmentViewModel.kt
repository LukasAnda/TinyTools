package com.tinytools.files.ui.files.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tinytools.common.viewmodel.BaseViewModel
import com.tinytools.common.views.DrawerView
import com.tinytools.files.filesystem.HybridFile
import com.tinytools.files.filesystem.getStorageDirectories
import com.tinytools.files.model.ui.HybridFileItem
import com.tinytools.files.model.ui.Icon
import com.tinytools.files.model.ui.PageStyle
import com.tinytools.files.model.ui.StorageDirectory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FilesFragmentViewModel(application: Application) : BaseViewModel(application) {
    private val pageItems = MutableLiveData<List<HybridFileItem>>()
    private var currentDirectory: HybridFile? = null
    private val currentPageStyle = MutableLiveData<PageStyle>()

    private val _drawerConfiguration = MutableLiveData<DrawerView.Configuration>()

    fun pageItems(): LiveData<List<HybridFileItem>> = pageItems

    fun listFiles(directory: StorageDirectory) {
        listFiles(HybridFile(directory.path).getTypedFile(context))
    }

    fun listFiles(directory: HybridFile) {
        currentDirectory = directory
        viewModelScope.launch(Dispatchers.IO) {
            // Todo load preferred directory style grid/linear
            currentPageStyle.postValue(PageStyle.List)
            val files = directory.listFiles(context, true).map { HybridFileItem.HybridFileLinearItem(it.name(context), Icon(), it.readableSize(context), it.getTypedFile(context), "") }
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

        val configuration = DrawerView.Configuration(listOf(
                DrawerView.Category("Storages", storageItems, true)
        ), null)

        _drawerConfiguration.postValue(configuration)
    }

    fun pageStyle(): LiveData<PageStyle> = currentPageStyle
}
