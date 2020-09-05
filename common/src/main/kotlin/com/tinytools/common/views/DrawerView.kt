package com.tinytools.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.tinytools.common.databinding.DrawerCategoryItemBinding
import com.tinytools.common.databinding.DrawerItemBinding
import com.tinytools.common.databinding.DrawerViewBinding
import com.tinytools.common.recyclical.datasource.dataSourceOf
import com.tinytools.common.recyclical.setup
import com.tinytools.common.recyclical.withItem

class DrawerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayoutCompat(context, attrs, defStyleAttr) {
    private val viewBinding = DrawerViewBinding.inflate(LayoutInflater.from(context), this, true)
    private val datasource = dataSourceOf()

    init {
        bindViews()
    }

    private fun bindViews() {
        viewBinding.recycler.setup {
            withDataSource(datasource)
            withLayoutManager(LinearLayoutManager(context))
            withItem<Item, DrawerItemBinding>(DrawerItemBinding::inflate) {
                onBind { binding, index, item ->
                    binding.name.text = item.name
                }
            }

            withItem<Category, DrawerCategoryItemBinding>(DrawerCategoryItemBinding::inflate) {
                onBind { binding, index, item ->
                    binding.name.text = item.name
                }

                onClick { index ->
                    item.items.forEachIndexed { listIndex, item ->
                        if (datasource.contains(item)) {
                            datasource.remove(item)
                        } else {
                            datasource.insert(index + listIndex + 1, item)
                        }
                    }
                }
            }
        }

        datasource.add(
                Category("Storages", listOf(Item("Main", 0), Item("Secondary", 0))),
                Category("Folders", listOf(
                        Item("Downloads", 0),
                        Item("Images", 0),
                        Item("Videos", 0),
                        Item("Music", 0)
                ))
        )
    }

    data class Item(val name: String, @DrawableRes val icon: Int)
    data class Category(val name: String, val items: List<Item>)
}

