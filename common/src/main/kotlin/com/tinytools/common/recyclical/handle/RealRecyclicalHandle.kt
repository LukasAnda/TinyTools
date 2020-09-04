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
package com.tinytools.common.recyclical.handle

import android.os.Looper.getMainLooper
import android.os.Looper.myLooper
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.tinytools.common.recyclical.datasource.DataSource
import com.tinytools.common.recyclical.internal.DefinitionAdapter
import com.tinytools.common.recyclical.itemdefinition.ItemGraph

/** @author Aidan Follestad (@afollestad) */
class RealRecyclicalHandle internal constructor(
        internal val emptyView: View?,
        private val adapter: DefinitionAdapter,
        val dataSource: DataSource<*>,
        @VisibleForTesting internal val itemGraph: ItemGraph
) : RecyclicalHandle {

  override fun showOrHideEmptyView(show: Boolean) {
    check(myLooper() == getMainLooper()) {
      "DataSource interaction must be done on the main (UI) thread."
    }
    emptyView?.visibility = if (show) View.VISIBLE else View.GONE
  }

  override fun getAdapter(): Adapter<*> = adapter

  override fun invalidateList(block: Adapter<*>.() -> Unit) {
    check(myLooper() == getMainLooper()) {
      "DataSource interaction must be done on the main (UI) thread."
    }
    getAdapter().block()
    showOrHideEmptyView(dataSource.isEmpty())
  }

  override fun itemGraph(): ItemGraph = itemGraph

  internal fun attachDataSource() {
    dataSource.attach(this)
    adapter.attach(this)
  }

  internal fun detachDataSource() {
    dataSource.detach()
    adapter.detach()
  }
}
