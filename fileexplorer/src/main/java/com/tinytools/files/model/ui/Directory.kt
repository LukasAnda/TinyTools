package com.tinytools.files.model.ui

import com.tinytools.files.filesystem.LibraryFile

interface Directory

data class StorageDirectory(val path: String, val name: String, val icon: Int): Directory

data class LibraryDirectory(val name: String, val icon: Int, val type: LibraryFile): Directory
