package com.tinytools.files

import com.tinytools.common.TinyApplication
import com.tinytools.files.di.viewModelsModule
import org.koin.core.module.Module

class FilesApplication : TinyApplication(){
    override val modules: List<Module>
        get() = mutableListOf(viewModelsModule)
}
