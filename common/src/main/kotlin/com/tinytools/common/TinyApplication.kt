package com.tinytools.common

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import xyz.aprildown.theme.Theme

open class TinyApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        Theme.init(context = this, themeRes = R.style.AppTheme)

        startKoin {
            modules(modules + baseModules)
            androidLogger(if(BuildConfig.DEBUG) Level.DEBUG else Level.INFO)
            androidContext(this@TinyApplication)
        }
    }

    open val modules = listOf<Module>()
    val baseModules = listOf<Module>()
}
