package com.tinytools.files.di

import com.tinytools.files.ui.files.viewmodel.FilesFragmentViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { FilesFragmentViewModel(androidApplication()) }
}
