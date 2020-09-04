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
import androidx.annotation.VisibleForTesting
import androidx.viewbinding.ViewBinding
import com.tinytools.common.recyclical.ChildViewClickListener
import com.tinytools.common.recyclical.IdGetter
import com.tinytools.common.recyclical.ItemClickListener
import com.tinytools.common.recyclical.ItemDefinition
import com.tinytools.common.recyclical.RecycledCallback
import com.tinytools.common.recyclical.RecyclicalMarker
import com.tinytools.common.recyclical.RecyclicalSetup
import com.tinytools.common.recyclical.ViewHolder
import com.tinytools.common.recyclical.ViewHolderBinder
import com.tinytools.common.recyclical.datasource.DataSource
import com.tinytools.common.recyclical.internal.Debouncer
import com.tinytools.common.recyclical.viewholder.SelectionStateProvider

/** @author Aidan Follestad (@afollestad) */
@RecyclicalMarker
class RealItemDefinition<out IT : Any, VH : ViewHolder, VB : ViewBinding>(
  internal val setup: RecyclicalSetup,
  val itemClassName: String
) : ItemDefinition<IT, VH, VB> {

  /** The current data source set in setup. */
  val currentDataSource: DataSource<*>?
    get() = setup.currentDataSource

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  internal var itemOnClick: ItemClickListener<Any>? = null
  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  internal var itemOnLongClick: ItemClickListener<Any>? = null

  internal var creator: ((layoutBinding: VB) -> VH)? = null
  internal var binder: ViewHolderBinder<*, *>? = null
  internal var idGetter: IdGetter<Any>? = null
  internal var onRecycled: RecycledCallback<Any>? = null

  var childClickDataList = mutableListOf<ChildClickData<*, *, *>>()

  override fun onBind(
    viewHolderCreator: (layoutBinding: VB) -> VH,
    block: ViewHolderBinder<VH, IT>
  ): ItemDefinition<IT, VH, VB> {
    this.creator = viewHolderCreator
    this.binder = block
    return this
  }

  override fun onClick(block: ItemClickListener<IT>): ItemDefinition<IT, VH, VB> {
    this.itemOnClick = (block as SelectionStateProvider<Any>.(Int) -> Unit)
    return this
  }

  override fun onLongClick(block: ItemClickListener<IT>): ItemDefinition<IT, VH, VB> {
    this.itemOnLongClick = (block as SelectionStateProvider<Any>.(Int) -> Unit)
    return this
  }

  override fun hasStableIds(idGetter: IdGetter<IT>): ItemDefinition<IT, VH, VB> {
    this.idGetter = idGetter as IdGetter<Any>
    return this
  }

  override fun onRecycled(block: RecycledCallback<VH>) {
    this.onRecycled = block as RecycledCallback<Any>
  }

  internal val viewClickListener = View.OnClickListener { itemView ->
    if (Debouncer.canPerform(itemView)) {
      val position = itemView.viewHolder()
          .adapterPosition
      getSelectionStateProvider(position).use {
        this.itemOnClick?.invoke(it, position)
        setup.globalOnClick?.invoke(it, position)
      }
    }
  }

  internal val viewLongClickListener = View.OnLongClickListener { itemView ->
    if (Debouncer.canPerform(itemView)) {
      val position = itemView.viewHolder()
          .adapterPosition
      getSelectionStateProvider(position)
          .use {
            this.itemOnLongClick?.invoke(it, position)
            setup.globalOnLongClick?.invoke(it, position)
          }
    }
    true
  }

  /** @author Aidan Follestad (@afollestad) */
  data class ChildClickData<in IT : Any, VB : ViewBinding, VT : View>(
    val viewBindingType: Class<VB>,
    val child: (viewBinding: VB) -> VT,
    val callback: ChildViewClickListener<IT, VT>
  )
}
