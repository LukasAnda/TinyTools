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
@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.tinytools.common.recyclical.handle

import androidx.recyclerview.widget.RecyclerView.Adapter
import com.tinytools.common.recyclical.datasource.DataSource
import com.tinytools.common.recyclical.itemdefinition.ItemGraph

typealias AdapterBlock = Adapter<*>.() -> Unit

/**
 * Represents a handle to Recyclical as it is setup and manipulating
 * a RecyclerView. Provides utility functions to be used by [DataSource]'s.
 *
 * @author Aidan Follestad (@afollestad)
 */
interface RecyclicalHandle {
  /** Shows the empty view if [show] is true, else hides it. */
  fun showOrHideEmptyView(show: Boolean)

  /** Gets the underlying adapter for the RecyclerView. */
  fun getAdapter(): Adapter<*>

  /**
   * Executes code in the given [block] on the current adapter,
   * then invalidates whether the empty view is visible or not
   * based on [DataSource.isEmpty].
   */
  fun invalidateList(block: AdapterBlock)

  /**
   * Gets the item graph that's responsible for mapping item
   * classes, layouts, and definitions.
   */
  fun itemGraph(): ItemGraph
}

/** Gets the current data source, auto casting it to [T]. */
inline fun <reified T : DataSource<*>> RecyclicalHandle.getDataSource(): T {
  return if (this is RealRecyclicalHandle) {
    dataSource as? T ?: error("$dataSource is not a ${T::class.java.name}")
  } else {
    error("Handle is not a real implementation.")
  }
}
