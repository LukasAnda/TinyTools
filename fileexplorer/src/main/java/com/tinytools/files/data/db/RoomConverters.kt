package com.tinytools.files.data.db

import androidx.room.TypeConverter
import com.tinytools.files.data.db.model.SortOrder
import com.tinytools.files.data.db.model.SortType
import com.tinytools.files.data.db.model.ViewStyle

object RoomConverters {

    @TypeConverter
    @JvmStatic fun viewStyleToInt(value: ViewStyle) = value.toInt()
    @TypeConverter
    @JvmStatic fun intToViewStyle(value: Int) = value.toEnum<ViewStyle>()

    @TypeConverter
    @JvmStatic fun sortTypeToInt(value: SortType) = value.toInt()
    @TypeConverter
    @JvmStatic fun intToSortType(value: Int) = value.toEnum<SortType>()

    @TypeConverter
    @JvmStatic fun sortOrderToInt(value: SortOrder) = value.toInt()
    @TypeConverter
    @JvmStatic fun intToSortOrder(value: Int) = value.toEnum<SortOrder>()
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T : Enum<T>> T.toInt(): Int = this.ordinal

inline fun <reified T : Enum<T>> Int.toEnum(): T = enumValues<T>()[this]
