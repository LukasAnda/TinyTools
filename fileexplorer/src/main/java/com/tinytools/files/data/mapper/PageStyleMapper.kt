package com.tinytools.files.data.mapper

import com.tinytools.files.data.db.model.PageStyle
import com.tinytools.files.data.ui.Page
import com.tinytools.files.data.ui.PageSortOrder
import com.tinytools.files.data.ui.PageSortType
import com.tinytools.files.data.ui.PageViewStyle

class DbPageStyleToUIMapper : Mapper<PageStyle, Page> {
    override fun map(from: PageStyle) = from.let {
        Page(
                it.path,
                PageViewStyle.values()[it.viewStyle.ordinal],
                PageSortType.values()[it.sortType.ordinal],
                PageSortOrder.values()[it.sortOrder.ordinal]
        )
    }
}
