package com.tinytools.files.filesystem

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.tinytools.files.R

enum class LibraryFile {
    Recents {
        override fun getName() = R.string.library_recents

        override fun getIcon() = R.drawable.ic_drawer_recents
    }, Images {
        override fun getName() = R.string.library_images

        override fun getIcon() = R.drawable.ic_drawer_images
    }, Video {
        override fun getName() = R.string.library_video

        override fun getIcon() = R.drawable.ic_drawer_video
    }, Audio {
        override fun getName() = R.string.library_audio

        override fun getIcon() = R.drawable.ic_drawer_audio
    }, Documents {
        override fun getName() = R.string.library_documents

        override fun getIcon() = R.drawable.ic_drawer_docs
    }, Apps {
        override fun getName() = R.string.library_apps

        override fun getIcon() = R.drawable.ic_drawer_apps
    }, Archives {
        override fun getName() = R.string.library_archives

        override fun getIcon() = R.drawable.ic_drawer_archives
    };

    @StringRes abstract fun getName(): Int
    @DrawableRes abstract fun getIcon(): Int
}
