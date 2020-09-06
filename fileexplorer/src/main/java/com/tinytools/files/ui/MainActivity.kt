package com.tinytools.files.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.tinytools.common.activities.ThemedActivity
import com.tinytools.common.views.DrawerView
import com.tinytools.files.R
import com.tinytools.files.databinding.MainActivityBinding
import com.tinytools.files.ui.files.fragment.FilesFragment

class MainActivity : ThemedActivity<MainActivityBinding>(), DrawerView.DrawerHandler {
    private lateinit var toggle: ActionBarDrawerToggle

    override fun getViewBinding() = MainActivityBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.nav_app_bar_open_drawer_description, R.string.nav_app_bar_navigate_up_description)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }
    }

    override fun onResume() {
        super.onResume()
        supportFragmentManager.beginTransaction().replace(binding.container.id, FilesFragment()).commit()

    }

    override fun onItemSelected(item: DrawerView.Item) {
        swapColors()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return false
    }

    override fun onDrawerConfigurationChanged(configuration: DrawerView.Configuration) {
        binding.drawer.reloadConfiguration(configuration.apply { handler = this@MainActivity })
    }

    override fun onBackPressed() {
        if (binding.root.isDrawerOpen(GravityCompat.START)) {
            binding.root.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
