package com.tinytools.sampleapp

import android.os.Bundle
import android.view.View
import com.tinytools.common.fragments.BaseFragment
import com.tinytools.sampleapp.databinding.SampleFragmentBinding
import java.util.jar.Manifest

class SampleFragment : BaseFragment<SampleFragmentBinding>(){
    override fun getViewBinding(): SampleFragmentBinding = SampleFragmentBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.button?.setOnClickListener {
            requestPermissions(R.string.app_name, R.string.app_name, R.string.app_name, listOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                println("Success!!!")
            }
        }
    }
}
