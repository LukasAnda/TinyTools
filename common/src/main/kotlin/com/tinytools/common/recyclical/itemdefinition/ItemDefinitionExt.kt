/**
 * Designed and developed by Aidan Follestad (@afollestad)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("UNCHECKED_CAST")

package com.tinytools.common.recyclical.itemdefinition

import android.view.View
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY
import androidx.viewbinding.ViewBinding
import com.tinytools.common.R
import com.tinytools.common.recyclical.BindingViewHolder
import com.tinytools.common.recyclical.ChildViewClickListener
import com.tinytools.common.recyclical.ItemDefinition
import com.tinytools.common.recyclical.ViewBinder
import com.tinytools.common.recyclical.ViewHolder
import com.tinytools.common.recyclical.datasource.DataSource
import com.tinytools.common.recyclical.datasource.SelectableDataSource
import com.tinytools.common.recyclical.internal.makeBackgroundSelectable
import com.tinytools.common.recyclical.internal.onClickDebounced
import com.tinytools.common.recyclical.viewholder.NoSelectionStateProvider
import com.tinytools.common.recyclical.viewholder.RealSelectionStateProvider
import com.tinytools.common.recyclical.viewholder.SelectionStateProvider

internal inline fun <reified VB : ViewBinding> ItemDefinition<*, *>.createViewHolder(itemBinding: VB): BindingViewHolder<VB> {
    val realDefinition = realDefinition()
    val setup = realDefinition.setup

    if (realDefinition.itemOnClick != null || setup.globalOnClick != null) {
        itemBinding.root.setOnClickListener(realDefinition.viewClickListener)
        itemBinding.root.makeBackgroundSelectable()
    }
    if (realDefinition.itemOnLongClick != null || setup.globalOnLongClick != null) {
        itemBinding.root.setOnLongClickListener(realDefinition.viewLongClickListener)
        itemBinding.root.makeBackgroundSelectable()
    }
    return BindingViewHolder(itemBinding)
            .also {
                setChildClickListeners(it.binding)
            }
}

private inline fun <reified VB : ViewBinding> ItemDefinition<*, *>.setChildClickListeners(viewBinding: VB) {
    val realDefinition = realDefinition()
    if (realDefinition.childClickDataList.isEmpty()) {
        return
    }

    val clickDatas = realDefinition.childClickDataList.filter {
        it.viewBindingType == VB::class.java
    }
    for (item in clickDatas) {
        val viewGetter = item.child as ((ViewBinding) -> View)
        val callback = item.callback as (SelectionStateProvider<Any>.(Int, Any) -> Unit)
        val childView = viewGetter(viewBinding)

        childView.onClickDebounced { child ->
            val index = viewBinding.root.viewHolder().adapterPosition
            getSelectionStateProvider(index).use {
                callback(it, index, child)
            }
        }
    }
}

internal fun <VB : ViewBinding> ItemDefinition<*, VB>.bindViewHolder(
        viewHolder: ViewHolder,
        item: Any,
        position: Int
) {
    val realDefinition = realDefinition()
    viewHolder.itemView.run {
        setTag(R.id.rec_view_item_view_holder, viewHolder)
        setTag(R.id.rec_view_item_selectable_data_source, realDefinition.currentDataSource)
    }

    val viewHolderBinder = realDefinition.binder as? ViewBinder<Any, VB>
    val holder = viewHolder as? BindingViewHolder<VB> ?: return
    viewHolderBinder?.invoke(holder, holder.binding, position, item)

    // Make sure we cleanup this reference, the data source shouldn't be held onto in views
    viewHolder.itemView.setTag(R.id.rec_view_item_selectable_data_source, null)
}

internal fun ItemDefinition<*, *>.recycleViewHolder(viewHolder: ViewHolder) {
    val realDefinition = realDefinition()
    realDefinition.onRecycled?.invoke(viewHolder)
}

internal fun <IT : Any, VB : ViewBinding> ItemDefinition<IT, VB>.getSelectionStateProvider(
        position: Int
): SelectionStateProvider<IT> {
    val selectableSource = getDataSource<SelectableDataSource<*>, VB>()
    return if (selectableSource != null) {
        RealSelectionStateProvider(selectableSource, position)
    } else {
        NoSelectionStateProvider(getDataSource(), position)
    }
}

internal fun View.viewHolder(): ViewHolder {
    return getTag(R.id.rec_view_item_view_holder) as? ViewHolder ?: error(
            "Didn't find view holder in itemView tag."
    )
}

@RestrictTo(LIBRARY)
fun <VB : ViewBinding> ItemDefinition<*, VB>.realDefinition(): RealItemDefinition<*, VB> {
    return this as? RealItemDefinition<*, VB> ?: error("$this is not a RealItemDefinition")
}

/**
 * Sets a callback that's invoked when a child view in a item is clicked.
 *
 * @param view A lambda that provides the view we are attaching to in each view holder.
 * @param block A lambda executed when the view is clicked.
 */
inline fun <IT : Any, VT : View, reified VB : ViewBinding> ItemDefinition<IT, VB>.onChildViewClick(
        noinline view: VB.() -> VT,
        noinline block: ChildViewClickListener<IT, VT>
): ItemDefinition<IT, VB> {
    realDefinition().childClickDataList.add(
            RealItemDefinition.ChildClickData(
                    viewBindingType = VB::class.java,
                    child = view,
                    callback = block
            )
    )
    return this
}

/** Gets the current data source, auto casting to the type [T]. */
inline fun <reified T : DataSource<*>, VB : ViewBinding> ItemDefinition<*, VB>.getDataSource(): T? {
    return if (this is RealItemDefinition) {
        currentDataSource as? T
    } else {
        error("$this is not a RealItemDefinition")
    }
}
