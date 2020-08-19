package com.tinytools.files.model.ui

import androidx.recyclerview.widget.GridLayoutManager
import com.tinytools.files.filesystem.HybridFile
import com.tinytools.files.ui.files.adapters.FilesListAdapter

data class PageConfig(val layoutManager: GridLayoutManager, val adapter: FilesListAdapter, var pageStyle: PageStyle)

enum class PageStyle {
    List, Grid
}
