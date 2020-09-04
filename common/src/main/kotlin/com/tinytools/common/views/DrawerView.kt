package com.tinytools.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import com.tinytools.common.recyclical.setup
import com.tinytools.common.databinding.DrawerViewBinding

class DrawerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayoutCompat(context, attrs, defStyleAttr){
    private val binding = DrawerViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        bindViews()
    }

    fun bindViews(){
        binding.recycler.setup {
        }
    }
}

