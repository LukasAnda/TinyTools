package com.tinytools.files.ui

import android.os.Bundle
import com.tinytools.common.activities.ThemedActivity
import com.tinytools.files.databinding.MainActivityBinding
import com.tinytools.files.ui.files.fragment.FilesFragment

class MainActivity : ThemedActivity<MainActivityBinding>() {
    override fun getViewBinding() = MainActivityBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
    }

    override fun onResume() {
        super.onResume()
        supportFragmentManager.beginTransaction().replace(binding.container.id, FilesFragment()).commit()

    }
}
