package com.tinytools.files.data.ui

import androidx.annotation.DrawableRes
import com.tinytools.files.filesystem.HybridFile

sealed class HybridFileItem(val name: String, val icon: Icon, val size: String, val file: HybridFile, val permissions: String) {
    class HybridFileLinearItem(name: String, icon: Icon, size: String, file: HybridFile, permissions: String) : HybridFileItem(name, icon, size, file, permissions)
    class HybridFileGridItem(name: String, icon: Icon, size: String, file: HybridFile, permissions: String) : HybridFileItem(name, icon, size, file, permissions)

    fun areTheSame(other: Any): Boolean{
        return when(other){
            is HybridFileItem -> file.path == other.file.path
            else -> false
        }
    }

    fun areContentsTheSame(other: Any): Boolean {
        return when(other){
            is HybridFileItem -> name == other.name && icon == other.icon && size == other.size && file == other.file && permissions == other.permissions
            else -> false
        }
    }


}

data class Icon(@DrawableRes val resource: Int = 0, val path: String = "")
