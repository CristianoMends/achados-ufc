package com.edu.achadosufc.di

import ChatSocketService
import com.edu.achadosufc.data.SessionManager
import com.edu.achadosufc.data.UserPreferences
import com.edu.achadosufc.data.repository.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single { UserRepository(get(), get()) }
    single { LoginRepository(androidContext(), get()) }
    single { UserPreferences(androidContext()) }
    single { FileRepository(get()) }
    single { ItemRepository(get(), get()) }
    single { SessionManager(androidContext()) }
    single { ChatSocketService(androidContext()) }
    single { ChatRepository(get(), get()) }
}