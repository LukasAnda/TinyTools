package com.tinytools.files.data.ui

import androidx.annotation.Keep

@Keep
data class Page(val path: String, var viewStyle: PageViewStyle, var sortType: PageSortType, var sortOrder: PageSortOrder)

enum class PageViewStyle {
    List, Grid
}

enum class PageSortType {
    Name, Date, Size
}

enum class PageSortOrder {
    Ascending, Descending
}
