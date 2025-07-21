package com.edu.achadosufc.di

import com.edu.achadosufc.data.dao.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single { AppDatabase.getDatabase(androidContext()) }
    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().itemDao() }
    single { get<AppDatabase>().messageDao() }
}