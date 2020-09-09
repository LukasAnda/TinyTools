package com.tinytools.common.activities

import android.R
import android.content.Intent
import android.graphics.Color
import android.media.VolumeShaper
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.tinytools.common.fragments.BaseFragment
import com.tinytools.common.fragments.BasicSettingsFragment
import com.tinytools.common.fragments.BasicSettingsFragment.ColorType.Primary
import com.tinytools.common.fragments.BasicSettingsFragment.ColorType.Secondary
import com.tinytools.common.views.DrawerView
import xyz.aprildown.theme.Theme
import kotlin.random.Random


abstract class ThemedActivity<BINDING : ViewBinding> : AppCompatActivity(), BasicSettingsFragment.SettingsHandler {

    val binding by lazy {
        getViewBinding()
    }

    abstract fun getViewBinding(): BINDING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Theme.tintSystemUi(this)
        setContentView(binding.root)
    }

    fun swapColors() {
        Theme.edit(this) {
            colorPrimary = randomColor
            colorPrimaryVariant = darker(colorPrimary)
            colorStatusBar = colorPrimaryVariant
            colorOnPrimary = Color.BLACK
            colorSecondary = randomColor
            colorSecondaryVariant = darker(colorSecondary)
            colorOnSecondary = Color.BLACK
            lightStatusByPrimary = true
            colorNavigationBar = if (Random.nextBoolean()) colorPrimary else null
        }
        finish()
        startActivity(Intent(this, javaClass))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    fun swapColor(colorFun: () -> Unit) {
        colorFun()
        finish()
        startActivity(Intent(this, javaClass))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun onColorPicked(colorType: BasicSettingsFragment.ColorType, color: Int) {
        when (colorType) {
            Primary -> swapColor {
                Theme.edit(this) {
                    colorPrimary = color
                    colorPrimaryVariant = darker(colorPrimary)
                    colorStatusBar = colorPrimaryVariant
                    colorOnPrimary = on(colorPrimary)
                }
            }
            Secondary -> swapColor {
                Theme.edit(this) {
                    colorSecondary = color
                    colorSecondaryVariant = darker(colorSecondary)
                    colorOnSecondary = on(colorSecondary)
                }
            }
        }
    }

    open fun onDrawerConfigurationChanged(configuration: DrawerView.Configuration){}

    private val randomColor: Int
        get() = Color.rgb(Random.nextInt(), Random.nextInt(), Random.nextInt())
}
