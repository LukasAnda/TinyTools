package com.tinytools.common.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.markodevcic.peko.PermissionResult
import com.markodevcic.peko.requestPermissionsAsync
import kotlinx.coroutines.launch

abstract class BaseDialogFragment<VIEW_BINDING : ViewBinding> : BottomSheetDialogFragment() {
    var binding: VIEW_BINDING? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = getViewBinding()
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    abstract fun getViewBinding(): VIEW_BINDING

}
