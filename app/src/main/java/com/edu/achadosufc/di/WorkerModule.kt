package com.edu.achadosufc.di

import com.edu.achadosufc.worker.ReportUploadWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val workerModule = module {
    worker { ReportUploadWorker(androidContext(), get(), get(), get()) }
}