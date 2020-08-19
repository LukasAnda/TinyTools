package com.tinytools.files.filesystem

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream

data class LocalFile(override var path: String) : HybridFile(path) {
    override fun lastModified() = File(path).lastModified()

    override fun size(context: Context) = File(path).length()

    override fun name(context: Context) = path.substringAfterLast("/")

    override fun parent(context: Context) = File(path).parent

    override fun isDirectory(context: Context) = File(path).isDirectory

    override fun usableSpace() = File(path).usableSpace

    override fun totalSpace(context: Context) = File(path).totalSpace

    override suspend fun listFiles(context: Context, showHidden: Boolean): List<HybridFile> = File(path)
            .takeIf {
                it.exists() && it.isDirectory
            } // We skip the code if we can not list files using java api
            ?.listFiles() // List file
            ?.filter { showHidden || !it.isHidden } // Filter all if OR ShowHidden OR file is explicitely NOT hidden, (therefore filter not hidden files)
            ?.map { HybridFile(it.path) } // Parse it to Hybrid file
            ?.map { it.getTypedFile(context) } // Get specific file type
            ?: emptyList()

    override fun inputStream(context: Context) = runCatching { FileInputStream(path) }.getOrNull()

    override fun outputStream(context: Context) = runCatching { FileUtil.getOutputStream(File(path), context) }.getOrNull()

    override fun exists(context: Context) = File(path).exists()

    override fun setLastModified(lastModified: Long) = File(path).setLastModified(lastModified)

    override fun mkdirs(context: Context) = FileUtil.mkdir(File(path), context)

    override fun delete(context: Context) = FileUtil.deleteFile(File(path), context)
}
