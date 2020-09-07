package com.tinytools.common.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.markodevcic.peko.PermissionResult
import com.markodevcic.peko.requestPermissionsAsync
import com.tinytools.common.views.DrawerView
import kotlinx.coroutines.launch

abstract class BaseFragment<VIEW_BINDING : ViewBinding> : Fragment() {
    var binding: VIEW_BINDING? = null
    private lateinit var configHandler: DrawerConfigurationHandler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = getViewBinding()
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onAttach(context: Context) {
        if(context is DrawerConfigurationHandler){
            configHandler = context
        } else error("Activity must extend ThemedActivity")
        super.onAttach(context)
    }

    fun requestPermissions(@StringRes deniedMessage: Int,
                           @StringRes rationaleMessage: Int,
                           @StringRes permanentlyDeniedMessage: Int,
                           permissions: List<String>, onGranted: () -> Unit) {
        lifecycleScope.launch {
            when (requestPermissionsAsync(*permissions.toTypedArray())) {
                is PermissionResult.Granted -> {
                    onGranted()
                }
                is PermissionResult.Denied.JustDenied -> {
                    showErrorDialog(deniedMessage)
                }
                is PermissionResult.Denied.NeedsRationale -> {
                    showInfoDialog(rationaleMessage)
                }
                is PermissionResult.Denied.DeniedPermanently -> {
                    showErrorDialog(permanentlyDeniedMessage)
                }
                PermissionResult.Cancelled -> {
                    showErrorDialog(deniedMessage)
                }
            }
        }
    }

    fun requestAfterSaf() {

    }

    open fun showInfoDialog(message: Int) {

    }

    open fun showErrorDialog(message: Int) {

    }

    abstract fun getViewBinding(): VIEW_BINDING

    fun setConfiguration(configuration: DrawerView.Configuration){
        configHandler.onConfigurationChanged(configuration)
    }

    interface DrawerConfigurationHandler{
        fun onConfigurationChanged(configuration: DrawerView.Configuration)
    }


}
