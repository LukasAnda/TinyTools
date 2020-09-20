package com.tinytools.files.helpers

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import coil.bitmap.BitmapPool
import coil.decode.DataSource
import coil.decode.Options
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.size.Size
import java.io.File


class ApkIconFetcher(private val context: Context) : Fetcher<File>{
    override fun key(data: File): String {
        return data.path
    }

    override fun handles(data: File) = data.name.endsWith(".apk")

    override suspend fun fetch(
            pool: BitmapPool,
            data: File,
            size: Size,
            options: Options
    ): FetchResult {
        return DrawableResult(
                drawable = getDrawable(data),
                isSampled = false,
                dataSource = DataSource.DISK
        )
    }

    private fun getDrawable(file: File): Drawable{
        val packageInfo = context.packageManager.getPackageArchiveInfo(file.path, PackageManager.GET_ACTIVITIES)
        if (packageInfo != null) {
            val appInfo = packageInfo.applicationInfo
            appInfo.sourceDir = file.path
            appInfo.publicSourceDir = file.path
            return appInfo.loadIcon(context.packageManager)
        }
        error("Failed to get icon")
    }
}
