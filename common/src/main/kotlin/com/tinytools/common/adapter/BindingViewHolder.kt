package com.tinytools.common.adapter

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BindingViewHolder<ITEM, BINDING: ViewBinding>(protected val binding: BINDING): RecyclerView.ViewHolder(binding.root){
    abstract fun bind(item: ITEM, handler: BindingViewHolderHandler)
}

interface BindingViewHolderHandler
