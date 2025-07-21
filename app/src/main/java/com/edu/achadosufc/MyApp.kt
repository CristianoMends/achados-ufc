package com.edu.achadosufc

import android.app.Application
import com.edu.achadosufc.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MyApp)
            workManagerFactory()

            modules(
                networkModule,
                repositoryModule,
                databaseModule,
                viewModelModule,
                workerModule
            )
        }
    }
}