package com.edu.achadosufc.di

import com.edu.achadosufc.data.service.*
import com.edu.achadosufc.BuildConfig
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    val baseUrl = BuildConfig.BASE_URL

    single {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single { get<Retrofit>().create(UserService::class.java) }
    single { get<Retrofit>().create(ItemService::class.java) }
    single { get<Retrofit>().create(AuthService::class.java) }
    single { get<Retrofit>().create(FileService::class.java) }
}