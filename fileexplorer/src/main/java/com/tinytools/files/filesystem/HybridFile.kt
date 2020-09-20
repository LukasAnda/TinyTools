package com.tinytools.files.filesystem

import android.content.Context
import com.tinytools.files.R
import com.tinytools.files.helpers.formatAsFileSize
import com.tinytools.files.model.ui.HybridFileItem
import com.tinytools.files.model.ui.Icon
import java.io.File
import java.io.InputStream
import java.io.OutputStream

open class HybridFile(open var path: String) {
    fun getTypedFile(context: Context): HybridFile {
        return when {
            FileUtil.isOnExtSdCard(File(path), context) -> LocalFile(path)
            else -> LocalFile(path)
        }
    }

    open fun lastModified(): Long = 0
    open suspend fun size(context: Context): Long = 0

    suspend fun readableSize(context: Context): String {
        val size = size(context)
        return if (isDirectory(context)) {
            context.resources.getQuantityString(R.plurals.folder_size, size.toInt(), size.toInt())
        } else {
            size.formatAsFileSize
        }
    }

    open fun name(context: Context): String = ""
    open fun parent(context: Context): String = ""
    open fun isDirectory(context: Context): Boolean = false
    open fun usableSpace(): Long = 0
    open fun totalSpace(context: Context): Long = 0
    open suspend fun listFiles(context: Context, showHidden: Boolean): List<HybridFile> = emptyList()
    open fun inputStream(context: Context): InputStream? = null
    open fun outputStream(context: Context): OutputStream? = null
    open fun exists(context: Context): Boolean = false
    open fun setLastModified(lastModified: Long): Boolean = false
    open fun mkdirs(context: Context): Boolean = false
    open fun delete(context: Context): Boolean = false

    fun getIcon(context: Context, showPreview: Boolean = false): Icon {
        // Todo add preview showing
        if(isDirectory(context)){
            return Icon(R.drawable.ic_folder)
        } else {
            return Icon(R.drawable.ic_file)
        }
    }
}
