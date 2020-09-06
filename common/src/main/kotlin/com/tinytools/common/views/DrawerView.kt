package com.tinytools.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.tinytools.common.R
import com.tinytools.common.databinding.DrawerCategoryItemBinding
import com.tinytools.common.databinding.DrawerItemBinding
import com.tinytools.common.databinding.DrawerViewBinding
import com.tinytools.common.recyclical.datasource.dataSourceOf
import com.tinytools.common.recyclical.datasource.dataSourceTypedOf
import com.tinytools.common.recyclical.setup
import com.tinytools.common.recyclical.withItem

class DrawerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayoutCompat(context, attrs, defStyleAttr) {
    private val viewBinding = DrawerViewBinding.inflate(LayoutInflater.from(context), this, true)
    private val dataSource = dataSourceTypedOf<DrawerItem>()
    private var listener: DrawerHandler? = null

    init {
        bindViews()
    }

    private fun bindViews() {
        viewBinding.recycler.setup {
            withDataSource(dataSource)
            withLayoutManager(LinearLayoutManager(context))
            withItem<Item, DrawerItemBinding>(DrawerItemBinding::inflate) {
                onBind { binding, index, item ->
                    binding.name.text = item.name

                    if(item.icon != 0){
                        binding.icon.setImageResource(item.icon)
                    }
                }

                onClick {
                    listener?.onItemSelected(item)
                }
            }

            withItem<Category, DrawerCategoryItemBinding>(DrawerCategoryItemBinding::inflate) {
                onBind { binding, index, item ->
                    binding.name.text = item.name
                    postDelayed({
                        item.items.forEachIndexed { listIndex, listItem ->
                            if (!item.expanded) {
                                dataSource.remove(listItem)
                            } else {
                                dataSource.insert(index + listIndex + 1, listItem)
                            }
                        }
                    },100)

                    if(item.expanded){
                        binding.arrow.setImageResource(R.drawable.ic_down)
                    } else {
                        binding.arrow.setImageResource(R.drawable.ic_up)
                    }
                }

                onClick { index ->
//                    item.items.forEachIndexed { listIndex, listItem ->
//                        if (item.expanded) {
//                            dataSource.remove(listItem)
//                        } else {
//                            dataSource.insert(index + listIndex + 1, listItem)
//                        }
//                    }
                    item.expanded = !item.expanded

                    dataSource.invalidateAt(index)
                }
            }
        }
    }

    fun reloadConfiguration(configuration: Configuration){
        dataSource.addAll(configuration.items)
        listener = configuration.handler
    }

    interface DrawerHandler {
        fun onItemSelected(item: Item)
    }

    data class Configuration(val items: List<DrawerItem>, var handler: DrawerHandler?)

    data class Item(val name: String, @DrawableRes val icon: Int, val item: Any): DrawerItem
    data class Category(val name: String, val items: List<Item>, var expanded: Boolean): DrawerItem

    interface DrawerItem
}

