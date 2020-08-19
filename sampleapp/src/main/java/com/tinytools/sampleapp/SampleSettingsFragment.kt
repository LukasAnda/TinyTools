package com.tinytools.sampleapp

import com.tinytools.common.fragments.BasicSettingsFragment

class SampleSettingsFragment : BasicSettingsFragment(){
    override fun addAdditionalPreferences() {
        super.addAdditionalPreferences()

        addPreferencesFromResource(R.xml.settings)
    }
}
