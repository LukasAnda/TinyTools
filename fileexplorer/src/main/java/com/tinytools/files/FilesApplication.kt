package com.tinytools.files

import coil.Coil
import coil.ImageLoader
import coil.fetch.VideoFrameFileFetcher
import coil.fetch.VideoFrameUriFetcher
import com.tinytools.common.TinyApplication
import com.tinytools.files.di.dbModule
import com.tinytools.files.di.repositoryModule
import com.tinytools.files.di.viewModelsModule
import com.tinytools.files.helpers.ApkIconFetcher
import org.koin.core.module.Module

class FilesApplication : TinyApplication() {
    override val modules: List<Module>
        get() = mutableListOf(viewModelsModule, dbModule, repositoryModule)

    override fun onCreate() {
        super.onCreate()

        val imageLoader = ImageLoader.Builder(this)
                .componentRegistry {
                    add(VideoFrameFileFetcher(this@FilesApplication))
                    add(VideoFrameUriFetcher(this@FilesApplication))
                    add(ApkIconFetcher(this@FilesApplication))
                }
                .build()
        Coil.setImageLoader(imageLoader)
    }
}
