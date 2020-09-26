package com.tinytools.files.helpers

import android.content.Context
import android.media.MediaMetadataRetriever
import coil.fetch.VideoFrameFetcher
import java.io.File


val SUPPORTED_FILE_EXTENSIONS = arrayOf(".3gp", ".3gpp", ".mkv", ".mp4", ".ts", ".webm")

class VideoIconFetcher(context: Context) : VideoFrameFetcher<File>(context) {

    override fun key(data: File) = "${data.path}:${data.lastModified()}"

    override fun handles(data: File): Boolean {
        val fileName = data.name
        return SUPPORTED_FILE_EXTENSIONS.any { fileName.endsWith(it, true) }
    }

    override fun MediaMetadataRetriever.setDataSource(data: File) = setDataSource(data.path)
}
