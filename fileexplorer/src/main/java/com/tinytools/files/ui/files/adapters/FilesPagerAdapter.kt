package com.tinytools.files.ui.files.adapters

import android.view.ViewGroup
import com.tinytools.common.adapter.BindingRecyclerAdapter
import com.tinytools.common.adapter.BindingViewHolder
import com.tinytools.common.adapter.BindingViewHolderHandler
import com.tinytools.common.adapter.create
import com.tinytools.files.databinding.FilesPageItemBinding
import com.tinytools.files.filesystem.HybridFile
import com.tinytools.files.model.ui.HybridFileItem
import com.tinytools.files.model.ui.PageConfig
import com.tinytools.files.model.ui.PageStyle
import java.io.File

class FilesPagerAdapter(handler: FilesPageAdapterHandler): BindingRecyclerAdapter<PageConfig, FilesPagerAdapter.PageHolder>(handler){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageHolder = parent.create(::PageHolder, FilesPageItemBinding::inflate)

    inner class PageHolder(binding: FilesPageItemBinding): BindingViewHolder<PageConfig, FilesPageItemBinding>(binding){
        override fun bind(item: PageConfig, handler: BindingViewHolderHandler) {

            binding.recycler.layoutManager = item.layoutManager
            binding.recycler.adapter = item.adapter
        }
    }

    fun swapPageStyle(page: Int){
        items[page].apply {
            when(pageStyle){
                PageStyle.List -> {
                    layoutManager.spanCount = 3
                    pageStyle = PageStyle.Grid
                }
                PageStyle.Grid -> {
                    layoutManager.spanCount = 1
                    pageStyle = PageStyle.List
                }
            }
            items[page].adapter.notifyItemRangeChanged(0, items[page].adapter.itemCount)
        }
    }

    fun swapPageData(page: Int, newItems: List<HybridFileItem>){
        items[page].adapter.swapData(newItems)
    }

    interface FilesPageAdapterHandler: BindingViewHolderHandler
}
