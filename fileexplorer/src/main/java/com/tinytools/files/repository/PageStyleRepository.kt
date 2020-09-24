package com.tinytools.files.repository

import com.tinytools.files.data.db.dao.PageStyleDao
import com.tinytools.files.data.db.model.PageStyle
import com.tinytools.files.data.db.model.SortOrder
import com.tinytools.files.data.db.model.SortType
import com.tinytools.files.data.db.model.ViewStyle
import com.tinytools.files.data.mapper.DbPageStyleToUIMapper
import com.tinytools.files.data.ui.Page
import com.tinytools.files.data.ui.PageSortOrder
import com.tinytools.files.data.ui.PageSortType
import com.tinytools.files.data.ui.PageViewStyle

class PageStyleRepository(private val dao: PageStyleDao) {

    suspend fun getPage(path: String) = getPageStyle(path).let { DbPageStyleToUIMapper().map(it) }

    private suspend fun getPageStyle(path: String): PageStyle {
        var style = dao.getPageStyle(path)

        if (style == null) {
            style = PageStyle(path, ViewStyle.List, SortType.Name, SortOrder.Ascending)
            dao.insert(style)
        }

        return style
    }

    suspend fun setViewStyle(path: String, newViewStyle: PageViewStyle) {
        val style = getPageStyle(path).apply {
            viewStyle = ViewStyle.values()[newViewStyle.ordinal]
        }

        dao.update(style)
    }

    suspend fun setSortType(path: String, newSortType: PageSortType) {
        val style = getPageStyle(path).apply {
            sortType = SortType.values()[newSortType.ordinal]
        }

        dao.update(style)
    }

    suspend fun setSortOrder(path: String, newSortOrder: PageSortOrder) {
        val style = getPageStyle(path).apply {
            sortOrder = SortOrder.values()[newSortOrder.ordinal]
        }

        dao.update(style)
    }
}
