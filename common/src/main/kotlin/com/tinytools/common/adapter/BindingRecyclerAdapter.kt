package com.tinytools.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BindingRecyclerAdapter<ITEM : Any, VIEWHOLDER : BindingViewHolder<ITEM, *>>(val onClick: BindingViewHolderHandler) :
    RecyclerView.Adapter<VIEWHOLDER>() {
    protected val items = mutableListOf<ITEM>()

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VIEWHOLDER, position: Int) =
        holder.bind(items[position], onClick)

    fun swapData(newItems: List<ITEM>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}

inline fun <ITEM, reified VIEW_BINDING : ViewBinding, reified T : BindingViewHolder<ITEM, VIEW_BINDING>> ViewGroup.create(
    createHolder: (VIEW_BINDING) -> T,
    inflateFun: (LayoutInflater, ViewGroup, Boolean) -> VIEW_BINDING
): T {
    val inflater = LayoutInflater.from(context)
    return createHolder(inflateFun(inflater, this, false))
}
