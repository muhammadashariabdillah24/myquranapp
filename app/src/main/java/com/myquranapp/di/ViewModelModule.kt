package com.myquranapp.di

import com.myquranapp.ui.detail.DetailViewModel
import com.myquranapp.ui.home.HomeViewModel
import com.myquranapp.ui.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { DetailViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
}
