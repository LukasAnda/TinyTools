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
package com.tinytools.common.recyclical.internal

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.AttrRes
import androidx.core.view.ViewCompat
import com.tinytools.common.R

internal fun Context.resolveDrawable(
  @AttrRes attr: Int? = null,
  fallback: Drawable? = null
): Drawable? {
  if (attr != null) {
    val a = theme.obtainStyledAttributes(intArrayOf(attr))
    try {
      var d = a.getDrawable(0)
      if (d == null && fallback != null) {
        d = fallback
      }
      return d
    } finally {
      a.recycle()
    }
  }
  return fallback
}

internal fun View?.onAttach(block: View.() -> Unit) {
  this?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
    override fun onViewDetachedFromWindow(v: View) = Unit

    override fun onViewAttachedToWindow(v: View) = v.block()
  })
  if (isAttachedToWindowCompat && this != null) {
    block.invoke(this)
  }
}

internal fun View?.onDetach(block: View.() -> Unit) {
  this?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
    override fun onViewDetachedFromWindow(v: View) = v.block()

    override fun onViewAttachedToWindow(v: View) = Unit
  })
  if (!isAttachedToWindowCompat && this != null) {
    block.invoke(this)
  }
}

internal val View?.isAttachedToWindowCompat: Boolean
  get() {
    return if (this == null) {
      false
    } else {
      ViewCompat.isAttachedToWindow(this)
    }
  }

internal fun View?.makeBackgroundSelectable() {
  if (this != null && background == null && context != null) {
    background = context.resolveDrawable(R.attr.selectableItemBackground)
  }
}
