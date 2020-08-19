package com.tinytools.common.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.tinytools.common.R

open class BasicSettingsFragment : PreferenceFragmentCompat() {
    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { p0, p1 ->
        onPreferenceChanged(p0, p1)
    }

    private lateinit var handler: SettingsHandler

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is SettingsHandler){
            handler = context
        } else error("Activity needs to implement ${SettingsHandler::class.simpleName}")
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.color_settings, rootKey)
        addAdditionalPreferences()
    }

    override fun onResume() {
        super.onResume()
        PreferenceManager.getDefaultSharedPreferences(requireContext()).registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    override fun onPause() {
        super.onPause()
        PreferenceManager.getDefaultSharedPreferences(requireContext()).unregisterOnSharedPreferenceChangeListener(preferenceListener)
    }

    open fun addAdditionalPreferences() {

    }

    open fun onPreferenceChanged(preferences: SharedPreferences, key: String) {
        when (key) {
            getString(R.string.primary_color_preference_key) -> handler.onColorPicked(ColorType.Primary, preferences.getInt(key, -1))
            getString(R.string.secondary_color_preference_key) -> handler.onColorPicked(ColorType.Secondary, preferences.getInt(key, -1))
        }
    }

    enum class ColorType {
        Primary, Secondary
    }

    interface SettingsHandler {
        fun onColorPicked(colorType: ColorType, color: Int)
    }
}
