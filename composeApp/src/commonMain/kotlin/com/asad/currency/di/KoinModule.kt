package com.asad.currency.di

import com.asad.currency.data.local.DatabaseRepositoryImpl
import com.asad.currency.data.local.PreferencesImpl
import com.asad.currency.data.local.database.CurrencyDatabase
import com.asad.currency.data.remote.api.CurrencyApiServiceImpl
import com.asad.currency.domain.CurrencyApiService
import com.asad.currency.domain.DatabaseRepository
import com.asad.currency.domain.PreferencesRepository
import com.asad.currency.presentation.screen.HomeViewModel
import com.russhwolf.settings.Settings
import org.koin.dsl.module

val appModule = module {
    single<Settings> { Settings() }
    single<PreferencesRepository> { PreferencesImpl(settings = get()) }
    single<CurrencyApiService> { CurrencyApiServiceImpl(preferences = get()) }
    single<DatabaseRepository> { DatabaseRepositoryImpl(database = get()) }

    factory {
        HomeViewModel(
            preferences = get(),
            api = get(),
            dbRepository = get()
        )
    }
}

val databaseModule = module {
    single<CurrencyDatabase> {
        CurrencyDatabase(
            driver = get()
        )
    }
}