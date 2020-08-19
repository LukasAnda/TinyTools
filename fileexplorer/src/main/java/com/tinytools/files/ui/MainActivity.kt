package com.tinytools.files.ui

import com.tinytools.common.activities.ThemedActivity
import com.tinytools.files.databinding.MainActivityBinding
import com.tinytools.files.ui.files.fragment.FilesFragment

class MainActivity : ThemedActivity<MainActivityBinding>() {
    override fun getViewBinding() = MainActivityBinding.inflate(layoutInflater)

    override fun onResume() {
        super.onResume()

        supportFragmentManager.beginTransaction().replace(binding.container.id, FilesFragment()).commit()

    }
}
