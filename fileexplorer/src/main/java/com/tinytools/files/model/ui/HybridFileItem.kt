package com.tinytools.files.model.ui

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import com.tinytools.files.filesystem.HybridFile

data class HybridFileItem(val name: String, val icon: Icon, val size: String, val file: HybridFile, val permissions: String)

data class Icon(@DrawableRes val resource: Int = 0, val bitmap: Bitmap? = null)
