package com.tinytools.files.data.db.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "pageStyle")
data class PageStyle(@PrimaryKey(autoGenerate = false) val path: String, var viewStyle: ViewStyle, var sortType: SortType, var sortOrder: SortOrder)

enum class ViewStyle {
    List, Grid
}

enum class SortType {
    Name, Date, Size, Type
}

enum class SortOrder {
    Ascending, Descending
}
