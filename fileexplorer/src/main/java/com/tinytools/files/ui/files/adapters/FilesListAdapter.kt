package com.tinytools.files.ui.files.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewbinding.ViewBinding
import com.tinytools.common.adapter.BindingRecyclerAdapter
import com.tinytools.common.adapter.BindingViewHolder
import com.tinytools.common.adapter.BindingViewHolderHandler
import com.tinytools.common.adapter.create
import com.tinytools.files.databinding.FilesItemGridBinding
import com.tinytools.files.databinding.FilesItemLinearBinding
import com.tinytools.files.filesystem.HybridFile
import com.tinytools.files.model.ui.HybridFileItem
import com.tinytools.files.ui.files.adapters.FilesListAdapter.FilesItemHolder.FilesGridHolder
import com.tinytools.files.ui.files.adapters.FilesListAdapter.FilesItemHolder.FilesLinearHolder
import com.tinytools.files.ui.files.adapters.FilesListAdapter.ViewType.*

class FilesListAdapter(private val layoutManager: GridLayoutManager, handler: FilesListAdapterHandler) : BindingRecyclerAdapter<HybridFileItem, FilesListAdapter.FilesItemHolder>(handler) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType.toViewType()) {
        LINEAR -> parent.create(::FilesLinearHolder, FilesItemLinearBinding::inflate)
        GRID -> parent.create(::FilesGridHolder, FilesItemGridBinding::inflate)
    }

    override fun getItemViewType(position: Int) =
            if (layoutManager.spanCount == 1) {
                LINEAR.ordinal
            } else {
                GRID.ordinal
            }

    enum class ViewType {
        LINEAR, GRID;
    }

    private fun Int.toViewType() = values()[this]

    sealed class FilesItemHolder(binding: ViewBinding) : BindingViewHolder<HybridFileItem, ViewBinding>(binding) {
        class FilesLinearHolder(binding: ViewBinding) : FilesItemHolder(binding) {
            override fun bind(item: HybridFileItem, handler: BindingViewHolderHandler) {
                (binding as? FilesItemLinearBinding)?.let {
                    binding.text.text = item.name
                    binding.size.text = item.size
                    binding.root.setOnClickListener {
                        (handler as? FilesListAdapterHandler)?.onFileSelected(item)
                    }
                }
            }

        }

        class FilesGridHolder(binding: ViewBinding) : FilesItemHolder(binding) {
            override fun bind(item: HybridFileItem, handler: BindingViewHolderHandler) {
                (binding as? FilesItemGridBinding)?.let {
                    binding.text.text = item.name
                    binding.root.setOnClickListener {
                        (handler as? FilesListAdapterHandler)?.onFileSelected(item)
                    }
                }
            }

        }
    }

    interface FilesListAdapterHandler : BindingViewHolderHandler {
        fun onFileSelected(path: HybridFileItem)
    }
}
