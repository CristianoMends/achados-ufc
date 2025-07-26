package com.edu.achadosufc.di

import com.edu.achadosufc.viewModel.*
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { ThemeViewModel(get()) }
    viewModel { LoginViewModel(get(), get(), get(), androidContext()) }
    viewModel { HomeViewModel(get()) }
    viewModel { UserViewModel(get(), get(), get(), androidContext()) }
    viewModel { ItemViewModel(get()) }
    viewModel { ReportViewModel(androidContext()) }
    viewModel { ChatViewModel(get(), get()) }
    viewModel { SignUpViewModel(get(), get(), androidContext()) }
}