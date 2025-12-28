package com.asad.currency.di

import com.asad.currency.data.PreferencesImpl
import com.asad.currency.data.remote.api.CurrencyApiServiceImpl
import com.asad.currency.domain.CurrencyApiService
import com.asad.currency.domain.PreferencesRepository
import com.asad.currency.presentation.screen.HomeViewModel
import com.russhwolf.settings.Settings
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule = module {
    single<Settings> { Settings() }
    single<PreferencesRepository> { PreferencesImpl(settings = get()) }
    single<CurrencyApiService> { CurrencyApiServiceImpl(preferences = get()) }
    factory {
        HomeViewModel(
            preferences = get(),
            api = get()
        )
    }
}

fun initializeKoin() {
    startKoin {
        modules(
            appModule,
            getCoreDatabaseModule()
        )
    }
}