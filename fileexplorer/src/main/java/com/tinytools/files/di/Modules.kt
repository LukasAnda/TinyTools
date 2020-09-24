package com.tinytools.files.di

import com.tinytools.files.data.db.AppDatabase
import com.tinytools.files.repository.PageStyleRepository
import com.tinytools.files.ui.MainActivityViewModel
import com.tinytools.files.ui.files.viewmodel.FilesFragmentViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { FilesFragmentViewModel(androidApplication(), get()) }
    viewModel { MainActivityViewModel(androidApplication()) }
}

val dbModule = module {
    single { AppDatabase.getInstance(androidApplication()) }
    single(createdAtStart = false) { get<AppDatabase>().getPageStyleDao() }
}

val repositoryModule = module {
    single { PageStyleRepository(get()) }
}
