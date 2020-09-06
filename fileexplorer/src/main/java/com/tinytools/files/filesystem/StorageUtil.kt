package com.tinytools.files.filesystem

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Build.VERSION
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.text.TextUtils
import androidx.annotation.DrawableRes
import com.tinytools.files.R
import com.tinytools.files.model.ui.StorageDirectory
import java.io.File
import java.util.*

private const val DEFAULT_FALLBACK_STORAGE_PATH: String = "/storage/sdcard0"
private const val DIR_SEPARATOR = "/"

/** @return paths to all available volumes in the system (include emulated)
 */
@Synchronized
fun getStorageDirectories(context: Context): List<StorageDirectory> {
    val volumes = if (VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        getStorageDirectoriesNew(context)
    } else {
        getStorageDirectoriesLegacy(context)
    }
//    if (isRootExplorer()) {
//        volumes.add(
//                StorageDirectoryParcelable(
//                        "/",
//                        getResources().getString(R.string.root_directory),
//                        R.drawable.ic_drawer_root_white))
//    }
    return volumes
}

/**
 * @return All available storage volumes (including internal storage, SD-Cards and USB devices)
 */
@TargetApi(Build.VERSION_CODES.N)
@Synchronized
private fun getStorageDirectoriesNew(context: Context): List<StorageDirectory> {
    // Final set of paths
    val volumes: MutableList<StorageDirectory> = mutableListOf()
    val sm: StorageManager = context.getSystemService(StorageManager::class.java)
    for (volume in sm.storageVolumes) {
        if (!volume.state.equals(Environment.MEDIA_MOUNTED, ignoreCase = true)
                && !volume.state.equals(Environment.MEDIA_MOUNTED_READ_ONLY, ignoreCase = true)) {
            continue
        }
        val path: File = getVolumeDirectory(volume)
        val name = volume.getDescription(context)
        var icon: Int
        icon = if (!volume.isRemovable) {
            R.drawable.ic_internal_storage
        } else {
            // HACK: There is no reliable way to distinguish USB and SD external storage
            // However it is often enough to check for "USB" String
            if (name.toUpperCase().contains("USB") || path.path.toUpperCase().contains("USB")) {
//                R.drawable.ic_usb_white_24dp
                0
            } else {
                R.drawable.ic_sdcard
            }
        }
        volumes.add(StorageDirectory(path.path, name, icon))
    }
    return volumes
}

/**
 * Returns all available SD-Cards in the system (include emulated)
 *
 *
 * Warning: Hack! Based on Android source code of version 4.3 (API 18) Because there was no
 * standard way to get it before android N
 *
 * @return All available SD-Cards in the system (include emulated)
 */
@Synchronized
private fun getStorageDirectoriesLegacy(context: Context): List<StorageDirectory> {
    val rv: MutableList<String> = ArrayList()

    // Primary physical SD-CARD (not emulated)
    val rawExternalStorage = System.getenv("EXTERNAL_STORAGE")
    // All Secondary SD-CARDs (all exclude primary) separated by ":"
    val rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE")
    // Primary emulated SD-CARD
    val rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET")
    if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
        // Device has physical external storage; use plain paths.
        if (TextUtils.isEmpty(rawExternalStorage)) {
            // EXTERNAL_STORAGE undefined; falling back to default.
            // Check for actual existence of the directory before adding to list
            if (File(DEFAULT_FALLBACK_STORAGE_PATH).exists()) {
                rv.add(DEFAULT_FALLBACK_STORAGE_PATH)
            } else {
                // We know nothing else, use Environment's fallback
                rv.add(Environment.getExternalStorageDirectory().absolutePath)
            }
        } else {
            rv.add(rawExternalStorage)
        }
    } else {
        // Device has emulated storage; external storage paths should have
        // userId burned into them.
        val rawUserId: String
        val path = Environment.getExternalStorageDirectory().absolutePath
        val folders: List<String> = path.split(DIR_SEPARATOR)
        val lastFolder = folders[folders.size - 1]
        var isDigit = false
        try {
            Integer.valueOf(lastFolder)
            isDigit = true
        } catch (ignored: NumberFormatException) {
        }
        rawUserId = if (isDigit) lastFolder else ""
        // /storage/emulated/0[1,2,...]
        if (TextUtils.isEmpty(rawUserId)) {
            rv.add(rawEmulatedStorageTarget)
        } else {
            rv.add(rawEmulatedStorageTarget + File.separator + rawUserId)
        }
    }
    // Add all secondary storages
    if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
        // All Secondary SD-CARDs splited into array
        val rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator.toRegex()).toTypedArray()
        Collections.addAll(rv, *rawSecondaryStorages)
    }
    if (VERSION.SDK_INT >= Build.VERSION_CODES.M) rv.clear()
    val strings: Array<String> = FileUtil.getExtSdCardPaths(context)
    for (s in strings) {
        val f = File(s)
        if (!rv.contains(s) && FileUtil.canListFiles(f)) rv.add(s)
    }
//    val usb: File = getUsbDrive()
//    if (usb != null && !rv.contains(usb.path)) rv.add(usb.path)
//    if (SingletonUsbOtg.getInstance().isDeviceConnected()) {
//        rv.add(OTGUtil.PREFIX_OTG.toString() + "/")
//    }

    // Assign a label and icon to each directory
    val volumes: MutableList<StorageDirectory> = mutableListOf()
    for (file in rv) {
        val f = File(file)
        @DrawableRes val icon: Int = if ("/storage/emulated/legacy" == file || "/storage/emulated/0" == file || "/mnt/sdcard" == file) {
            R.drawable.ic_internal_storage
        } else if ("/storage/sdcard1" == file) {
            R.drawable.ic_sdcard
            0
        } else if ("/" == file) {
            R.drawable.ic_root
        } else {
            R.drawable.ic_sdcard
        }
        val name: String = getDeviceDescriptionLegacy(context, f)
        volumes.add(StorageDirectory(file, name, icon))
    }
    return volumes
}

@TargetApi(Build.VERSION_CODES.N)
private fun getVolumeDirectory(volume: StorageVolume): File {
    return try {
        val f = StorageVolume::class.java.getDeclaredField("mPath")
        f.isAccessible = true
        f[volume] as File
    } catch (e: Exception) {
        // This shouldn't fail, as mPath has been there in every version
        throw RuntimeException(e)
    }
}

private fun getDeviceDescriptionLegacy(context: Context, file: File): String {
    val path = file.path
    return when (path) {
        "/storage/emulated/legacy", "/storage/emulated/0", "/mnt/sdcard" -> context.getString(R.string.storage_internal)
        "/storage/sdcard", "/storage/sdcard1" -> context.getString(R.string.storage_sd_card)
        "/" -> context.getString(R.string.root_directory)
        else -> file.name
    }
}
