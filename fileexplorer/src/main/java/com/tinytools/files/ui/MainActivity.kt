package com.tinytools.files.ui

import android.os.Bundle
import com.tinytools.common.activities.ThemedActivity
import com.tinytools.files.databinding.MainActivityBinding
import com.tinytools.files.ui.files.fragment.FilesFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ThemedActivity<MainActivityBinding>() {
    private val viewModel by viewModel<MainActivityViewModel>()

    override fun getViewBinding() = MainActivityBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        supportFragmentManager.beginTransaction().replace(binding.container.id, FilesFragment()).commit()

    }
}
