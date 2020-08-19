package com.tinytools.sampleapp

import android.os.Bundle
import com.tinytools.common.activities.ThemedActivity
import com.tinytools.sampleapp.databinding.MainActivityBinding

class MainActivity : ThemedActivity<MainActivityBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.fab.setOnClickListener {
            swapColors()
        }

        supportFragmentManager.beginTransaction().replace(R.id.container, SampleFragment()).commit()
    }

    override fun getViewBinding() = MainActivityBinding.inflate(layoutInflater)
}
